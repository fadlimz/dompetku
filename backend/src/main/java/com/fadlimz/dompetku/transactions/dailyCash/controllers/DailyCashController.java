package com.fadlimz.dompetku.transactions.dailyCash.controllers;

import com.fadlimz.dompetku.transactions.dailyCash.dtos.DailyCashDto;
import com.fadlimz.dompetku.transactions.dailyCash.entities.DailyCash;
import com.fadlimz.dompetku.transactions.dailyCash.services.DailyCashService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/daily-cash")
@RequiredArgsConstructor
public class DailyCashController {

    private final DailyCashService dailyCashService;

    @GetMapping
    public ResponseEntity<List<DailyCashDto>> getAll(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        if ((year == null) != (month == null)) {
            return ResponseEntity.badRequest().build();
        }

        List<DailyCash> data = (year != null && month != null)
                ? dailyCashService.findByMonth(year, month)
                : dailyCashService.findAll();
        return ResponseEntity.ok(DailyCashDto.fromEntityList(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyCashDto> getById(@PathVariable String id) {
        return dailyCashService.findById(id)
                .map(DailyCashDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DailyCashDto> create(@RequestBody DailyCashDto dto) {
        DailyCash created = dailyCashService.create(dto);
        return ResponseEntity.ok(DailyCashDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DailyCashDto> update(@PathVariable String id, @RequestBody DailyCashDto dto) {
        DailyCash updated = dailyCashService.update(id, dto);
        return ResponseEntity.ok(DailyCashDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        dailyCashService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
