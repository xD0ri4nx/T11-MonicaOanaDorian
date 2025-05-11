package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Author;
import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.repository.AuthorRepository;
import com.example.smartlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    public void deleteAuthorById(Long id) {
       authorRepository.deleteById(id);
    }

    public Map<String, Object> getAuthorStats(Long authorId) {
        Author author = authorRepository.findById(authorId).orElseThrow();
        List<Book> books = bookRepository.findByAuthorId(authorId);

        int totalBooks = books.size();
        int totalRatings = books.stream()
                .filter(b -> b.getNumberOfRatings() != null)
                .mapToInt(Book::getNumberOfRatings)
                .sum();
        double avgRating = books.stream()
                .filter(b -> b.getAverageRating() != null)
                .mapToDouble(Book::getAverageRating)
                .average()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("author", author);
        stats.put("totalBooks", totalBooks);
        stats.put("totalRatings", totalRatings);
        stats.put("averageRating", avgRating);
        stats.put("books", books);

        return stats;
    }

}
