package com.fadlimz.dompetku.master.category.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"category_code", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Column(name = "category_code", length = 255, nullable = false)
    private String categoryCode;

    @Column(name = "category_name", length = 255, nullable = false)
    private String categoryName;

    @Column(name = "cash_flow_flag", length = 7, nullable = false)
    private String cashFlowFlag;

    @Column(name = "active_flag", length = 8, nullable = false)
    private String activeFlag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
