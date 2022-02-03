package com.arthur.libraryapi.service.imp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.repository.BookRepository;
import com.arthur.libraryapi.service.BookService;

@Service
public class BookServiceImp implements BookService {

	@Autowired
	private BookRepository repository;
	
	@Override
	public Book save(Book book) {
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("isbn já cadastrado");
		}
		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(long id) {
		// TODO Auto-generated method stub
		return this.repository.findById((long) id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == 0) {
			throw new IllegalArgumentException("Book ou book id nulo");
		}
		this.repository.delete(book);
		
	}

	@Override
	public Book updateBook(Book book) {
		if(book == null || book.getId() == 0) {
			throw new IllegalArgumentException("Book ou book id nulo");
		}
		//this.repository.save(book);
		
		return this.repository.save(book);		
		
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		
		Example<Book> example = Example.of(filter,
				ExampleMatcher
				.matching()
				.withIgnoreCase()
				.withIgnoreNullValues()
				.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
		
		return repository.findAll(example, pageRequest);
	}

	@Override
	public Optional<Book> getBookByIsbn(String isbn) {
		// TODO Auto-generated method stub
		return repository.findByIsbn(isbn);
	}



}
