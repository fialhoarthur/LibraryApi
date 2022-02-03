package com.arthur.libraryapi.service;

import java.util.List;

public interface EmailService {

	void sendEmail(String msg, List<String> mailList);

}
