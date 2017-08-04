package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

public class DayEntry implements Entry {

	private final LocalDate day;

	private final EntryType type;

	private final Map<String, Duration> items;
	
	private final Duration timeAssetsBefore;

	public DayEntry(LocalDate day, EntryType type, Duration timeAssetsBefore, Map<String, Duration> items) {
		this.day = day;
		this.type = type;
		this.timeAssetsBefore = timeAssetsBefore;
		this.items = Collections.unmodifiableMap(items);
	}

	@Override
	public LocalDate getDay() {
		return day;
	}

	@Override
	public EntryType getType() {
		return type;
	}

	@Override
	public LocalTime getStart() {
		return day.atStartOfDay().toLocalTime();
	}

	@Override
	public Duration getTimeAssets() {
		return timeAssetsBefore;
	}

	@Override
	public Map<String, Duration> getItems() {
		return items;
	}

}
