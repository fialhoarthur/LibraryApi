package com.arthur.libraryapi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;


public interface LoanRepository extends JpaRepository<Loan, Long>{

	@Query(value = "select case when (count(l.id) > 0) then true else false end" + 
					" from Loan l where l.book = :book and (l.returned is false or l.returned is null)")
	boolean existsByBookAndNotReturned(@Param("book") Book book);

	Page<Loan> findByBook(Book book, Pageable pageable);

	@Query(value = "select l from Loan l where l.loanDate <= :threeDaysAgo and (l.returned is false or l.returned is null)")
	List<Loan> findByLoanDateLessThenAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

}
