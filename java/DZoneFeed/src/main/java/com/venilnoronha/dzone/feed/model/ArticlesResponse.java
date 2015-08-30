package com.venilnoronha.dzone.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArticlesResponse {

	private ArticlesResult result;
	private int status;
	private boolean success;

	public ArticlesResult getResult() {
		return result;
	}

	public void setResult(ArticlesResult result) {
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
