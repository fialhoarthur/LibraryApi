package com.arthur.libraryapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.arthur.libraryapi.entity.Loan;

@Service
public class ScheduleService {
	
	private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";
	
	@Autowired
	LoanService loanService;
	
	@Autowired
	EmailService emailService;
	
	@Value("${application.email.lateLoan.message}")
	private String msg;
	
	@Scheduled(cron = CRON_LATE_LOANS)
	public void sendEmailToLateLoans() {
		
		List<Loan> lateLoan = loanService.getAllLateLoans();
		List<String> mailList = lateLoan
				.stream()
				.map(loan-> loan.getClientEmail())
				.collect(Collectors.toList());
		
		
		emailService.sendEmail(msg, mailList);
	}
	
}
