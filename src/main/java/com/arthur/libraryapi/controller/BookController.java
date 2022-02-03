package com.arthur.libraryapi.controller;


import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.arthur.libraryapi.dto.BookDTO;
import com.arthur.libraryapi.dto.LoanDTO;
import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.service.BookService;
import com.arthur.libraryapi.service.LoanService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create( @RequestBody @Valid BookDTO bookDto) {
		Book book = modelMapper.map(bookDto, Book.class);
		book = bookService.save(book);
		
		return modelMapper.map(book, BookDTO.class);
	}
	
	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO get(@PathVariable int id) {
		return bookService
				.getById(id)
				.map(book -> modelMapper.map(book, BookDTO.class))		//NAO ENTENDI MT BEM ESSE MÉTODO (GEtbyid)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable int id) {
		
		Book book = bookService.getById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		bookService.delete(book);
				
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public BookDTO updateBook (@PathVariable int id, BookDTO bookDTO) {
		Book book = bookService.getById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		book.setTitle(bookDTO.getTitle());
		book.setAuthor(bookDTO.getAuthor());
		
		book = bookService.updateBook(book);
	
		return modelMapper.map(book, BookDTO.class);
	}
	
//	@GetMapping
//	public Page<BookDTO> findBook(BookDTO bookDTO, Pageable pageRequest){
//		Book filter = modelMapper.map(bookDTO, Book.class);
//		Page<Book> result = bookService.find(filter, pageRequest);
//		List<BookDTO> list = result.getContent()
//				.stream()
//				.map(entity -> modelMapper.map(entity, BookDTO.class))
//				.collect(Collectors.toList());
//		
//		return new PageImpl<BookDTO>(list, pageRequest, result.getTotalElements());
//	}
	
	@GetMapping("/{id}/loan")
	public Page<LoanDTO> loanByBook (@PathVariable int id, Pageable pageable){
		
		Book book = bookService.getById(id)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		Page<Loan> loanList = loanService.getLoanByBook(book, pageable);
		
		List<LoanDTO> list = loanList.getContent()
				.stream()
				.map(loan -> {
					Book loanBook = loan.getBook();
					BookDTO bookDTO = modelMapper.map(loanBook, BookDTO.class);
					LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
					loanDTO.setBookDTO(bookDTO);
					return loanDTO;
				})
				.collect(Collectors.toList());
		
		return new PageImpl<LoanDTO>(list, pageable, loanList.getTotalElements());
		
	}
	
	
}
