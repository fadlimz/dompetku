package com.fadlimz.dompetku.transactions.accountBalanceTransfer.entities;

import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "account_balance_transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceTransfer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_code_id")
    private TransactionCode transactionCode;

    @Column(name = "transaction_number", insertable = false, updatable = false)
    private Integer transactionNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date")
    private Date transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account accountFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account accountTo;

    @Column(name = "value", precision = 20, scale = 0)
    private Double value;

    @Column(name = "description", length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
