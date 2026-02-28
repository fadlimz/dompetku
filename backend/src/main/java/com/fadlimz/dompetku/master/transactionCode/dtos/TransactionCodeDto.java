package com.fadlimz.dompetku.master.transactionCode.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
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
public class TransactionCodeDto extends BaseDto {
    public String transactionCode;
    public String transactionName;

    /**
     * Converts this DTO to an Entity
     */
    public TransactionCode toEntity() {
        TransactionCode entity = super.toEntity(TransactionCode.class);
        entity.setTransactionCode(transactionCode);
        entity.setTransactionName(transactionName);
        return entity;
    }

    /**
     * Converts an Entity to DTO
     */
    public static TransactionCodeDto fromEntity(TransactionCode entity) {
        if (entity == null) return null;
        TransactionCodeDto dto = BaseDto.fromEntity(TransactionCodeDto.class, entity);
        dto.transactionCode = entity.getTransactionCode();
        dto.transactionName = entity.getTransactionName();
        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<TransactionCode> toEntityList(List<TransactionCodeDto> dtos) {
        return dtos.stream()
                .map(TransactionCodeDto::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<TransactionCodeDto> fromEntityList(List<TransactionCode> entities) {
        return entities.stream()
                .map(TransactionCodeDto::fromEntity)
                .collect(Collectors.toList());
    }
}
