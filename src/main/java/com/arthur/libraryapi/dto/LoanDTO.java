package com.arthur.libraryapi.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
	
	@NotEmpty
	private String isbn;
	
	@NotEmpty
	private String client;
	
	@NotEmpty
	private String clientEmail;
	
	@NotEmpty
	private BookDTO bookDTO;
}
