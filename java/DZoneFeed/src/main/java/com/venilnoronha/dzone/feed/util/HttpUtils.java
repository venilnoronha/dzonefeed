/*
 * Copyright 2015 Venil Noronha
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.venilnoronha.dzone.feed.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * 
 * @author Venil Noronha
 */
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
