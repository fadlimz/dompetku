package com.fadlimz.dompetku.balances.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account_balance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "value", precision = 20, scale = 0, nullable = false)
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}