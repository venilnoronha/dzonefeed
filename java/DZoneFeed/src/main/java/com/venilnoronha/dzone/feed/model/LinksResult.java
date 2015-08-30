package com.venilnoronha.dzone.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinksResult {

	private LinksData data;

	public LinksData getData() {
		return data;
	}

	public void setData(LinksData data) {
		this.data = data;
	}
	
}
