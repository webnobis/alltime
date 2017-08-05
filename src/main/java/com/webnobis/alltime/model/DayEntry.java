package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

public class DayEntry extends AbstractEntry implements Entry {

	private final LocalTime start;

	private final Map<String, Duration> items;

	private final Duration timeAssetsBefore;

	public DayEntry(LocalDate day, EntryType type, Duration timeAssetsBefore, Map<String, Duration> items) {
		super(day, type);
		start = day.atStartOfDay().toLocalTime();
		this.timeAssetsBefore = timeAssetsBefore;
		this.items = Collections.unmodifiableMap(items);
	}

	@Override
	public LocalTime getStart() {
		return start;
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
