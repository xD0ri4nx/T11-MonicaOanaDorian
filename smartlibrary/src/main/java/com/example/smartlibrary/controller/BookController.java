package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author
    ) {
        if (title != null) {
            return ResponseEntity.ok(bookService.searchByTitle(title));
        } else if (author != null) {
            return ResponseEntity.ok(bookService.searchByAuthor(author));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.createBook(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<Page<Book>> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        Page<Book> result = bookService.getBooksFiltered(title, category, pageable);
        return ResponseEntity.ok(result);
    }
}