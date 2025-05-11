package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.model.BookCopy;
import com.example.smartlibrary.model.Branch;
import com.example.smartlibrary.model.utils.Status;
import com.example.smartlibrary.repository.BookCopyRepository;
import com.example.smartlibrary.repository.BookRepository;
import com.example.smartlibrary.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BranchRepository branchRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookCopyService(BookCopyRepository bookCopyRepository, BranchRepository branchRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.branchRepository = branchRepository;
        this.bookRepository = bookRepository;
    }

    public List<BookCopy> getAllCopies() {
        return bookCopyRepository.findAll();
    }

    public Optional<BookCopy> getCopyById(Long id) {
        return bookCopyRepository.findById(id);
    }

    public List<BookCopy> getCopiesByBookId(Long bookId) {
        return bookCopyRepository.findByBookId(bookId);
    }

    public List<BookCopy> getCopiesByBranchId(Long branchId) {
        return bookCopyRepository.findByBranchId(branchId);
    }

    public BookCopy createCopy(BookCopy copy) {
        return bookCopyRepository.save(copy);
    }

    public void deleteCopy(Long id) {
        bookCopyRepository.deleteById(id);
    }

    public Map<String, Long> getAvailabilityPerBranch(Long bookId) {
        List<Object[]> results = bookCopyRepository.countAvailableCopiesByBranch(bookId);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    public Map<String, Map<Status, Long>> getBookStatusStats(Long bookId) {
        List<Object[]> rows = bookCopyRepository.countCopiesByBranchAndStatus(bookId);
        Map<String, Map<Status, Long>> stats = new HashMap<>();

        for (Object[] row : rows) {
            String branch = (String) row[0];
            Status status = (Status) row[1];
            Long count = (Long) row[2];

            stats.computeIfAbsent(branch, k -> new EnumMap<>(Status.class)).put(status, count);
        }

        return stats;
    }

    public void createOneCopyPerBookForBranchOne() {
        Branch branch = branchRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Branch ID 1 not found"));

        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setBranch(branch);
            copy.setStatus(Status.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }

    public Optional<BookCopy> findAvailableCopy(Long bookId, Long branchId) {
        return bookCopyRepository.findFirstByBookIdAndBranchIdAndStatus(bookId, branchId, Status.AVAILABLE);
    }

    public List<BookCopy> getCopiesByBookAndBranch(Long bookId, Long branchId) {
        return bookCopyRepository.findByBookIdAndBranchId(bookId, branchId);
    }


}
