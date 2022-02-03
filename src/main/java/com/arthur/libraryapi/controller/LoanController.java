package com.arthur.libraryapi.controller;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.arthur.libraryapi.dto.LoanDTO;
import com.arthur.libraryapi.dto.ReturnedLoanDTO;
import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.service.BookService;
import com.arthur.libraryapi.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
		
	@Autowired
	private BookService bookService;
	
	@Autowired
	private LoanService loanService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public long createLoan (@RequestBody @Valid LoanDTO loanDTO) {
		
		Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"book not found for isbn"));
		
		Loan entity = Loan.builder().book(book).client(loanDTO.getClient())
				.loanDate(LocalDate.now())
				.isbn(loanDTO.getIsbn())
				.build();
		
		entity = loanService.save(entity);
		
		return entity.getId();
					
	}

	@PatchMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public void returnedLoan(@PathVariable long id, @RequestBody ReturnedLoanDTO dto) {
		
		Loan loan = loanService.getById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro inexistente no banco de dados") );
		loan.setReturned(dto.getReturned());
		loanService.updateLoan(loan);
		
		
	}
	
}
