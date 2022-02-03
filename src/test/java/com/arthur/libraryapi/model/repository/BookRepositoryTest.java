package com.arthur.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.repository.BookRepository;

//
//No curso o cara testa todos os repositórios, inclusive os da classe do Java. 
//Testei só o que fiz para o ISBN

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
	public void returnTrueWhenIsbnExists() {
		
		//cenario
		String isbn = ("123");
		Book book = Book.builder().author("arthur").isbn(isbn).title("o idiota").build();
		entityManager.persist(book);
		
		//execução
		boolean exists = repository.existsByIsbn(isbn);
		
		//verificação
		assertThat(exists).isTrue();	
	}
	
	@Test
	@DisplayName("Deve retornar falso quando não existir um livro na base com isbn informado")
	public void returnFalseWhenIsbnNotExists() {
		//cenario
		String isbn = ("123");

		//execução
		boolean exists = repository.existsByIsbn(isbn);
		
		//verificação
		assertThat(exists).isFalse();	
	}
	
	
	
}
