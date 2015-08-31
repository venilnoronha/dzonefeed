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