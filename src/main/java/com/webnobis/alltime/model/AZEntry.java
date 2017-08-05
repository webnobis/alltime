package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class AZEntry extends AbstractEntry implements Entry {

	private final LocalTime start;

	private final LocalTime now;

	private final LocalTime end;

	private final Duration expectedTime;

	private final Duration idleTime;

	private final Duration timeAssetsBefore;

	private final Map<String, Duration> items;

	public AZEntry(LocalDate day, LocalTime start, LocalTime now, Duration expectedTime, Duration idleTime, Duration timeAssetsBefore) {
		this(day, start, now, null, expectedTime, idleTime, timeAssetsBefore, Collections.emptyMap());
	}

	public AZEntry(LocalDate day, LocalTime start, LocalTime end, Duration expectedTime, Duration idleTime, Duration timeAssetsBefore, Map<String, Duration> items) {
		this(day, start, null, end, expectedTime, idleTime, timeAssetsBefore, items);
	}

	private AZEntry(LocalDate day, LocalTime start, LocalTime now, LocalTime end, Duration expectedTime, Duration idleTime, Duration timeAssetsBefore, Map<String, Duration> items) {
		super(day, EntryType.AZ);
		this.start = start;
		this.now = now;
		this.end = end;
		this.expectedTime = expectedTime;
		this.idleTime = idleTime;
		this.timeAssetsBefore = timeAssetsBefore;
		this.items = items;
	}

	@Override
	public LocalTime getStart() {
		return start;
	}

	@Override
	public LocalTime getEnd() {
		return end;
	}

	@Override
	public Duration getExpectedTime() {
		return expectedTime;
	}

	@Override
	public Duration getIdleTime() {
		return idleTime;
	}

	@Override
	public Duration getRealTime() {
		return Duration.between(start, Optional.ofNullable(end).orElse(now));
	}

	@Override
	public Duration getTimeAssets() {
		return timeAssetsBefore.minus(expectedTime).minus(idleTime).plus(getRealTime());
	}

	@Override
	public Map<String, Duration> getItems() {
		return items;
	}

}
