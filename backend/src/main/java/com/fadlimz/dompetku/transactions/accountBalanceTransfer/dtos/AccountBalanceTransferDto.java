package com.fadlimz.dompetku.transactions.accountBalanceTransfer.dtos;

import com.fadlimz.dompetku.base.dtos.BaseDto;
import com.fadlimz.dompetku.transactions.accountBalanceTransfer.entities.AccountBalanceTransfer;
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
public class AccountBalanceTransferDto extends BaseDto {
    public String transactionCodeId;
    public String transactionCode;
    public Integer transactionNumber;
    public Date transactionDate;
    public String fromAccountId;
    public String toAccountId;
    public Double value;
    public String description;

    public AccountBalanceTransfer toEntity() {
        AccountBalanceTransfer entity = super.toEntity(AccountBalanceTransfer.class);
        entity.setTransactionDate(transactionDate);
        entity.setValue(value);
        entity.setDescription(description);
        return entity;
    }

    public static AccountBalanceTransferDto fromEntity(AccountBalanceTransfer entity) {
        if (entity == null) return null;
        AccountBalanceTransferDto dto = BaseDto.fromEntity(AccountBalanceTransferDto.class, entity);
        dto.transactionNumber = entity.getTransactionNumber();
        dto.transactionDate = entity.getTransactionDate();
        dto.value = entity.getValue();
        dto.description = entity.getDescription();

        if (entity.getTransactionCode() != null) {
            dto.transactionCodeId = entity.getTransactionCode().getId();
            dto.transactionCode = entity.getTransactionCode().getTransactionCode();
        }
        if (entity.getAccountFrom() != null) {
            dto.fromAccountId = entity.getAccountFrom().getId();
        }
        if (entity.getAccountTo() != null) {
            dto.toAccountId = entity.getAccountTo().getId();
        }

        return dto;
    }

    public static List<AccountBalanceTransferDto> fromEntityList(List<AccountBalanceTransfer> entities) {
        return entities.stream().map(AccountBalanceTransferDto::fromEntity).collect(Collectors.toList());
    }
}
