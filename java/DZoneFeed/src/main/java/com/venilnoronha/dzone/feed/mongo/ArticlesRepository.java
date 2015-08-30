package com.venilnoronha.dzone.feed.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.venilnoronha.dzone.feed.model.Article;

public interface ArticlesRepository
		extends MongoRepository<Article, String>, PagingAndSortingRepository<Article, String> {

	public Page<Article> findByCategory(String category, Pageable page);

	public Article findByArticleId(Long articleId);

}