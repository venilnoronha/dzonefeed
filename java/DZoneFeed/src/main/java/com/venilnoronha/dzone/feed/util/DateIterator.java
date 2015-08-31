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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * 
 * @author Venil Noronha
 */
public class DateIterator implements Iterable<Date>, Iterator<Date> {

	private Date since;
	private Date until;
	
	public DateIterator(Date until, int noOfDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(until);
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.until = cal.getTime();
		cal.add(Calendar.DATE, -1 * noOfDays);
		this.since = cal.getTime();
	}
	
	public DateIterator(Date until, Date since) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(until);
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.until = cal.getTime();
		cal.setTime(since);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.since = cal.getTime();
	}
	
	@Override
	public boolean hasNext() {
		return until.after(since);
	}

	@Override
	public Date next() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(until);
		cal.add(Calendar.DATE, -1);
		until = cal.getTime();
		return cal.getTime();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove is not implemented");
	}

	@Override
	public Iterator<Date> iterator() {
		return this;
	}

}
