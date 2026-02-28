package com.fadlimz.dompetku.master.account.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "account", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"account_code", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Column(name = "account_code", length = 255, nullable = false)
    private String accountCode;

    @Column(name = "account_name", length = 255, nullable = false)
    private String accountName;

    @Column(name = "active_flag", length = 8, nullable = false)
    private String activeFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
