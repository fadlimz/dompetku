package com.fadlimz.dompetku.master.cutOffDate.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.master.cutOffDate.entities.CutOffDate;
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
public class CutOffDateDto extends BaseDto {
    public Integer cutOffDate;

    /**
     * Converts this DTO to an Entity
     */
    public CutOffDate toEntity() {
        CutOffDate entity = super.toEntity(CutOffDate.class);
        entity.setCutOffDate(cutOffDate);

        return entity;
    }

    /**
     * Converts an Entity to DTO
     */
    public static CutOffDateDto fromEntity(CutOffDate entity) {
        if (entity == null) return null;
        CutOffDateDto dto = BaseDto.fromEntity(CutOffDateDto.class, entity);
        dto.cutOffDate = entity.getCutOffDate();

        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<CutOffDate> toEntityList(List<CutOffDateDto> dtos) {
        return dtos.stream()
                .map(CutOffDateDto::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<CutOffDateDto> fromEntityList(List<CutOffDate> entities) {
        return entities.stream()
                .map(CutOffDateDto::fromEntity)
                .collect(Collectors.toList());
    }
}
