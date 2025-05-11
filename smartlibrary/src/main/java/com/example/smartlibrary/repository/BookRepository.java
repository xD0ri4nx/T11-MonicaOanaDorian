package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
//    List<Book> findByTitleContainingIgnoreCase(String title);
//
    List<Book> findByAuthorNameContainingIgnoreCase(String author);

    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByCategoryIgnoreCase(String category, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCaseAndCategoryIgnoreCase(String title, String category, Pageable pageable);

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
    List<String> findAllCategories();

    List<Book> findTop5ByAuthorIdAndIdNot(Long authorId, Long excludeId);
    List<Book> findTop5ByCategoryIgnoreCaseAndIdNot(String category, Long excludeId);

    List<Book> findByAuthorId(Long authorId);

}