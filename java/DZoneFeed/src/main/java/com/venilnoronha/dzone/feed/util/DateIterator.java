package com.venilnoronha.dzone.feed.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
