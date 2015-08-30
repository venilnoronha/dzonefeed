package com.venilnoronha.dzone.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlesResult {

	private ArticlesData data;

	public ArticlesData getData() {
		return data;
	}

	public void setData(ArticlesData data) {
		this.data = data;
	}
	
}
