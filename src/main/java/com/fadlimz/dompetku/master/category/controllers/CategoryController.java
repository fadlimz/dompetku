package com.fadlimz.dompetku.master.category.controllers;

import com.fadlimz.dompetku.config.StringUtil;
import com.fadlimz.dompetku.master.category.dtos.CategoryDto;
import com.fadlimz.dompetku.master.category.entities.Category;
import com.fadlimz.dompetku.master.category.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllOrSearch(@RequestParam(required = false) String keyword) {
        List<Category> categories;
        if (!StringUtil.isBlank(keyword)) {
            categories = categoryService.search(keyword);
        } else {
            categories = categoryService.findAll();
        }
        
        return ResponseEntity.ok(CategoryDto.fromEntityList(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable String id) {
        return categoryService.findById(id)
                .map(CategoryDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
        Category created = categoryService.create(dto);
        return ResponseEntity.ok(CategoryDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable String id, @RequestBody CategoryDto dto) {
        Category updated = categoryService.update(id, dto);
        return ResponseEntity.ok(CategoryDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
