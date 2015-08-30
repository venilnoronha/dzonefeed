package com.venilnoronha.dzone.feed.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlesData {

	private String sort;
	private List<Article> nodes;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<Article> getNodes() {
		return nodes;
	}

	public void setNodes(List<Article> nodes) {
		this.nodes = nodes;
	}

}
