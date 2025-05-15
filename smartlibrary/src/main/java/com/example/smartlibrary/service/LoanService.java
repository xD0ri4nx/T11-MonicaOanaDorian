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
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

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

    public Loan createLoan(Long userId, Long copyId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        User user = userRepository.findById(userId).orElseThrow();
        BookCopy copy = bookCopyRepository.findById(copyId).orElseThrow();

        copy.setStatus(Status.BORROWED);
        bookCopyRepository.save(copy);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBookCopy(copy);
        loan.setBorrowDate(borrowDate);
        loan.setDueDate(dueDate);
        loan.setReturnDate(returnDate);

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

    public Optional<Loan> returnBook(Long loanId) {
        return loanRepository.findById(loanId).map(loan -> {
            loan.setReturnDate(LocalDate.now());

            BookCopy copy = loan.getBookCopy();
            copy.setStatus(Status.AVAILABLE);
            bookCopyRepository.save(copy);

            return loanRepository.save(loan);
        });
    }

    public List<Loan> getActiveLoansByUser(Long userId) {
        return loanRepository.findByUserIdAndReturnDateIsNull(userId);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    public Map<String, Long> getMonthlyLoanStats(LocalDate month) {
        YearMonth yearMonth = YearMonth.from(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Map<String, Long> stats = new HashMap<>();

        // Total loans
        long totalLoans = loanRepository.countByBorrowDateBetween(startDate, endDate);
        stats.put("Total Loans", totalLoans);

        // Completed loans (returned)
        long completedLoans = loanRepository.countByBorrowDateBetweenAndReturnDateIsNotNull(startDate, endDate);
        stats.put("Completed Loans", completedLoans);

        // Overdue loans
        long overdueLoans = loanRepository.countOverdueLoansForMonth(startDate, endDate);
        stats.put("Overdue Loans", overdueLoans);

        // Active loans (not yet returned)
        long activeLoans = loanRepository.countByBorrowDateBetweenAndReturnDateIsNull(startDate, endDate);
        stats.put("Active Loans", activeLoans);

        return stats;
    }

    public List<Map<String, Object>> getOverdueLoansForMonth(LocalDate month) {
        YearMonth yearMonth = YearMonth.from(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return loanRepository.findOverdueLoansForMonth(startDate, endDate).stream()
                .map(loan -> {
                    Map<String, Object> loanData = new HashMap<>();
                    loanData.put("bookTitle", loan.getBookCopy().getBook().getTitle());
                    loanData.put("userName", loan.getUser().getName());
                    loanData.put("dueDate", loan.getDueDate().toString());
                    loanData.put("daysOverdue", LocalDate.now().getDayOfYear() - loan.getDueDate().getDayOfYear());
                    return loanData;
                })
                .collect(Collectors.toList());
    }
}
