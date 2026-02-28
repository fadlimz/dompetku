package com.fadlimz.dompetku.master.transactionCode.repositories;

import com.fadlimz.dompetku.master.transactionCode.entities.TransactionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionCodeRepository extends JpaRepository<TransactionCode, String> {

    Optional<TransactionCode> findByTransactionCode(String transactionCode);

    @Query("SELECT t FROM TransactionCode t WHERE " +
           "LOWER(t.transactionCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.transactionName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TransactionCode> search(@Param("keyword") String keyword);
}
