package com.arthur.libraryapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;


public interface LoanService {

	 Loan save(Loan loan);

	Loan updateLoan(Loan loan);

	Page<Loan> getLoanByBook(Book book, Pageable pageable);

	List<Loan> getAllLateLoans();

	Optional<Loan> getById(long id);
	
}
