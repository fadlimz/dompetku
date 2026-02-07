package com.fadlimz.dompetku.balances.repositories;

import com.fadlimz.dompetku.balances.entities.AccountBalance;
import com.fadlimz.dompetku.master.account.entities.Account;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, String> {

    // Cari berdasarkan Account dan User
    Optional<AccountBalance> findByAccountAndUser(Account account, User user);

    // Cari berdasarkan ID tapi wajib milik User tertentu
    Optional<AccountBalance> findByIdAndUser(String id, User user);

    // Cari semua milik User
    List<AccountBalance> findByUser(User user);
}
