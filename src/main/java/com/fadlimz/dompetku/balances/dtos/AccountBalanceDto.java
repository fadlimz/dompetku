package com.fadlimz.dompetku.balances.dtos;

import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.base.dtos.BaseDto;
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
public class AccountBalanceDto extends BaseDto {
    public String accountId;
    public String accountCode;
    public String accountName;
    public Double value;

    /**
     * Converts this DTO to an Entity
     */
    public AccountBalance toEntity() {
        AccountBalance balance = super.toEntity(AccountBalance.class);
        balance.setValue(value);
        return balance;
    }

    /**
     * Converts an Entity to DTO
     */
    public static AccountBalanceDto fromEntity(AccountBalance entity) {
        if (entity == null) return null;
        AccountBalanceDto dto = BaseDto.fromEntity(AccountBalanceDto.class, entity);
        dto.value = entity.getValue();
        if (entity.getAccount() != null) {
            dto.accountId = entity.getAccount().getId();
            dto.accountCode = entity.getAccount().getAccountCode();
            dto.accountName = entity.getAccount().getAccountName();
        }

        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<AccountBalance> toEntityList(List<AccountBalanceDto> dtos) {
        return dtos.stream()
                .map(AccountBalanceDto::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<AccountBalanceDto> fromEntityList(List<AccountBalance> entities) {
        return entities.stream()
                .map(AccountBalanceDto::fromEntity)
                .collect(Collectors.toList());
    }
}