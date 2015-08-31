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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.venilnoronha.dzone.feed.model.Link;

/**
 * 
 * @author Venil Noronha
 */
public class LinksRSSView extends AbstractRssFeedView {

	private static final String DZONE = "http://www.dzone.com";
	
	private List<Link> links;

	public LinksRSSView(List<Link> links) {
		this.links = links;
	}

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
		feed.setTitle("DZone Links RSS");
		feed.setDescription("RSS feed for dzone.com links");
		feed.setLink("http://www.dzone.com/links");
		super.buildFeedMetadata(model, feed, request);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Item> items = new ArrayList<Item>(links.size());
		for (Link link : links) {
			Item item = new Item();
			Guid guid = new Guid();
			guid.setValue(DZONE + link.getLinkGo());
			guid.setPermaLink(true);
			item.setGuid(guid);
			item.setTitle(link.getTitle());
			item.setAuthor(link.getAuthor());
			item.setLink(DZONE + link.getLinkGo());
			item.setPubDate(link.getLinkDate());
			Description desc = new Description();
			desc.setType("plain");
			desc.setValue(link.getLinkDescription());
			item.setDescription(desc);
			items.add(item);
		}
		return items;
	}

}