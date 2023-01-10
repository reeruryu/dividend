package com.example.dividend.scrapper;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;

public interface Scraper {
	Company scrapCompanyByTicker(String ticker);
	ScrapedResult scrap(Company company);

}
