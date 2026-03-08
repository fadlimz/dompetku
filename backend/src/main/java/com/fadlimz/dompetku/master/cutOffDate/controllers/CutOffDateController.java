package com.fadlimz.dompetku.master.cutOffDate.controllers;

import com.fadlimz.dompetku.master.cutOffDate.dtos.CutOffDateDto;
import com.fadlimz.dompetku.master.cutOffDate.entities.CutOffDate;
import com.fadlimz.dompetku.master.cutOffDate.services.CutOffDateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cut-off-dates")
@RequiredArgsConstructor
public class CutOffDateController {

    private final CutOffDateService cutOffDateService;

    @GetMapping
    public ResponseEntity<List<CutOffDateDto>> getAllOrSearch(@RequestParam(required = false) String keyword) {
        List<CutOffDate> cutOffDates = cutOffDateService.search(keyword);
        return ResponseEntity.ok(CutOffDateDto.fromEntityList(cutOffDates));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CutOffDateDto> getById(@PathVariable String id) {
        return cutOffDateService.findById(id)
                .map(CutOffDateDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CutOffDateDto> create(@RequestBody CutOffDateDto dto) {
        CutOffDate created = cutOffDateService.create(dto);
        return ResponseEntity.ok(CutOffDateDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CutOffDateDto> update(@PathVariable String id, @RequestBody CutOffDateDto dto) {
        CutOffDate updated = cutOffDateService.update(id, dto);
        return ResponseEntity.ok(CutOffDateDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        cutOffDateService.delete(id);
        return ResponseEntity.ok().build();
    }
}
