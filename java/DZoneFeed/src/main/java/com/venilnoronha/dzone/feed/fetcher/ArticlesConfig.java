package com.venilnoronha.dzone.feed.fetcher;

import java.util.Map;

public class ArticlesConfig {

	private Map<Integer, String> categories;

	public Map<Integer, String> getCategories() {
		return categories;
	}

	public void setCategories(Map<Integer, String> categories) {
		this.categories = categories;
	}

}
