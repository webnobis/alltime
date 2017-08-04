package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class GTEntry extends DayEntry implements Entry {

	private final Duration expectedTime;

	public GTEntry(LocalDate day, EntryType type, Duration expectedTime, Duration timeAssetsBefore, Map<String, Duration> items) {
		super(day, type, timeAssetsBefore, items);
		this.expectedTime = expectedTime;
	}

	@Override
	public Duration getExpectedTime() {
		return expectedTime;
	}

	@Override
	public Duration getTimeAssets() {
		return super.getTimeAssets().minus(expectedTime);
	}

}
