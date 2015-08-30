package com.venilnoronha.dzone.feed.fetcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.venilnoronha.dzone.feed.model.Article;
import com.venilnoronha.dzone.feed.model.ArticlesResponse;
import com.venilnoronha.dzone.feed.mongo.ArticlesRepository;

@Component
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ArticlesFetcher {

	private static final Logger LOGGER = Logger.getLogger(ArticlesFetcher.class);
	
	private static final String ARTICLES_API = "https://dzone.com/services/widget/article-listV2/list?sort=newest";
	private static final Integer OK = 200;
	
	@Value("${articles.articlesToFetch}")
	private int articlesToFetch;
	
	@Bean
    @ConfigurationProperties(prefix = "articles")
    public ArticlesConfig articles() {
        return new ArticlesConfig();
    }
	
	@Autowired
	private ArticlesRepository articlesRepository;
	
	@Scheduled(
		initialDelayString="${articles.fetchInitialDelayMs}",
		fixedRateString="${articles.fetchFrequencyMs}"
	)
	public void fetch() throws InterruptedException {
		LOGGER.info("Starting articles fetch...");
		for (Map.Entry<Integer, String> categoryEntry : articles().getCategories().entrySet()) {
			Integer portal = categoryEntry.getKey();
			String category = categoryEntry.getValue();
			LOGGER.info("Starting " + category + " articles fetch...");
			Date lastDumped = findLastDumpedArticle(category);
			List<Article> allArticles = new ArrayList<>();
			Integer from = null;
			Integer page = null;
			boolean completed = false;
			do {
				LOGGER.info("Fetching " + category + " articles with page " + page + " and from " + from);
				ArticlesResponse resp = fetchArticles(portal, from, page);
				List<Article> articles = extractArticles(resp);
				LOGGER.info("Fetched " + articles.size() + " articles for " + category);
				for (Article article : articles) {
					article.setCategory(category);
					if (lastDumped != null) {
						if (article.getArticleDate().after(lastDumped)) {
							allArticles.add(article);
						}
						else {
							completed = true;
							break;
						}
					}
					else {
						if (allArticles.size() < articlesToFetch) {
							allArticles.add(article);
						}
						else {
							completed = true;
							break;
						}
					}
				}
				if (articles.isEmpty()) {
					completed = true;
				}
				from = from == null ? articles.size() : from + articles.size();
				page = page == null ? 2 : page + 1;
				Thread.sleep(1000); // Sleep a while
			} while (!completed);
			dump(allArticles, category);
			LOGGER.info("Completed " + category + " articles fetch");
		}
		LOGGER.info("Completed articles fetch");
	}
	
	private void dump(List<Article> articles, String category) {
		LOGGER.info("Starting " + category + " articles dump...");
		int no = 0;
		for (Article article : articles) {
			Long articleId = article.getArticleId();
			Article existing = articlesRepository.findByArticleId(articleId);
			if (existing == null) {
				articlesRepository.save(article);
				no ++;
			}
		}
		LOGGER.info("Dumped " + no + " " + category + " articles");
	}

	private List<Article> extractArticles(ArticlesResponse resp) {
		List<Article> articles = new ArrayList<>();
		if (OK.equals(resp.getStatus())) {
			if (resp.getResult() != null
					&& resp.getResult().getData() != null
					&& !resp.getResult().getData().getNodes().isEmpty()) {
				articles = resp.getResult().getData().getNodes();
			}
		}
		return articles;
	}
	
	private ArticlesResponse fetchArticles(int portal, Integer from, Integer page) {
		StringBuilder api = new StringBuilder(ARTICLES_API);
		api.append("&portal=").append(portal);
		if (from != null) {
			api.append("&from=").append(from);
		}
		if (page != null) {
			api.append("&page=").append(page);
		}
		RestTemplate restTemplate = new RestTemplate();
        ArticlesResponse articlesResponse = restTemplate.getForObject(api.toString(), ArticlesResponse.class);
        return articlesResponse;
	}

	private Date findLastDumpedArticle(String category) {
		Date lastDumpedDate = null;
		Pageable page = new PageRequest(0, 1, Direction.DESC, "articleDate");
		Page<Article> articles = articlesRepository.findByCategory(category, page);
		Iterator<Article> articlesIter = articles.iterator();
		if (articlesIter.hasNext()) {
			Article lastDumpedArticle = articlesIter.next();
			lastDumpedDate = lastDumpedArticle.getArticleDate();
		}
		return lastDumpedDate;
	}
	
}
