package com.example.smartlibrary.service;

import com.example.smartlibrary.model.BookCopy;
import com.example.smartlibrary.repository.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookCopyService {

    private final BookCopyRepository bookCopyRepository;

    @Autowired
    public BookCopyService(BookCopyRepository bookCopyRepository) {
        this.bookCopyRepository = bookCopyRepository;
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
}
