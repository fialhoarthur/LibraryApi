package com.arthur.libraryapi.Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.repository.BookRepository;
import com.arthur.libraryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class BookServiceTest {
	
	@Autowired
	BookService bookService;
	
	@MockBean
	@Autowired
	BookRepository repository;
	
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void mustSaveABook () {
		//cenario
		Book book = createValidBook();
		//Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(Book.builder().id(101).isbn("1001").title("o minimo idiota")
		.build());
		
		//Act
		Book savedBook = bookService.save(book);
		
		//verific
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("1001");
		assertThat(savedBook.getTitle()).isEqualTo("o minimo idiota");
	}


	private Book createValidBook() {
		return Book.builder().author("arthur").title("o minimo idiota").isbn("1001").build();
	}
	
	@Test
	@DisplayName("Deve lançar erro de negocio ao cadastrar um livro com isbn já cadastrado")
	public void shouldNotSaveaBookWithDuplicatedIsbn () {
	
	Book book = createValidBook();
	Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
	
	Throwable exception = Assertions.catchThrowable( () -> bookService.save(book));
	
	assertThat(exception).isInstanceOf(BusinessException.class)
		.hasMessage("isbn já cadastrado");
	
	Mockito.verify(repository, Mockito.never()).save(book);
	
	}
	
	@Test
	@DisplayName("Deve pegar um livro por id")
	public void mustGetABookById () {
		
		//cenario
		int id = 10;
		Book book = createValidBook();
		book.setId(id);
		
		Mockito.when(repository.findById((long) id)).thenReturn(Optional.of(book));
		
		//Act
		Optional <Book> foundBook = bookService.getById(id);
				
		//verific
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
	}
	
	@Test
	@DisplayName("Deve retornar vazio quando o ID nao existir na base")
	public void BookNotFoundByIdTest () {
		
		//cenario
		int id = 10;
		
		Mockito.when(repository.findById((long) id)).thenReturn(Optional.empty());
		
		//Act
		Optional <Book> foundBook = bookService.getById(id);
				
		//verific
		assertThat(foundBook.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void mustDeleteBookTest () {
		
		//cenario
		int id = 10;
		Book book = createValidBook();
		book.setId(id);
		
		//Act
		org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> bookService.delete(book));
				
		//verific
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	
	}
	
	@Test
	@DisplayName("Não deve deletar um livro por falta de Id ou livro nulo")
	public void IvalidDeleteABookTest () {
		
		Book book = new Book();		
		//Act
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));
				
		//verific
		Mockito.verify(repository, Mockito.never()).delete(book);
	
	}
	
	@Test
	@DisplayName("Deve atualizar os dados de um livro")
	public void UpdateBookTest () {
		
		int id = 10;
		Book book = Book.builder().id(id).build();
		
		Book updatedBook = createValidBook();
		updatedBook.setId(id);
		
		Mockito.when(repository.save(book)).thenReturn(updatedBook);
		
		Book finalBook = bookService.updateBook(book);
		
		assertThat(finalBook.getId()).isEqualTo(updatedBook.getId());
		assertThat(finalBook.getAuthor()).isEqualTo(updatedBook.getAuthor());
		assertThat(finalBook.getIsbn()).isEqualTo(updatedBook.getIsbn());
		assertThat(finalBook.getTitle()).isEqualTo(updatedBook.getTitle());
		
	}
	
	@Test
	@DisplayName("Lançar erro ao tentar atualizar um livro inexistente")
	public void UpdateInvalidBookTest () {
	
		Book book = new Book();		
		//Act
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(book));
				
		//verific
		Mockito.verify(repository, Mockito.never()).save(book);
	
	}
	
//	@Test
//	@DisplayName("Deve filtrar livros pelas propriedades")
//	public void findBookTest () {
//	
//		Book book = createValidBook();
//		
//
//		Page<Book>	page = new PageImpl(asList, PageRequest.of(0, 10),1 );
//		
//		PageRequest pageRequest = PageRequest.of(0, 10);
//		
//		Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class) ))
//			.thenReturn(page);
//			
//		Page<Book> result = bookService.find(book, pageRequest);
//		
//		assertThat(result.getTotalElements()).isEqualTo(1);
//		assertThat(result.getContent()).isEqualTo(asList);
//		
//	}
	
	@Test
	@DisplayName("Deve buscar um livro pelo Isbn")
	public void getBookByIsbnTest () {
		
		Book book = createValidBook();
		book.setId(1);
		book.setIsbn("123");
		
		Mockito.when(repository.findByIsbn("123")).thenReturn(Optional.of(book));
		
		Optional<Book> findedbook = bookService.getBookByIsbn("123");
		
		assertThat(findedbook.isPresent()).isTrue();
		assertThat(findedbook.get().getIsbn()).isEqualTo("123");
		
	}

	
}

