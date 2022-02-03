package com.arthur.libraryapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.arthur.libraryapi.entity.Book;

public interface BookService {

	Book save(Book book);

	Optional<Book> getById(long id);

	void delete(Book book);

	Book updateBook(Book book);

	Page<Book> find(Book filter, Pageable PageRequest );

	Optional<Book> getBookByIsbn(String isbn);

}
