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
package com.venilnoronha.dzone.feed.fetcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.venilnoronha.dzone.feed.model.Link;
import com.venilnoronha.dzone.feed.model.LinksResponse;
import com.venilnoronha.dzone.feed.mongo.LinksRepository;
import com.venilnoronha.dzone.feed.util.DateIterator;

/**
 * 
 * @author Venil Noronha
 */
@Component
public class LinksFetcher {

	private static final Logger LOGGER = Logger.getLogger(LinksFetcher.class);
	
	private static final String LINKS_API = "https://dzone.com/services/widget/links-listV2/list?sort=new";
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	private static final Integer OK = 200;
	
	@Value("${links.daysToFetch}")
	private int daysToFetch;
	
	@Autowired
	private LinksRepository linksRepository;
	
	@Scheduled(
		initialDelayString="${links.fetchInitialDelayMs}",
		fixedRateString="${links.fetchFrequencyMs}"
	)
	public void fetch() throws InterruptedException {
		LOGGER.info("Starting links fetch...");
		Date lastDumpDt = findLastDumpedLink();
		DateIterator di = prepareDateIterator(lastDumpDt);
		List<Link> allLinks = new ArrayList<>();
		boolean completed = false;
		for (Date day : di) {
			List<Link> links;
			Integer from = null;
			do {
				LOGGER.info("Fetching for day " + day + " and from " + from);
				LinksResponse resp = fetchLinks(day, from);
				links = extractLinks(resp);
				LOGGER.info("Fetched " + links.size() + " links");
				for (Link link : links) {
					if (lastDumpDt == null || link.getLinkDate().after(lastDumpDt)) {
						allLinks.add(link);
					}
					else {
						completed = true;
						break;
					}
				}
				from = from == null ? links.size() : from + links.size();
				Thread.sleep(1000); // Sleep a while
			} while (!links.isEmpty() && !completed);
			if (completed) {
				break;
			}
		}
		dump(allLinks);
		LOGGER.info("Completed links fetch");
	}
	
	private void dump(List<Link> links) {
		// Sort by earliest first
		Collections.sort(links, new Comparator<Link>() {
			@Override public int compare(Link l1, Link l2) {
				return l1.getLinkDate().compareTo(l2.getLinkDate());
			}
		});
		LOGGER.info("Starting links dump...");
		int no = 0;
		for (Link link : links) {
			Long linkId = link.getLinkId();
			Link existing = linksRepository.findByLinkId(linkId);
			if (existing == null) {
				linksRepository.save(link);
				no ++;
			}
		}
		LOGGER.info("Dumped " + no + " links");
	}

	private DateIterator prepareDateIterator(Date lastDumpDt) {
		DateIterator dateIterator;
		if (lastDumpDt == null) {
			dateIterator = new DateIterator(new Date(), daysToFetch);
		}
		else {
			dateIterator = new DateIterator(new Date(), lastDumpDt);
		}
		return dateIterator;
	}

	private Date findLastDumpedLink() {
		Date lastDumpedDate = null;
		Pageable page = new PageRequest(0, 1, Direction.DESC, "linkDate");
		Page<Link> links = linksRepository.findAll(page);
		Iterator<Link> linksIter = links.iterator();
		if (linksIter.hasNext()) {
			Link lastDumpedLink = linksIter.next();
			lastDumpedDate = lastDumpedLink.getLinkDate();
		}
		return lastDumpedDate;
	}

	private List<Link> extractLinks(LinksResponse resp) {
		List<Link> links = new ArrayList<>();
		if (OK.equals(resp.getStatus())) {
			if (resp.getResult() != null
					&& resp.getResult().getData() != null
					&& !resp.getResult().getData().getLinks().isEmpty()) {
				links = resp.getResult().getData().getLinks();
			}
		}
		return links;
	}
	
	public LinksResponse fetchLinks(Date date, Integer from) {
		StringBuilder api = new StringBuilder(LINKS_API);
		if (date != null) {
			String day = DAY_FORMAT.format(date);
			api.append("&day=").append(day);
		}
		if (from != null) {
			api.append("&from=").append(from);
		}
		RestTemplate restTemplate = new RestTemplate();
        LinksResponse linksResponse = restTemplate.getForObject(api.toString(), LinksResponse.class);
        return linksResponse;
	}

}
