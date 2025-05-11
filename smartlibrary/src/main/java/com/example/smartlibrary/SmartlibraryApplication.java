package com.example.smartlibrary;

import com.example.smartlibrary.service.BookCopyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartlibraryApplication {
//	@Bean
//	public CommandLineRunner initBookCopies(BookCopyService bookCopyService) {
//		return args -> {
//			bookCopyService.createOneCopyPerBookForBranchOne();
//		};
//	}


	public static void main(String[] args) {
		SpringApplication.run(SmartlibraryApplication.class, args);
	}

}
