package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scrapper.Scraper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor
public class CompanyService {

	private final Scraper yahooFinanceScraper;
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;


	public Company save(String ticker) {
		boolean exists = this.companyRepository.existsByTicker(ticker);
		if (exists) {
			throw new RuntimeException("already exists ticker -> " + ticker);
		}
		return this.storeCompanyAndDividend(ticker);
	}

	private Company storeCompanyAndDividend(String ticker) {
		// ticker를 기준으로 회사를 스크래핑
		Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
		if (ObjectUtils.isEmpty(company)) {
			throw new RuntimeException("failed to scrap ticker -> " + ticker);
		}

		// 해당 회사 존재할 경우, 회사의 배당금 정보를 스크래핑
		ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

		// 스크래핑 결과
		CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
		List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
														.map(e -> new DividendEntity(companyEntity.getId(), e))
														.collect(Collectors.toList());

		this.dividendRepository.saveAll(dividendEntityList);
		return company;
	}

}