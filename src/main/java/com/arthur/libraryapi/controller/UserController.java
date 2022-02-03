package com.arthur.libraryapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arthur.libraryapi.dto.CardDTO;
import com.arthur.libraryapi.service.RestTemplateService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private RestTemplateService service;
	
	@GetMapping
    public ResponseEntity getUserCardApi () {
		CardDTO retorno = service.chamarApiExterna();
        return ResponseEntity.ok(retorno);
    }
	
}
