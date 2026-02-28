package com.fadlimz.dompetku.master.account.dtos;

import com.fadlimz.dompetku.master.account.entities.Account;
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
public class AccountDto extends BaseDto {
    public String accountCode;
    public String accountName;
    public String activeFlag;

    /**
     * Converts this DTO to an Entity
     */
    public Account toEntity() {
        Account account = super.toEntity(Account.class);
        account.setAccountCode(accountCode);
        account.setAccountName(accountName);
        account.setActiveFlag(activeFlag);
        
        return account;
    }

    /**
     * Converts an Entity to DTO
     */
    public static AccountDto fromEntity(Account entity) {
        if (entity == null) return null;
        AccountDto dto = BaseDto.fromEntity(AccountDto.class, entity);
        dto.accountCode = entity.getAccountCode();
        dto.accountName = entity.getAccountName();
        dto.activeFlag = entity.getActiveFlag();

        return dto;
    }

    /**
     * Converts a list of DTOs to a list of Entities
     */
    public static List<Account> toEntityList(List<AccountDto> dtos) {
        return dtos.stream()
                .map(AccountDto::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of Entities to a list of DTOs
     */
    public static List<AccountDto> fromEntityList(List<Account> entities) {
        return entities.stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }
}
