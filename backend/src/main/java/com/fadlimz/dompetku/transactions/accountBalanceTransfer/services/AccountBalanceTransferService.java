package com.fadlimz.dompetku.transactions.accountBalanceTransfer.services;

import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.account.services.AccountService;
import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.balances.services.AccountBalanceService;
import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.transactionCode.services.TransactionCodeService;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.dtos.AccountBalanceTransferDto;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.entities.AccountBalanceTransfer;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.repositories.AccountBalanceTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountBalanceTransferService extends BaseService<AccountBalanceTransfer> {

    private final AccountBalanceTransferRepository transferRepository;
    private final UserService userService;
    private final TransactionCodeService transactionCodeService;
    private final AccountService accountService; // Tambahkan AccountService
    private final AccountBalanceService balanceService;

    public AccountBalanceTransferService(AccountBalanceTransferRepository transferRepository,
                                         UserService userService,
                                         TransactionCodeService transactionCodeService,
                                         AccountService accountService, // Tambahkan parameter
                                         AccountBalanceService balanceService) {
        super(transferRepository);
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.transactionCodeService = transactionCodeService;
        this.accountService = accountService; // Inisialisasi
        this.balanceService = balanceService;
    }

    public AccountBalanceTransfer create(AccountBalanceTransferDto dto) {
        User user = userService.getLoggedInUser();
        AccountBalanceTransfer entity = dto.toEntity();

        if (!StringUtil.isBlank(dto.getTransactionCodeId())) {
            entity.setTransactionCode(transactionCodeService.findById(dto.getTransactionCodeId())
                    .orElseThrow(() -> new IllegalArgumentException("TransactionCode not found with id: " + dto.getTransactionCodeId())));
        }

        if (!StringUtil.isBlank(dto.getFromAccountId())) {
            entity.setAccountFrom(accountService.findById(dto.getFromAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("FromAccount not found with id: " + dto.getFromAccountId())));
        }

        if (!StringUtil.isBlank(dto.getToAccountId())) {
            entity.setAccountTo(accountService.findById(dto.getToAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("ToAccount not found with id: " + dto.getToAccountId())));
        }

        // Apply balances
        adjustBalances(entity.getAccountFrom(), entity.getAccountTo(), entity.getValue(), false);

        entity.setUser(user);
        return save(entity);
    }

    public AccountBalanceTransfer update(String id, AccountBalanceTransferDto dto) {
        AccountBalanceTransfer before = findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        // 1. Revert balances before
        adjustBalances(before.getAccountFrom(), before.getAccountTo(), before.getValue(), true);

        // 2. Prepare updated entity
        AccountBalanceTransfer entity = dto.toEntity();
        entity.setId(id);

        if (!StringUtil.isBlank(dto.getTransactionCodeId())) {
            entity.setTransactionCode(transactionCodeService.findById(dto.getTransactionCodeId())
                    .orElseThrow(() -> new IllegalArgumentException("TransactionCode not found with id: " + dto.getTransactionCodeId())));
        }

        if (!StringUtil.isBlank(dto.getFromAccountId())) {
            entity.setAccountFrom(accountService.findById(dto.getFromAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("FromAccount not found with id: " + dto.getFromAccountId())));
        }

        if (!StringUtil.isBlank(dto.getToAccountId())) {
            entity.setAccountTo(accountService.findById(dto.getToAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("ToAccount not found with id: " + dto.getToAccountId())));
        }

        // 3. Apply balances after
        adjustBalances(entity.getAccountFrom(), entity.getAccountTo(), entity.getValue(), false);

        entity.setUser(userService.getLoggedInUser());
        return update(entity);
    }

    @Override
    public void delete(String id) {
        AccountBalanceTransfer entity = findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        // Revert balances before deleting
        adjustBalances(entity.getAccountFrom(), entity.getAccountTo(), entity.getValue(), true); // Gunakan accountFrom, accountTo

        super.delete(id);
    }

    private void adjustBalances(Account from,
                                Account to,
                                Double amount, boolean isRevert) {
        if (amount == null) return;
        User currentUser = userService.getLoggedInUser(); // Ambil user saat ini

        if (from != null) {
            // Cari AccountBalance berdasarkan Account
            Optional<AccountBalance> fromBalanceOpt = balanceService.findByAccount(from);
            if (fromBalanceOpt.isPresent()) {
                AccountBalance fromBalance = fromBalanceOpt.get();
                double currentFrom = fromBalance.getValue() != null ? fromBalance.getValue() : 0.0;
                fromBalance.setValue(isRevert ? currentFrom + amount : currentFrom - amount);
                balanceService.update(fromBalance);
            } else {
                // Jika tidak ditemukan, mungkin perlu throw exception atau log, karena tidak bisa mengurangi dari akun tanpa saldo
                // Untuk saat ini, kita asumsikan akun sumber pasti memiliki saldo, jadi tidak dibuatkan baru.
                // Jika ternyata bisa tidak ada, maka logika ini perlu ditinjau ulang.
                // Atau mungkin transfer dari akun yang tidak memiliki balance record sama sekali?
                // Jika ini adalah kasus yang valid, maka mungkin perlu dibuat balance baru dengan nilai negatif.
                // Namun, ini bisa menimbulkan saldo negatif, jadi mungkin tidak diinginkan.
                // Kita tetap asumsikan balance harus ada sebelum transfer keluar.
            }
        }

        if (to != null) {
            // Cari AccountBalance berdasarkan Account
            Optional<AccountBalance> toBalanceOpt = balanceService.findByAccount(to);
            if (toBalanceOpt.isPresent()) {
                AccountBalance toBalance = toBalanceOpt.get();
                double currentTo = toBalance.getValue() != null ? toBalance.getValue() : 0.0;
                toBalance.setValue(isRevert ? currentTo - amount : currentTo + amount);
                balanceService.update(toBalance);
            } else {
                // Jika tidak ditemukan, buat baru
                AccountBalance toBalance = new AccountBalance();
                toBalance.setAccount(to); // Set relasi ke Account
                toBalance.setUser(currentUser); // Set user yang sedang login
                // Nilai awal saat membuat baru:
                // - Jika isRevert=false (create/update transfer baru), maka nilai awal adalah +amount (uang masuk)
                // - Jika isRevert=true (revert update/delete), maka nilai awal adalah -amount (uang "keluar" dari akun tujuan kembali ke asal)
                toBalance.setValue(isRevert ? -amount : amount);
                balanceService.save(toBalance); // Simpan entitas baru, tidak perlu update lagi
            }
        }
    }

    @Override
    public Optional<AccountBalanceTransfer> findById(String id) {
        return transferRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    @Override
    public List<AccountBalanceTransfer> findAll() {
        return transferRepository.findByUser(userService.getLoggedInUser());
    }
}
