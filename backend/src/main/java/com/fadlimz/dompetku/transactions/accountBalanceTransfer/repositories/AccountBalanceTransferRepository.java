package com.fadlimz.dompetku.transactions.accountBalanceTransfer.repositories;

import com.fadlimz.dompetku.transactions.accountBalanceTransfer.entities.AccountBalanceTransfer;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountBalanceTransferRepository extends JpaRepository<AccountBalanceTransfer, String> {
    List<AccountBalanceTransfer> findByUser(User user);
    Optional<AccountBalanceTransfer> findByIdAndUser(String id, User user);
}
