package com.arthur.libraryapi.controller;

import static org.mockito.ArgumentMatchers.anyInt;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.arthur.libraryapi.controller.BookController;
import com.arthur.libraryapi.dto.BookDTO;
import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.service.BookService;
import com.arthur.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {BookController.class})
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@MockBean
	LoanService loanService;
	
	private BookDTO createNewBook() {
		return BookDTO.builder()
				.author("arthur")
				.title("O Idiota")
				.isbn("111")
				.build();
	}
	
	@Test
	@DisplayName("Deve criar um livro com sucesso")
	public void createBookTest () throws Exception {
		
		BookDTO bookDto = createNewBook();
		
		Book savedBook = Book.builder()
				.author("arthur")
				.title("O Idiota")
				.isbn("111")
				.id(10)
				.build();
		
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);
				
		String json = new ObjectMapper().writeValueAsString(bookDto);	
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(BOOK_API)
		.contentType(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
		.accept(MediaType.APPLICATION_JSON)		// ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
		.content(json);							// ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
		
		mvc 				// ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
		.andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDto.getTitle()))
		.andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDto.getAuthor()))
		.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDto.getIsbn()));
	}

	
	@Test
	@DisplayName("Lançar erro de validação na criação do livro por falta de dados")
	public void createInvalidBookTest () throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(BOOK_API)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.content(json);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
	
		
	}
	
	@Test
	@DisplayName("Lançar erro ao criar livro com Isbn já cadastrado")
	public void createBookWithExistingIsbn() throws Exception {
		
		BookDTO bookDTO = createNewBook();
		
		String json = new ObjectMapper().writeValueAsString(bookDTO);
		String msg = "isbn repetido";
		BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(msg));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.post(BOOK_API)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.content(json);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest())
		.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
		.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(msg));
		
	}
	
	@Test
	@DisplayName("Deve pegar informações de um livro cadastrado")
	public void shouldGetBookInformationTest() throws Exception  {
		
		long id = 11;
		
		Book book = Book.builder().id(id).title(createNewBook().getTitle())
				.author(createNewBook().getAuthor()).isbn(createNewBook().getIsbn()).build();
		
		BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));  //optional????
	
		//Execução
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
		.get(BOOK_API.concat("/"+id))
		.accept(MediaType.APPLICATION_JSON);
		
		//assert
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
		.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
		.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()))
		.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
		;
		
	}
	
	@Test
	@DisplayName("Deve retornar not found quando livro não existir")
	public void bookNotFoundTest() throws Exception {
		
		BDDMockito.given(bookService.getById(Mockito.anyInt())).willReturn(Optional.empty());  //????
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Deve deletar um livro")
	public void deleteBookTest() throws Exception {
		
		BDDMockito.given(bookService.getById(anyInt())).willReturn(Optional.of(Book.builder().id(11).build()));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+11))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNoContent())
		;
		
	}
	
	@Test
	@DisplayName("Deve retornar not found quando nao encontrar um livro para deletar")
	public void notFoundDeleteBookTest() throws Exception {
		
		BDDMockito.given(bookService.getById(Mockito.anyInt())).willReturn(Optional.empty());  //optional????
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(BOOK_API.concat("/"+1))
				.accept(MediaType.APPLICATION_JSON);
		
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	@Test
	@DisplayName("Deve atualizar um livro")
	public void updateBookTest() throws Exception {
		
		long id = 10;
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		
		Book updatingBook = Book.builder().id(id).author("machado")
								.title("o alienista").isbn("111").build();
		
		Book updatedBook = Book.builder().id(id).author("arthur")
				.title("O Idiota")
				.isbn("111")
				.build();
		
		BDDMockito.given(bookService.getById(id))
				.willReturn(Optional.of(updatingBook));
		
		BDDMockito.given(bookService.updateBook(updatingBook)).willReturn(updatedBook);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+ 10))
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
				
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
		.andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
		.andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()))
		.andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle())); 
	}
	
	@Test
	@DisplayName("Deve retornar not found ao tentar atualizar um livro")
	public void notFoundUpdateBookTest() throws Exception {
		
		String json = new ObjectMapper().writeValueAsString(createNewBook());
		BDDMockito.given(bookService.getById(Mockito.anyInt())).willReturn(Optional.empty());  //optional????
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(BOOK_API.concat("/"+10))
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
				
		mvc
		.perform(request)
		.andExpect(MockMvcResultMatchers.status().isNotFound());
		
	}
	
//	@Test
//	@DisplayName("Deve filtrar livros")
//	public void findBookTest()throws Exception {
//		
//		Book book = Book.builder()
//				.author(createNewBook().getAuthor())
//				.title(createNewBook().getTitle())
//				.isbn(createNewBook().getIsbn())
//				.build();
//		book.setId(1);
//		
//		BDDMockito.given( bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
//		.willReturn( new PageImpl(Arrays.asList(book), PageRequest.of(0, 100), 1)) ;
//		;
//		
//		String query = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
//		
//		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
//				.get(BOOK_API.concat(query))
//				.accept(MediaType.APPLICATION_JSON);
//		
//		mvc
//		.perform(request)
//		.andExpect(MockMvcResultMatchers.status().isOk())
//		.andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
//		.andExpect(MockMvcResultMatchers.jsonPath("total elements").value(1));
//	}

	

}
