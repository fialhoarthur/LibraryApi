package com.arthur.libraryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
	
	private long id;
	
    @NotEmpty
	private String title;
	
    @NotEmpty
	private String author;
    
    @NotEmpty
	private String isbn;
}
