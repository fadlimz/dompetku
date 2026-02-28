package com.fadlimz.dompetku.master.transactionCode.controllers;

import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.transactionCode.dtos.TransactionCodeDto;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import com.fadlimz.dompetku.master.transactionCode.services.TransactionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-codes")
@RequiredArgsConstructor
public class TransactionCodeController {

    private final TransactionCodeService transactionCodeService;

    @GetMapping
    public ResponseEntity<List<TransactionCodeDto>> getAllOrSearch(@RequestParam(required = false) String keyword) {
        List<TransactionCode> results;
        if (!StringUtil.isBlank(keyword)) {
            results = transactionCodeService.search(keyword);
        } else {
            results = transactionCodeService.findAll();
        }
        return ResponseEntity.ok(TransactionCodeDto.fromEntityList(results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionCodeDto> getById(@PathVariable String id) {
        return transactionCodeService.findById(id)
                .map(TransactionCodeDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionCodeDto> create(@RequestBody TransactionCodeDto dto) {
        TransactionCode created = transactionCodeService.create(dto);
        return ResponseEntity.ok(TransactionCodeDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionCodeDto> update(@PathVariable String id, @RequestBody TransactionCodeDto dto) {
        TransactionCode updated = transactionCodeService.update(id, dto);
        return ResponseEntity.ok(TransactionCodeDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        transactionCodeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
