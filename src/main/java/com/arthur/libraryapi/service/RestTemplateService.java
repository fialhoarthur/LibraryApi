package com.arthur.libraryapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.arthur.libraryapi.dto.CardDTO;

@Service
public class RestTemplateService {

	 private static final String URI = "https://61f94ea4783c1d0017c44aad.mockapi.io/api/payments/get";

	    public CardDTO chamarApiExterna() {
	    	long id = 5;
	    	RestTemplate rest = new RestTemplate();
	    	CardDTO cardDto1  = rest.getForObject(URI.concat("/"+id), CardDTO.class);
       		return cardDto1;
	    }
}	    
