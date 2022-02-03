package com.arthur.libraryapi.dto;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

	private int id;
	
	@NotEmpty 
	private String titular;
	
	@NotEmpty
	private String cardNumber;
	
	@NotEmpty
	private String cpf;
	
}
