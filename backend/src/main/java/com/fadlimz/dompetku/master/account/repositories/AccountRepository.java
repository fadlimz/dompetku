package com.fadlimz.dompetku.master.account.repositories;

import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    // Cari berdasarkan unique key (User + AccountCode)
    Optional<Account> findByAccountCodeAndUser(String accountCode, User user);

    // Cari berdasarkan ID tapi wajib milik User tertentu (Security)
    Optional<Account> findByIdAndUser(String id, User user);

    // Cari semua milik User
    List<Account> findByUser(User user);

    // Pencarian Fuzzy (Code atau Name) + Filter User Mutlak
    @Query("SELECT a FROM Account a WHERE a.user = :user AND " +
           "(LOWER(a.accountCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.accountName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Account> search(@Param("user") User user, @Param("keyword") String keyword);
}
