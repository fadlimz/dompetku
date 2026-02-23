package com.fadlimz.dompetku.master.account.services;

import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.account.dtos.AccountDto;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.account.repositories.AccountRepository;
import com.fadlimz.dompetku.base.services.BaseService;
import com.fadlimz.dompetku.master.user.entities.User;
import com.fadlimz.dompetku.master.user.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountService extends BaseService<Account> {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        super(accountRepository);
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Account create(AccountDto dto) {
        User user = userService.getLoggedInUser();

        // Auto-generate accountCode from accountName if not provided
        String accountCode = dto.getAccountCode();
        if (StringUtil.isBlank(accountCode) && !StringUtil.isBlank(dto.getAccountName())) {
            accountCode = generateCode(dto.getAccountName(), user);
        }

        // Check duplicate
        if (accountRepository.findByAccountCodeAndUser(accountCode, user).isPresent()) {
            throw new RuntimeException("Account Code already exists for this user.");
        }

        // Use toEntity from DTO
        Account account = dto.toEntity();
        account.setAccountCode(accountCode);
        account.setUser(user);

        // BaseService.save handles auditing and versioning
        return save(account);
    }

    public Account update(String id, AccountDto dto) {
        User user = userService.getLoggedInUser();
        
        // Reuse findById (which we will override to be secure) or check manually
        // Since we override findById below to be secure, we can trust it.
        Account existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Check duplicate if code changes
        if (!existing.getAccountCode().equals(dto.getAccountCode())) {
             if (accountRepository.findByAccountCodeAndUser(dto.getAccountCode(), user).isPresent()) {
                throw new RuntimeException("Account Code already exists for this user.");
            }
        }

        // Use Setters for updates
        existing.setAccountCode(dto.getAccountCode());
        existing.setAccountName(dto.getAccountName());
        existing.setActiveFlag(dto.getActiveFlag());
        
        // BaseService.update handles auditing
        return update(existing);
    }

    @Override
    public void delete(String id) {
        // Verify existence and ownership
        findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        super.delete(id);
    }

    // Override findById to enforce User Isolation
    // This satisfies "filter user mutlak" while keeping the BaseService method signature
    @Override
    public Optional<Account> findById(String id) {
        return accountRepository.findByIdAndUser(id, userService.getLoggedInUser());
    }

    // Override findAll to enforce User Isolation
    @Override
    public List<Account> findAll() {
        return accountRepository.findByUser(userService.getLoggedInUser());
    }

    public Optional<Account> findByAccountCode(String accountCode) {
        return accountRepository.findByAccountCodeAndUser(accountCode, userService.getLoggedInUser());
    }

    public List<Account> search(String keyword) {
        User user = userService.getLoggedInUser();
        if (StringUtil.isBlank(keyword)) {
            return findAll();
        }
        return accountRepository.search(user, keyword);
    }

    /**
     * Generates a unique code from a name string.
     * Format: trim, toUpperCase, replace spaces with underscore
     * @param name the source name
     * @param user the current user (for user-specific uniqueness)
     * @return unique code
     */
    private String generateCode(String name, User user) {
        String baseCode = name.trim().toUpperCase().replaceAll("\\s+", "_");
        return findUniqueCode(baseCode, user);
    }

    /**
     * Finds a unique code by adding suffix if necessary.
     * @param baseCode the base code to check
     * @param user the current user
     * @return unique code with suffix if needed
     */
    private String findUniqueCode(String baseCode, User user) {
        String uniqueCode = baseCode;
        int counter = 1;

        while (accountRepository.findByAccountCodeAndUser(uniqueCode, user).isPresent()) {
            uniqueCode = baseCode + "_" + counter;
            counter++;
        }

        return uniqueCode;
    }
}
