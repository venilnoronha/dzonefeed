package com.venilnoronha.dzone.feed.fetcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		for (Date day : di) {
			List<Link> allLinks = new ArrayList<>();
			List<Link> links;
			Integer from = null;
			do {
				LOGGER.info("Fetching for day " + day + " and from " + from);
				LinksResponse resp = fetchLinks(day, from);
				links = extractLinks(resp);
				LOGGER.info("Fetched " + links.size() + " links");
				allLinks.addAll(links);
				from = from == null ? links.size() : from + links.size();
				Thread.sleep(1000); // Sleep a while
			} while (!links.isEmpty());
			dump(allLinks); // Dump per day
		}
		LOGGER.info("Completed links fetch");
	}
	
	private void dump(List<Link> links) {
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
