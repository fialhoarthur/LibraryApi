package com.arthur.libraryapi.Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.repository.LoanRepository;
import com.arthur.libraryapi.service.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class LoanServiceTest {

	@Autowired
	LoanService loanService;
	
	@MockBean
	@Autowired
	LoanRepository repository;
	
	
	@Test
	@DisplayName("Deve salvar um emprestimo")
	public void mustSaveABook () {
		//cenario
		Loan loan = Loan.builder().book(Book.builder().id(1).isbn("123").build())
				.client("fulano")
				.loanDate(LocalDate.now())
				.build();

		Mockito.when(repository.save(loan)).thenReturn(Loan.builder().id(1).isbn("123")
				.client("fulano")
				.loanDate(LocalDate.now()).build());
		
		//Act
		Loan savedLoan = loanService.save(loan);
		
		//verific
		assertThat(savedLoan.getId()).isNotNull();
		assertThat(savedLoan.getIsbn()).isEqualTo("123");
		assertThat(savedLoan.getClient()).isEqualTo("fulano");
	}
	
	@Test
	@DisplayName("Deve lançar erro ao tentar salvar um emprestimo com livro ja emprestado")
	public void bookAlreadyLoaned () {
		//cenario
		Loan loan = Loan.builder().book(Book.builder().id(1).isbn("123").build())
				.client("fulano")
				.loanDate(LocalDate.now())
				.build();

		when(repository.existsByBookAndNotReturned(Book.builder().id(1).isbn("123").build()))
			.thenReturn(true);
		
		Throwable exception = catchThrowable(() -> loanService.save(loan));
		
		//verific
		assertThat(exception).isInstanceOf(BusinessException.class)
		.hasMessage("Livro ja ta emprestado"); 
		
		verify(repository, never()).save(loan);
		
	}

	@Test
	@DisplayName("deve buscar informações de um empréstimo por id")
	public void getByIdTest() {
		
		Loan loan = Loan.builder().book(Book.builder().id(1).isbn("123").build())
				.client("fulano")
				.loanDate(LocalDate.now())
				.build();
		int id = 10;
		loan.setId(id);
		
		BDDMockito.when(repository.findById((long) id)).thenReturn(Optional.of(loan));
		
		Optional<Loan> getloan = loanService.getById(id);
		
		assertThat(getloan.isPresent()).isTrue();
		assertThat(getloan.get().getId()).isEqualTo(id);
		
	}
	
	
	@Test
	@DisplayName("Deve atualizar um emprestimo")
	public void updateLoanTest() {
		
		int id = 10;
		
		Loan loan = Loan.builder().book(Book.builder().id(1).isbn("123").build())
				.client("fulano")
				.loanDate(LocalDate.now())
				.build();
		
		loan.setId(id);
		loan.setReturned(true);
		
		BDDMockito.when(repository.save(loan)).thenReturn(loan);
		
		Loan updatedLoan = loanService.updateLoan(loan);
		
		assertThat(updatedLoan.getReturned()).isTrue();
		verify(repository).save(loan);
		
	}
	
	
		
}
