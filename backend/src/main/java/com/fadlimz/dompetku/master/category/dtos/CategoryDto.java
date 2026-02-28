package com.fadlimz.dompetku.master.category.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.master.category.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CategoryDto extends BaseDto {
    public String categoryCode;
    public String categoryName;
    public String cashFlowFlag;
    public String activeFlag;

    /**
     * Converts this DTO to an Entity
     */
    public Category toEntity() {
        Category category = super.toEntity(Category.class);
        category.setCategoryCode(categoryCode);
        category.setCategoryName(categoryName);
        category.setCashFlowFlag(cashFlowFlag);
        category.setActiveFlag(activeFlag);

        return category;
    }

    /**
     * Converts an Entity to DTO
     */
    public static CategoryDto fromEntity(Category entity) {
        if (entity == null) return null;
        CategoryDto dto = BaseDto.fromEntity(CategoryDto.class, entity);
        dto.categoryCode = entity.getCategoryCode();
        dto.categoryName = entity.getCategoryName();
        dto.cashFlowFlag = entity.getCashFlowFlag();
        dto.activeFlag = entity.getActiveFlag();

        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<Category> toEntityList(List<CategoryDto> dtos) {
        return dtos.stream()
                .map(CategoryDto::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<CategoryDto> fromEntityList(List<Category> entities) {
        return entities.stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}
