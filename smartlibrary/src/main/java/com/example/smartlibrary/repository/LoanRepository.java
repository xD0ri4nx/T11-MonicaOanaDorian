package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);

    @Query("SELECT l FROM Loan l WHERE l.returnDate IS NULL AND l.dueDate < CURRENT_DATE")
    List<Loan> findOverdueLoans();

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.borrowDate BETWEEN :start AND :end")
    long countByBorrowDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.borrowDate BETWEEN :start AND :end AND l.returnDate IS NOT NULL")
    long countByBorrowDateBetweenAndReturnDateIsNotNull(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.borrowDate BETWEEN :start AND :end AND l.returnDate IS NULL")
    long countByBorrowDateBetweenAndReturnDateIsNull(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.dueDate < CURRENT_DATE AND l.returnDate IS NULL AND l.borrowDate BETWEEN :start AND :end")
    long countOverdueLoansForMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT l FROM Loan l WHERE l.dueDate < CURRENT_DATE AND l.returnDate IS NULL AND l.borrowDate BETWEEN :start AND :end")
    List<Loan> findOverdueLoansForMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
