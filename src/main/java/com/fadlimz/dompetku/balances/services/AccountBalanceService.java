package com.fadlimz.dompetku.balances.services;

import com.fadlimz.dompetku.balances.dtos.AccountBalanceDto;
import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.balances.repositories.AccountBalanceRepository;
import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.account.services.AccountService;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountBalanceService extends BaseService<AccountBalance> {

    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountService accountService;
    private final UserService userService;

    public AccountBalanceService(AccountBalanceRepository accountBalanceRepository, 
                                 AccountService accountService, 
                                 UserService userService) {
        super(accountBalanceRepository);
        this.accountBalanceRepository = accountBalanceRepository;
        this.accountService = accountService;
        this.userService = userService;
    }

    public AccountBalance create(AccountBalanceDto dto) {
        User user = userService.getLoggedInUser();
        
        Account account = accountService.findById(dto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found or access denied"));

        // Check if balance already exists for this account
        if (accountBalanceRepository.findByAccountAndUser(account, user).isPresent()) {
            throw new RuntimeException("Balance already exists for this account.");
        }

        AccountBalance balance = dto.toEntity();
        balance.setAccount(account);
        balance.setUser(user);
        
        return save(balance);
    }

    public AccountBalance update(String id, AccountBalanceDto dto) {
        AccountBalance existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Balance not found"));

        existing.setValue(dto.getValue());
        
        // Note: accountId modification usually not allowed for balances, 
        // but if needed, we'd add logic here similar to create.

        return update(existing);
    }

    @Override
    public Optional<AccountBalance> findById(String id) {
        return accountBalanceRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    @Override
    public List<AccountBalance> findAll() {
        return accountBalanceRepository.findByUser(userService.getLoggedInUser());
    }

    public Optional<AccountBalance> findByAccount(String accountId) {
        Account account = accountService.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountBalanceRepository.findByAccountAndUser(account, userService.getLoggedInUser());
    }

    public Optional<AccountBalance> findByAccount(Account account) {
        return accountBalanceRepository.findByAccountAndUser(account, userService.getLoggedInUser());
    }
}
