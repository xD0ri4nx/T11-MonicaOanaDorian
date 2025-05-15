package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.BookCopy;
import com.example.smartlibrary.model.utils.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookId(Long bookId);
    List<BookCopy> findByBranchId(Long branchId);

    @Query("SELECT bc.branch.name, COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.status = 'AVAILABLE' GROUP BY bc.branch.name")
    List<Object[]> countAvailableCopiesByBranch(@Param("bookId") Long bookId);

    @Query("""
    SELECT bc.branch.name, bc.status, COUNT(bc)
    FROM BookCopy bc
    WHERE bc.book.id = :bookId
    GROUP BY bc.branch.name, bc.status
""")
    List<Object[]> countCopiesByBranchAndStatus(@Param("bookId") Long bookId);

    Optional<BookCopy> findFirstByBookIdAndBranchIdAndStatus(Long bookId, Long branchId, Status status);

    List<BookCopy> findByBookIdAndBranchId(Long bookId, Long branchId);

    Page<BookCopy> findByBranchId(Long branchId, Pageable pageable);



}
