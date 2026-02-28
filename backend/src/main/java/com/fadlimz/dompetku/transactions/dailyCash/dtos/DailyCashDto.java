package com.fadlimz.dompetku.transactions.dailyCash.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.master.account.dtos.AccountDto;
import com.fadlimz.dompetku.master.category.dtos.CategoryDto;
import com.fadlimz.dompetku.master.transactionCode.dtos.TransactionCodeDto;
import com.fadlimz.dompetku.transactions.dailyCash.entities.DailyCash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DailyCashDto extends BaseDto {
    public TransactionCodeDto transactionCode;
    public Integer transactionNumber;
    public Date transactionDate;
    public String cashflowFlag;
    public CategoryDto category;
    public AccountDto account;
    public Double value;
    public String description;

    public DailyCash toEntity() {
        DailyCash entity = super.toEntity(DailyCash.class);
        entity.setTransactionDate(transactionDate);
        entity.setCashflowFlag(cashflowFlag);
        entity.setValue(value);
        entity.setDescription(description);
        return entity;
    }

    public static DailyCashDto fromEntity(DailyCash entity) {
        if (entity == null) return null;
        DailyCashDto dto = BaseDto.fromEntity(DailyCashDto.class, entity);
        dto.transactionNumber = entity.getTransactionNumber();
        dto.transactionDate = entity.getTransactionDate();
        dto.cashflowFlag = entity.getCashflowFlag();
        dto.value = entity.getValue();
        dto.description = entity.getDescription();

        if (entity.getTransactionCode() != null) {
            dto.transactionCode = TransactionCodeDto.fromEntity(entity.getTransactionCode());
        }
        if (entity.getCategory() != null) {
            dto.category = CategoryDto.fromEntity(entity.getCategory());
        }
        if (entity.getAccount() != null) {
            dto.account = AccountDto.fromEntity(entity.getAccount());
        }

        return dto;
    }

    public static List<DailyCashDto> fromEntityList(List<DailyCash> entities) {
        return entities.stream().map(DailyCashDto::fromEntity).collect(Collectors.toList());
    }
}
