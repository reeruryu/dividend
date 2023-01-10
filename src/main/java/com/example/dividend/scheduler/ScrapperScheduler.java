package com.example.dividend.scheduler;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scrapper.YahooFinanceScraper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScrapperScheduler {

	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;
	private final YahooFinanceScraper yahooFinanceScraper;

	// 일정 주기마다 수행
//	@CacheEvict(value = "finance", key = ) // 특정 키만 지우고 싶을 때
	@CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // 레디스 캐시에 있는 finance에 해당하는 데이터는 모두 비운다
	@Scheduled(cron = "${scheduler.scrap.yahoo}") // 서비스 도중에 바뀔 수 있으니 yaml 파일에서 관리
	public void yahooFinanceScheduling() {
//		log.info("scraping scheduler is started");
		// 저장된 회사 목록을 조회
		List<CompanyEntity> companies = this.companyRepository.findAll();

		// 회사마다 배당금 정보를 새로 스크래핑
		for (var company: companies) {
			log.info("scraping scheduler is started -> " + company.getName());
			ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
														new Company(company.getTicker(), company.getName()));

			// 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
			scrapedResult.getDividends().stream()
				// 디비든 모델을 디비든 엔티티로 매핑
				.map(e -> new DividendEntity(company.getId(), e))
				// 엘레먼트를 하나씩 디비든 레파지토리에 삽입
				.forEach(e -> {
					boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
					if (!exists) {
						this.dividendRepository.save(e);
					}
				});
			// 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
			try {
				Thread.sleep(3000); // 3 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// for 스레드 풀 테스트
	/*@Scheduled(fixedDelay = 1000)
	public void test1() throws InterruptedException {
		Thread.sleep(10000); // 10초 간 일시정지
		System.out.println(Thread.currentThread().getName() + " -> 테스트 1 : " + LocalDateTime.now());
	}

	@Scheduled(fixedDelay = 1000)
	public void test2(){
		System.out.println(Thread.currentThread().getName() + " -> 테스트 2 : " + LocalDateTime.now());
	}*/

}
