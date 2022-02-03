package com.arthur.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.repository.LoanRepository;



@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LoanRepository repository;
	
	@Test
	@DisplayName("Deve verificar se existe emprestimo nao devolvido para o livro")
	public void existsByBookAndNotReturnedTest () {
		
		Book book = Book.builder().author("arthur").isbn("123").title("o idiota").build();
		
		entityManager.persist(book);
		
		Loan loan = Loan.builder().book(book)
				.client("fulano")
				.loanDate(LocalDate.now())
				.build();
		
		entityManager.persist(loan);
		
		boolean exists = repository.existsByBookAndNotReturned(book);
		
		assertThat(exists).isTrue();	
		
	}
	
	@Test
	@DisplayName("verifica que a devolução do emprestimo do livro está atrasada")
	public void findByLoanDateLessThenAndNotReturnedTest () {
		
		Book book = Book.builder().author("arthur").isbn("123").title("o idiota").build();
		
		entityManager.persist(book);
		
		Loan loan = Loan.builder().book(book)
				.client("fulano")
				.loanDate(LocalDate.now().minusDays(5))
				.build();
		
		entityManager.persist(loan);
		
		List<Loan> result = repository.findByLoanDateLessThenAndNotReturned(LocalDate.now().minusDays(4));
		
		assertThat(result).hasSize(1).contains(loan);
		
	}
	
}
