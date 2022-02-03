package com.arthur.libraryapi.service.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.arthur.libraryapi.service.EmailService;

@Service
public class EmailServiceImp implements EmailService{

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${application.mail.default-remetent}")
	private String remetent;
	
	@Override
	public void sendEmail(String msg, List<String> mailList) {

		SimpleMailMessage mailMsg = new SimpleMailMessage();
		mailMsg.setFrom(remetent);
		mailMsg.setSubject("livro atrasado");
		mailMsg.setText(msg);
		mailMsg.setTo(mailList.toArray(new String[mailList.size()] ));
		
		javaMailSender.send(mailMsg);
	}

}
