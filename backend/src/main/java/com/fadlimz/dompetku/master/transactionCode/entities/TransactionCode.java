package com.fadlimz.dompetku.master.transactionCode.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCode extends BaseEntity {

    @Column(name = "transaction_code", length = 100, unique = true)
    private String transactionCode;

    @Column(name = "transaction_name", length = 100)
    private String transactionName;
}
