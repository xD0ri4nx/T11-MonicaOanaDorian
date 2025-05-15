package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.LoanOverdueNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanOverdueNotificationRepository extends JpaRepository<LoanOverdueNotification, Long> {
    List<LoanOverdueNotification> findByUserId(Long userId);
    boolean existsByLoanId(Long loanId);

}