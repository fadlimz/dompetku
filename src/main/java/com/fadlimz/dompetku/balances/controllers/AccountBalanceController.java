package com.fadlimz.dompetku.balances.controllers;

import com.fadlimz.dompetku.balances.dtos.AccountBalanceDto;
import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.balances.services.AccountBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/balances")
@RequiredArgsConstructor
public class AccountBalanceController {

    private final AccountBalanceService accountBalanceService;

    @GetMapping
    public ResponseEntity<List<AccountBalanceDto>> getAll() {
        List<AccountBalance> balances = accountBalanceService.findAll();
        return ResponseEntity.ok(AccountBalanceDto.fromEntityList(balances));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountBalanceDto> getById(@PathVariable String id) {
        return accountBalanceService.findById(id)
                .map(AccountBalanceDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountBalanceDto> getByAccountId(@PathVariable String accountId) {
        return accountBalanceService.findByAccount(accountId)
                .map(AccountBalanceDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountBalanceDto> create(@RequestBody AccountBalanceDto dto) {
        AccountBalance created = accountBalanceService.create(dto);
        return ResponseEntity.ok(AccountBalanceDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountBalanceDto> update(@PathVariable String id, @RequestBody AccountBalanceDto dto) {
        AccountBalance updated = accountBalanceService.update(id, dto);
        return ResponseEntity.ok(AccountBalanceDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        accountBalanceService.delete(id);
        return ResponseEntity.ok().build();
    }
}
