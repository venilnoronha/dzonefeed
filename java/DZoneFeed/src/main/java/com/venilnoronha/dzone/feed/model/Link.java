package com.venilnoronha.dzone.feed.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Link {

	@Id
	private String mongoId;

	@JsonProperty("id")
	private Long linkId;
	private String title;
	private String author;
	private String linkDescription;
	private Date linkDate;
	private String linkSource;
	private String linkGo;

	public String getMongoId() {
		return mongoId;
	}

	public void setMongoId(String mongoId) {
		this.mongoId = mongoId;
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLinkDescription() {
		return linkDescription;
	}

	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}

	public Date getLinkDate() {
		return linkDate;
	}

	public void setLinkDate(Date linkDate) {
		this.linkDate = linkDate;
	}

	public String getLinkSource() {
		return linkSource;
	}

	public void setLinkSource(String linkSource) {
		this.linkSource = linkSource;
	}

	public String getLinkGo() {
		return linkGo;
	}

	public void setLinkGo(String linkGo) {
		this.linkGo = linkGo;
	}

}
