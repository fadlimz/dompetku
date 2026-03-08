package com.fadlimz.dompetku.master.cutOffDate.repositories;

import com.fadlimz.dompetku.master.cutOffDate.entities.CutOffDate;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CutOffDateRepository extends JpaRepository<CutOffDate, String> {

    Optional<CutOffDate> findByCutOffDateAndUser(Integer cutOffDate, User user);

    Optional<CutOffDate> findByIdAndUser(String id, User user);

    List<CutOffDate> findByUser(User user);

    void deleteByUser(User user);
}
