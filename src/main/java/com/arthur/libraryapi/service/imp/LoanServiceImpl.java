package com.arthur.libraryapi.service.imp;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.arthur.libraryapi.entity.Book;
import com.arthur.libraryapi.entity.Loan;
import com.arthur.libraryapi.exceptions.BusinessException;
import com.arthur.libraryapi.repository.LoanRepository;
import com.arthur.libraryapi.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService  {

	
	@Autowired
	private LoanRepository repository;
	
	@Override
	public Loan save(Loan loan) {
		if(repository.existsByBookAndNotReturned(loan.getBook())) {
			throw new BusinessException("Livro ja ta emprestado");
		}
	
		return repository.save(loan);

	}

	@Override
	public Optional<Loan> getById(long id) {
		// TODO Auto-generated method stub
		return repository.findById((long) id);
	}

	@Override
	public Loan updateLoan(Loan loan) {
		// TODO Auto-generated method stub
		return repository.save(loan);
	}

	@Override
	public Page<Loan> getLoanByBook(Book book, Pageable pageable) {
		// TODO Auto-generated method stub
		return repository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanDay = 4;
		LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDay);
		
		return repository.findByLoanDateLessThenAndNotReturned(threeDaysAgo);
	}

}