package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.BookCopy;
import com.example.smartlibrary.model.utils.Status;
import com.example.smartlibrary.service.BookCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copies")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    @Autowired
    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @GetMapping
    public ResponseEntity<List<BookCopy>> getAllCopies() {
        return ResponseEntity.ok(bookCopyService.getAllCopies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopy> getCopyById(@PathVariable Long id) {
        return bookCopyService.getCopyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<BookCopy>> getCopiesByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookCopyService.getCopiesByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<BookCopy> createCopy(@RequestBody BookCopy copy) {
        return ResponseEntity.ok(bookCopyService.createCopy(copy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCopy(@PathVariable Long id) {
        bookCopyService.deleteCopy(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/available")
    public ResponseEntity<BookCopy> getAvailableCopy(
            @RequestParam Long bookId,
            @RequestParam Long branchId) {
        return bookCopyService.findAvailableCopy(bookId, branchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-branch-and-book")
    public ResponseEntity<List<BookCopy>> getCopiesByBookAndBranch(@RequestParam Long bookId, @RequestParam Long branchId) {
        return ResponseEntity.ok(bookCopyService.getCopiesByBookAndBranch(bookId, branchId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Page<BookCopy>> getByBranch(
            @PathVariable Long branchId,
            Pageable pageable) {
        return ResponseEntity.ok(bookCopyService.getCopiesByBranch(branchId, pageable));
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<BookCopy> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        return bookCopyService.updateBookCopyStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
