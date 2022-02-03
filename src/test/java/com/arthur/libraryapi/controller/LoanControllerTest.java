package com.arthur.libraryapi.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.time.LocalDate;
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

import com.arthur.libraryapi.controller.LoanController;
import com.arthur.libraryapi.dto.BookDTO;
import com.arthur.libraryapi.dto.LoanDTO;
import com.arthur.libraryapi.dto.ReturnedLoanDTO;
import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.service.BookService;
import com.arthur.libraryapi.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = { LoanController.class })
@AutoConfigureMockMvc
public class LoanControllerTest {

	static final String LOAN_API = "/api/loans";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService bookService;

	@MockBean
	LoanService loanService;

	@Test
	@DisplayName("Deve realizar um emprestimo")
	public void createLoanTest() throws Exception {

		BookDTO bookdTO = BookDTO.builder().isbn("123").id(1).build();

		LoanDTO loanDTO = LoanDTO.builder().client("fulano").isbn("123").clientEmail("@email.com").bookDTO(bookdTO)
				.build();

		Book book = Book.builder().isbn("123").id(1).build();

		Loan loan = Loan.builder().client(loanDTO.getClient()).id(1).book(book).loanDate(LocalDate.now())
				.isbn(loanDTO.getIsbn()).build();

		String json = new ObjectMapper().writeValueAsString(loanDTO);

		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.accept(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.content(json); // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ

		mvc // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(content().string("1"));

	}

	@Test
	@DisplayName("Deve lançar erro ao não encontrar o livro a ser emprestado")
	public void invalidIsbnLoanTest() throws Exception {

		BookDTO bookdTO = BookDTO.builder().isbn("123").id(1).build();

		LoanDTO loanDTO = LoanDTO.builder().client("fulano").isbn("123").clientEmail("@email.com").bookDTO(bookdTO)
				.build();

		String json = new ObjectMapper().writeValueAsString(loanDTO);

		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.accept(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.content(json); // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ

		mvc // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("book not found for isbn"));

	}

	@Test
	@DisplayName("Deve lançar erro ao tentar pegar um livro ja emprestado")
	public void livroJaEmprestadoErroTest() throws Exception {

		BookDTO bookdTO = BookDTO.builder().isbn("123").id(1).build();

		LoanDTO loanDTO = LoanDTO.builder().client("fulano").isbn("123").clientEmail("@email.com").bookDTO(bookdTO)
				.build();

		Book book = Book.builder().isbn("123").id(1).build();

		String json = new ObjectMapper().writeValueAsString(loanDTO);

		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));

		BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
				.willThrow(new BusinessException("Livro ja ta emprestado"));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Livro ja ta emprestado"));

	}

	@Test
	@DisplayName("Deve retornar um emprestimo")
	public void returnedLoanTest() throws Exception {

		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

		Loan loan = Loan.builder().client("fulano").id(1).loanDate(LocalDate.now()).isbn("123").build();

		BDDMockito.given(loanService.getById(1)).willReturn(Optional.of(loan));

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());

		verify(loanService, times(1)).updateLoan(loan);

	}

	@Test
	@DisplayName("Deve retornar 404 para devolver um livro inexistente")
	public void returnedInexistentLoanTest() throws Exception {

		ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

		BDDMockito.given(loanService.getById(1)).willReturn(Optional.empty());

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
				.contentType(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.accept(MediaType.APPLICATION_JSON) // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.content(json); // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ

		mvc // ESSA PARTE AQUI EU N LEMBRO O QUE FAZ
				.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Livro inexistente no banco de dados"));

	}

}
