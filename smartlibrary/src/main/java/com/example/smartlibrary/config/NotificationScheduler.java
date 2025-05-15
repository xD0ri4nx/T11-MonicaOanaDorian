package com.example.smartlibrary.config;

import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.service.LoanOverdueNotificationService;
import com.example.smartlibrary.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private LoanService loanService;

    @Autowired
    private LoanOverdueNotificationService overdueNotificationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);


    //    @Scheduled(cron = "0 0 8 * * *") // every day at 08:00
    @Scheduled(cron = "0 * * * * *") // every 1 minute

    public void checkOverdueLoans() {
        logger.info("📅 Running scheduled check for overdue loans...");

        List<Loan> overdueLoans = loanService.getOverdueLoans();

        for (Loan loan : overdueLoans) {
            // check if notification already exists
            boolean exists = overdueNotificationService.existsByLoanId(loan.getId());
            if (!exists) {
                overdueNotificationService.create(loan.getUser(), loan);
            }
        }
        logger.info("✅ Finished checking overdue loans.");
    }
}

