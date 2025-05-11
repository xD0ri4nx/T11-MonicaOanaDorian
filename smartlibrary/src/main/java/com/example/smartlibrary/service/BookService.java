package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public Page<Book> getBooksFiltered(String title, String category, Pageable pageable) {
        if (title != null && category != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(title, category, pageable);
        } else if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (category != null) {
            return bookRepository.findByCategoryIgnoreCase(category, pageable);
        } else {
            return bookRepository.findAll(pageable);
        }
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchByAuthor(String authorName) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorName);
    }

    public Optional<Book> updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setSubtitle(updatedBook.getSubtitle());
            book.setCategory(updatedBook.getCategory());
            book.setThumbnail(updatedBook.getThumbnail());
            book.setDescription(updatedBook.getDescription());
            book.setYear(updatedBook.getYear());
            book.setAverageRating(updatedBook.getAverageRating());
            book.setNumberOfPages(updatedBook.getNumberOfPages());
            book.setNumberOfRatings(updatedBook.getNumberOfRatings());
            book.setIsbn(updatedBook.getIsbn());
            book.setAuthor(updatedBook.getAuthor());
            return bookRepository.save(book);
        });
    }


    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }

    public List<Book> findSimilarBooks(Long bookId) {
        Book currentBook = bookRepository.findById(bookId).orElseThrow();

        List<Book> byAuthor = bookRepository.findTop5ByAuthorIdAndIdNot(currentBook.getAuthor().getId(), bookId);
        if (byAuthor.size() >= 1) {
            return byAuthor;
        }

        return bookRepository.findTop5ByCategoryIgnoreCaseAndIdNot(currentBook.getCategory(), bookId);
    }



}
