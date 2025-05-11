package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.dto.LoanRequestDTO;
import com.example.smartlibrary.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return loanService.getLoanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Loan> createLoan(@RequestParam Long userId,
                                           @RequestParam Long copyId,
                                           @RequestParam(defaultValue = "14") int days) {
        return ResponseEntity.ok(loanService.createLoan(userId, copyId, days));
    }

    @PostMapping("/with-dates")
    public ResponseEntity<Loan> createLoanWithDates(@RequestBody LoanRequestDTO dto) {
        return ResponseEntity.ok(
                loanService.createLoan(
                        dto.userId(),
                        dto.copyId(),
                        dto.borrowDate(),
                        dto.dueDate(),
                        dto.returnDate()
                )
        );
    }

    @PutMapping("/{id}/return-v2")
    public ResponseEntity<Loan> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        return loanService.returnBook(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Loan>> getActiveLoans(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getActiveLoansByUser(userId));
    }


    @GetMapping("/overdue")
    public ResponseEntity<List<Loan>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }


}
