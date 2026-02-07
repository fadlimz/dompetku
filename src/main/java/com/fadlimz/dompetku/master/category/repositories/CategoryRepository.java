package com.fadlimz.dompetku.master.category.repositories;

import com.fadlimz.dompetku.master.category.entities.Category;
import com.fadlimz.dompetku.master.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByCategoryCodeAndUser(String categoryCode, User user);

    Optional<Category> findByIdAndUser(String id, User user);

    List<Category> findByUser(User user);

    @Query("SELECT c FROM Category c WHERE c.user = :user AND " +
           "(LOWER(c.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Category> search(@Param("user") User user, @Param("keyword") String keyword);
}
