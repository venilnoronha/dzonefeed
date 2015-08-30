package com.venilnoronha.dzone.feed.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.venilnoronha.dzone.feed.model.Article;
import com.venilnoronha.dzone.feed.mongo.ArticlesRepository;

@Controller
public class ArticlesRSSController {

	private static final DateFormat HTTP_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
	private static final int PAGE_SIZE = 1000;
	
	@Autowired
	private ArticlesRepository articlesRepository;
	
	@RequestMapping(value="/rss/articles", method = RequestMethod.GET)
	public ModelAndView articlesRssByLastModified(HttpEntity<byte[]> requestEntity, HttpServletResponse response)
			throws ParseException {
		String lastModified = requestEntity.getHeaders().getFirst("If-Modified-Since");
		if (lastModified == null) {
			lastModified = requestEntity.getHeaders().getFirst("If-None-Match");
		}
		PageRequest page = new PageRequest(0, PAGE_SIZE, Direction.DESC, "articleDate");
		Page<Article> articlePage;
		if (lastModified != null) {
			Date lastModifiedDt = HTTP_FORMAT.parse(lastModified);
			articlePage = articlesRepository.findByArticleDateBetween(lastModifiedDt, new Date(), page);
		}
		else {
			articlePage = articlesRepository.findAll(page);
		}
		Iterator<Article> articlesIter = articlePage.iterator();
		List<Article> articles = new ArrayList<>();
		Date newLastModified = null;
		while (articlesIter.hasNext()) {
			Article article = articlesIter.next();
			Date articleDate = article.getArticleDate();
			if (newLastModified == null || newLastModified.before(articleDate)) {
				newLastModified = articleDate;
			}
			articles.add(article);
		}
		if (articles.isEmpty() && lastModified != null) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return null;
		}
		else {
			response.setHeader("ETag", HTTP_FORMAT.format(newLastModified));
			response.setHeader("Last-Modified", HTTP_FORMAT.format(newLastModified));
			ModelAndView mav = new ModelAndView();
			mav.setView(new ArticlesRSSView(articles));
			return mav;
		}
	}
	
}