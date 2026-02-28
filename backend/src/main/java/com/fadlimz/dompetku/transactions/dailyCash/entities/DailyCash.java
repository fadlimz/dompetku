package com.fadlimz.dompetku.transactions.dailyCash.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.category.entities.Category;
import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "daily_cash")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCash extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_code_id")
    private TransactionCode transactionCode;

    @Column(name = "transaction_number", insertable = false, updatable = false)
    private Integer transactionNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "cashflow_flag", length = 100)
    private String cashflowFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "value", columnDefinition = "NUMERIC(30,10)")
    private Double value;

    @Column(name = "description", length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
