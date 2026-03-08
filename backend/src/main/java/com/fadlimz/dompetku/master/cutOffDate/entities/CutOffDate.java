package com.fadlimz.dompetku.master.cutOffDate.entities;

import com.fadlimz.dompetku.base.entities.BaseEntity;
import com.fadlimz.dompetku.master.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cut_off_date", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cut_off_date", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CutOffDate extends BaseEntity {

    @Column(name = "cut_off_date", nullable = false)
    private Integer cutOffDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
