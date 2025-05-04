package com.example.smartlibrary.service;

import com.example.smartlibrary.model.BookCopy;
import com.example.smartlibrary.model.Loan;
import com.example.smartlibrary.model.User;
import com.example.smartlibrary.model.utils.Status;
import com.example.smartlibrary.repository.BookCopyRepository;
import com.example.smartlibrary.repository.LoanRepository;
import com.example.smartlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookCopyRepository bookCopyRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public Loan createLoan(Long userId, Long copyId, int loanDays) {
        User user = userRepository.findById(userId).orElseThrow();
        BookCopy copy = bookCopyRepository.findById(copyId).orElseThrow();

        copy.setStatus(Status.BORROWED);
        bookCopyRepository.save(copy);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBookCopy(copy);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(loanDays));
        return loanRepository.save(loan);
    }

    public Loan returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        loan.setReturnDate(LocalDate.now());

        BookCopy copy = loan.getBookCopy();
        copy.setStatus(Status.AVAILABLE);
        bookCopyRepository.save(copy);

        return loanRepository.save(loan);
    }
}
