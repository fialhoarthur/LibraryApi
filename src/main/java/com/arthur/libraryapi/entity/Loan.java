package com.arthur.libraryapi.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Loan {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@JoinColumn(name="id_book")
	@ManyToOne
	private Book book;
	
	@Column
	private LocalDate loanDate;

	@Column
	private Boolean returned;
	
	@Column
	private String isbn;

	@Column
	private String client;
	
	@Column
	private String clientEmail;
	
}
