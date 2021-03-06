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
package com.venilnoronha.dzone.feed.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.venilnoronha.dzone.feed.model.Link;
import com.venilnoronha.dzone.feed.mongo.LinksRepository;
import com.venilnoronha.dzone.feed.util.HttpUtils;

/**
 * 
 * @author Venil Noronha
 */
@Controller
public class LinksRSSController {

	private static final DateFormat HTTP_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");

	@Value("${links.feedSize}")
	private int feedSize;
	
	@Autowired
	private LinksRepository linksRepository;
	
	@RequestMapping(value="/rss/links", method = RequestMethod.GET)
	public ModelAndView linksRssByLastModified(HttpEntity<byte[]> requestEntity, HttpServletRequest request,
			HttpServletResponse response) throws ParseException {
		HttpUtils.logUserIP("LinksRSS", request);
		String lastModified = requestEntity.getHeaders().getFirst("If-Modified-Since");
		if (lastModified == null) {
			lastModified = requestEntity.getHeaders().getFirst("If-None-Match");
		}
		PageRequest page = new PageRequest(0, feedSize, Direction.DESC, "linkDate");
		Page<Link> linkPage;
		if (lastModified != null) {
			Date lastModifiedDt = HTTP_FORMAT.parse(lastModified);
			linkPage = linksRepository.findByLinkDateBetween(lastModifiedDt, new Date(), page);
		}
		else {
			linkPage = linksRepository.findAll(page);
		}
		Iterator<Link> linksIter = linkPage.iterator();
		List<Link> links = new ArrayList<>();
		Date newLastModified = null;
		while (linksIter.hasNext()) {
			Link link = linksIter.next();
			Date linkDate = link.getLinkDate();
			if (newLastModified == null || newLastModified.before(linkDate)) {
				newLastModified = linkDate;
			}
			links.add(link);
		}
		if (links.isEmpty() && lastModified != null) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return null;
		}
		else {
			response.setHeader("ETag", HTTP_FORMAT.format(newLastModified));
			response.setHeader("Last-Modified", HTTP_FORMAT.format(newLastModified));
			ModelAndView mav = new ModelAndView();
			mav.setView(new LinksRSSView(links));
			return mav;
		}
	}
	
}