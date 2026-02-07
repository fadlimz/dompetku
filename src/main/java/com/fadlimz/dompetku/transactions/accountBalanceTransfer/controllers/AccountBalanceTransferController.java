package com.fadlimz.dompetku.transactions.accountBalanceTransfer.controllers;

import com.fadlimz.dompetku.transactions.accountBalanceTransfer.dtos.AccountBalanceTransferDto;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.entities.AccountBalanceTransfer;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.services.AccountBalanceTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/transfers")
@RequiredArgsConstructor
public class AccountBalanceTransferController {

    private final AccountBalanceTransferService transferService;

    @GetMapping
    public ResponseEntity<List<AccountBalanceTransferDto>> getAll() {
        return ResponseEntity.ok(AccountBalanceTransferDto.fromEntityList(transferService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountBalanceTransferDto> getById(@PathVariable String id) {
        return transferService.findById(id)
                .map(AccountBalanceTransferDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountBalanceTransferDto> create(@RequestBody AccountBalanceTransferDto dto) {
        AccountBalanceTransfer created = transferService.create(dto);
        return ResponseEntity.ok(AccountBalanceTransferDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountBalanceTransferDto> update(@PathVariable String id, @RequestBody AccountBalanceTransferDto dto) {
        AccountBalanceTransfer updated = transferService.update(id, dto);
        return ResponseEntity.ok(AccountBalanceTransferDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        transferService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
