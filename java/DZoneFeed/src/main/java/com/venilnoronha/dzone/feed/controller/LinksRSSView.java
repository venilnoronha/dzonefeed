package com.venilnoronha.dzone.feed.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Item;
import com.venilnoronha.dzone.feed.model.Link;

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
			Content content = new Content();
			content.setValue(link.getLinkDescription());
			item.setContent(content);
			item.setTitle(link.getTitle());
			item.setLink(DZONE + link.getLinkGo());
			item.setPubDate(link.getLinkDate());
			items.add(item);
		}
		return items;
	}

}