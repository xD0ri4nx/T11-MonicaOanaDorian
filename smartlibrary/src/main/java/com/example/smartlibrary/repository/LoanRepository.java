package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);

    @Query("SELECT l FROM Loan l WHERE l.returnDate IS NULL AND l.dueDate < CURRENT_DATE")
    List<Loan> findOverdueLoans();


}
