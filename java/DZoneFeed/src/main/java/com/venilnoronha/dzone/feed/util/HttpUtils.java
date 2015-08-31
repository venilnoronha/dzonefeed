package com.venilnoronha.dzone.feed.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class HttpUtils {
	
	private static final Logger LOGGER = Logger.getLogger(HttpUtils.class);

	public static void logUserIP(String resource, HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");  
		if (ipAddress == null) {  
			ipAddress = request.getRemoteAddr();  
		}
		LOGGER.info("Feed consumed, " + resource + "," + ipAddress);
	}
	
}
