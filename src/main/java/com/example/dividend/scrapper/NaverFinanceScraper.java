package com.example.dividend.scrapper;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import org.springframework.stereotype.Component;

@Component
public class NaverFinanceScraper implements Scraper{ // 인터페이스를 적용해 확장성 ex

	@Override
	public ScrapedResult scrap(Company company) {


		return null;
	}

	@Override
	public Company scrapCompanyByTicker(String ticker) {

		return null;
	}

}
