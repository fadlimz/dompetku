package com.fadlimz.dompetku.transactions.dailyCash.repositories;

import com.fadlimz.dompetku.transactions.dailyCash.entities.DailyCash;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Date;

@Repository
public interface DailyCashRepository extends JpaRepository<DailyCash, String> {
    List<DailyCash> findByUser(User user);
    List<DailyCash> findByUserAndTransactionDateGreaterThanEqualAndTransactionDateLessThan(User user, Date startDate, Date endDate);
    Optional<DailyCash> findByIdAndUser(String id, User user);
}
