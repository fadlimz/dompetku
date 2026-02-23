package com.fadlimz.dompetku.transactions.dailyCash.services;

import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.balances.services.AccountBalanceService;
import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.account.services.AccountService;
import com.fadlimz.dompetku.master.category.entities.Category;
import com.fadlimz.dompetku.master.category.services.CategoryService;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import com.fadlimz.dompetku.master.transactionCode.services.TransactionCodeService;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import com.fadlimz.dompetku.transactions.dailyCash.dtos.DailyCashDto;
import com.fadlimz.dompetku.transactions.dailyCash.entities.DailyCash;
import com.fadlimz.dompetku.transactions.dailyCash.repositories.DailyCashRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DailyCashService extends BaseService<DailyCash> {

    private final DailyCashRepository dailyCashRepository;
    private final UserService userService;
    private final TransactionCodeService transactionCodeService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final AccountBalanceService accountBalanceService;

    public DailyCashService(DailyCashRepository dailyCashRepository,
                            UserService userService,
                            TransactionCodeService transactionCodeService,
                            CategoryService categoryService,
                            AccountService accountService,
                            AccountBalanceService accountBalanceService) {
        super(dailyCashRepository);
        this.dailyCashRepository = dailyCashRepository;
        this.userService = userService;
        this.transactionCodeService = transactionCodeService;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.accountBalanceService = accountBalanceService;
    }

    public DailyCash create(DailyCashDto dto) {
        User user = userService.getLoggedInUser();
        DailyCash entity = dto.toEntity();

        // Handle TransactionCode reference
        if (dto.getTransactionCode() != null) {
            TransactionCode transactionCode = resolveTransactionCode(dto.getTransactionCode());
            entity.setTransactionCode(transactionCode);
        }

        // Handle Category reference
        if (dto.getCategory() != null) {
            Category category = resolveCategory(dto.getCategory());
            entity.setCategory(category);
        }

        // Handle Account reference
        if (dto.getAccount() != null) {
            Account account = resolveAccount(dto.getAccount());
            entity.setAccount(account);
            if (account != null) {
                adjustBalance(account, entity.getValue(), entity.getCashflowFlag(), false);
            }
        }

        entity.setUser(user);
        return save(entity);
    }

    public DailyCash update(String id, DailyCashDto dto) {
        DailyCash before = findById(id)
                .orElseThrow(() -> new RuntimeException("Daily Cash not found"));

        // 1. Revert balance before
        if (before.getAccount() != null) {
            adjustBalance(before.getAccount(), before.getValue(), before.getCashflowFlag(), true);
        }

        // 2. Prepare updated entity
        DailyCash entity = dto.toEntity();
        entity.setId(id);

        // Handle TransactionCode reference
        if (dto.getTransactionCode() != null) {
            TransactionCode transactionCode = resolveTransactionCode(dto.getTransactionCode());
            entity.setTransactionCode(transactionCode);
        }

        // Handle Category reference
        if (dto.getCategory() != null) {
            Category category = resolveCategory(dto.getCategory());
            entity.setCategory(category);
        }

        // Handle Account reference
        if (dto.getAccount() != null) {
            Account accountAfter = resolveAccount(dto.getAccount());
            entity.setAccount(accountAfter);
            // 3. Apply balance after
            if (accountAfter != null) {
                adjustBalance(accountAfter, entity.getValue(), entity.getCashflowFlag(), false);
            }
        }

        entity.setUser(userService.getLoggedInUser());
        return update(entity);
    }

    @Override
    public void delete(String id) {
        DailyCash entity = findById(id)
                .orElseThrow(() -> new RuntimeException("Daily Cash not found"));

        // Revert balance before deleting
        if (entity.getAccount() != null) {
            adjustBalance(entity.getAccount(), entity.getValue(), entity.getCashflowFlag(), true);
        }

        super.delete(id);
    }

    private TransactionCode resolveTransactionCode(com.fadlimz.dompetku.master.transactionCode.dtos.TransactionCodeDto dto) {
        if (!StringUtil.isBlank(dto.getId())) {
            // If ID is provided, find by ID
            return transactionCodeService.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("TransactionCode not found with id: " + dto.getId()));
        } else if (!StringUtil.isBlank(dto.getTransactionCode())) {
            // If no ID but code is provided, find by code
            return transactionCodeService.findByTransactionCode(dto.getTransactionCode())
                    .orElseThrow(() -> new IllegalArgumentException("TransactionCode not found with code: " + dto.getTransactionCode()));
        }
        return null;
    }

    private Category resolveCategory(com.fadlimz.dompetku.master.category.dtos.CategoryDto dto) {
        if (!StringUtil.isBlank(dto.getId())) {
            // If ID is provided, find by ID
            return categoryService.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getId()));
        } else if (!StringUtil.isBlank(dto.getCategoryCode())) {
            // If no ID but code is provided, find by code
            return categoryService.findByCategoryCode(dto.getCategoryCode())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with code: " + dto.getCategoryCode()));
        }
        return null;
    }

    private Account resolveAccount(com.fadlimz.dompetku.master.account.dtos.AccountDto dto) {
        if (!StringUtil.isBlank(dto.getId())) {
            // If ID is provided, find by ID
            return accountService.findById(dto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + dto.getId()));
        } else if (!StringUtil.isBlank(dto.getAccountCode())) {
            // If no ID but code is provided, find by code
            return accountService.findByAccountCode(dto.getAccountCode())
                    .orElseThrow(() -> new IllegalArgumentException("Account not found with code: " + dto.getAccountCode()));
        }
        return null;
    }

    private void adjustBalance(Account account, Double amount, String flag, boolean isRevert) {
        if (amount == null || StringUtil.isBlank(flag)) return;

        Optional<AccountBalance> balanceOpt = accountBalanceService.findByAccount(account.getId());
        AccountBalance balance;

        if (balanceOpt.isPresent()) {
            balance = balanceOpt.get();
        } else {
            // Jika saldo belum ada, hanya izinkan create otomatis jika cashflow-nya "In"
            // dan bukan sedang dalam proses revert (undo)
            if ("In".equalsIgnoreCase(flag) && !isRevert) {
                balance = new AccountBalance();
                balance.setAccount(account);
                balance.setUser(userService.getLoggedInUser());
                balance.setValue(0.0);
            } else {
                throw new RuntimeException("Account balance not found for account: " + account.getAccountName());
            }
        }

        double currentBalance = balance.getValue() != null ? balance.getValue() : 0.0;
        double adjustment = "In".equalsIgnoreCase(flag) ? amount : -amount;

        if (isRevert) {
            balance.setValue(currentBalance - adjustment);
        } else {
            balance.setValue(currentBalance + adjustment);
        }

        if (balance.getId() == null) {
            accountBalanceService.save(balance);
        } else {
            accountBalanceService.update(balance);
        }
    }

    @Override
    public Optional<DailyCash> findById(String id) {
        return dailyCashRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    @Override
    public List<DailyCash> findAll() {
        return dailyCashRepository.findByUser(userService.getLoggedInUser());
    }
}
