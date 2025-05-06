package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
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



}