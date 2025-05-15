package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Book;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
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

    @Query(value = "SELECT b.title as title, a.name as author, COUNT(l.id) as borrowCount " +
            "FROM loan l " +
            "JOIN book_copy bc ON l.book_copy_id = bc.id " +
            "JOIN book b ON bc.book_id = b.id " +
            "JOIN author a ON b.author_id = a.id " +
            "WHERE l.borrow_date BETWEEN :start AND :end " +
            "GROUP BY b.title, a.name " +
            "ORDER BY borrowCount DESC LIMIT :limit", nativeQuery = true)
    List<Tuple> findMostBorrowedBooks(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("limit") int limit);

}