package com.fadlimz.dompetku.master.account.controllers;

import com.fadlimz.dompetku.master.account.dtos.AccountDto;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.account.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllOrSearch(@RequestParam(required = false) String keyword) {
        List<Account> accounts = accountService.search(keyword);
        return ResponseEntity.ok(AccountDto.fromEntityList(accounts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getById(@PathVariable String id) {
        return accountService.findById(id)
                .map(AccountDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto dto) {
        Account created = accountService.create(dto);
        return ResponseEntity.ok(AccountDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> update(@PathVariable String id, @RequestBody AccountDto dto) {
        Account updated = accountService.update(id, dto);
        return ResponseEntity.ok(AccountDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountService.delete(id);
        return ResponseEntity.ok().build();
    }
}
