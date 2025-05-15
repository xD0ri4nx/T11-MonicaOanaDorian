package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(DISTINCT l.user) FROM Loan l WHERE l.borrowDate BETWEEN :start AND :end")
    long countActiveUsers(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(u) FROM User u")
    long countByCreatedAtBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(u) FROM User u WHERE  u.id NOT IN " +
            "(SELECT DISTINCT l.user.id FROM Loan l WHERE l.borrowDate BETWEEN :start AND :end)")
    long countInactiveUsers(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
