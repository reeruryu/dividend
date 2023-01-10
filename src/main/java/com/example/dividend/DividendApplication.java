package com.example.dividend;

import com.example.dividend.model.Company;
import com.example.dividend.scrapper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ObjectUtils;

@SpringBootApplication
public class DividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendApplication.class, args);

	}

}
