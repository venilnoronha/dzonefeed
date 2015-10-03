/*
 * Copyright 2015 Venil Noronha
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.venilnoronha.dzone.feed.cleanup;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.venilnoronha.dzone.feed.mongo.ArticlesRepository;
import com.venilnoronha.dzone.feed.mongo.LinksRepository;

/**
 * 
 * @author Venil Noronha
 */
@Component
public class Cleaner {

	private final static Logger LOGGER = Logger.getLogger(Cleaner.class);
	
	@Value("${cleaner.linksToKeep}")
	private int linksToKeep;
	
	@Autowired
	private LinksRepository linksRepository;
	
	@Value("${cleaner.articlesToKeep}")
	private int articlesToKeep;
	
	@Autowired
	private ArticlesRepository articlesRepository;
	
	@Scheduled(
		initialDelayString = "${cleaner.cleanInitialDelayMs}",
		fixedRateString = "${cleaner.cleanFrequencyMs}"
	)
	public void clean() {
		LOGGER.info("Beginning cleanup activity...");
		cleanLinks();
		cleanArticles();
		LOGGER.info("Cleanup activity complete!");
	}

	private void cleanLinks() {
		int deleted = clean(linksToKeep, "linkDate", linksRepository);
		if (deleted > 0) {
			LOGGER.info("Deleted " + deleted + " links");
		}
		else {
			LOGGER.info("No links to delete");
		}
	}
	
	private void cleanArticles() {
		int deleted = clean(articlesToKeep, "articleDate", articlesRepository);
		if (deleted > 0) {
			LOGGER.info("Deleted " + deleted + " articles");
		}
		else {
			LOGGER.info("No articles to delete");
		}
	}
	
	private <T> int clean(int itemsToKeep, String dateField, MongoRepository<T, String> repo) {
		int deleted = 0;
		long itemsCount = repo.count();
		if (itemsCount > itemsToKeep) {
			List<T> items = repo.findAll(new Sort(Direction.DESC, dateField));
			List<T> toDelete = items.subList(itemsToKeep, items.size());
			repo.delete(toDelete);
			deleted = toDelete.size();
		}
		return deleted;
	}
	
}
