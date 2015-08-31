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
import com.venilnoronha.dzone.feed.model.Article;

public class ArticlesRSSView extends AbstractRssFeedView {

	private static final String DZONE = "http://www.dzone.com";

	private List<Article> articles;

	public ArticlesRSSView(List<Article> articles) {
		this.articles = articles;
	}

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
		feed.setTitle("DZone Articles RSS");
		feed.setDescription("RSS feed for dzone.com articles");
		feed.setLink("http://www.dzone.com/");
		super.buildFeedMetadata(model, feed, request);
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Item> items = new ArrayList<Item>(articles.size());
		for (Article article : articles) {
			Item item = new Item();
			Guid guid = new Guid();
			guid.setValue(DZONE + article.getArticleLink());
			guid.setPermaLink(true);
			item.setGuid(guid);
			item.setTitle(article.getTitle());
			item.setAuthor(article.getAuthorName());
			item.setLink(DZONE + article.getArticleLink());
			item.setPubDate(article.getArticleDate());
			Description desc = new Description();
			desc.setType("plain");
			desc.setValue(article.getArticleContent());
			// article.getImageUrl()
			item.setDescription(desc);
			items.add(item);
		}
		return items;
	}

}