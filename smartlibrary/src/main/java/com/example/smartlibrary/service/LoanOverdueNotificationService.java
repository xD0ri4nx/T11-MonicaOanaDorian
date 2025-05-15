package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.LoanOverdueNotification;
import com.example.smartlibrary.model.User;
import com.example.smartlibrary.repository.LoanOverdueNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanOverdueNotificationService {
    @Autowired
    private LoanOverdueNotificationRepository repository;

    public LoanOverdueNotification create(User user, Loan loan) {
        LoanOverdueNotification n = new LoanOverdueNotification();
        n.setUser(user);
        n.setLoan(loan);
        n.setTimestamp(LocalDateTime.now());
        return repository.save(n);
    }

    public List<LoanOverdueNotification> getForUser(Long userId) {
        return repository.findByUserId(userId);
    }

    public void markAsRead(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setRead(true);
            repository.save(n);
        });
    }

    public boolean existsByLoanId(Long loanId) {
        return repository.existsByLoanId(loanId);
    }

}
