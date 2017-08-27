package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

public class AZEntry extends AbstractEntry implements Entry {

	private final LocalTime start;

	private final LocalTime end;

	private final Duration expectedTime;

	private final Duration idleTime;

	public AZEntry(LocalDate day, LocalTime start, LocalTime end, Duration expectedTime, Duration idleTime, Map<String, Duration> items) {
		super(day, EntryType.AZ, items);
		this.start = start;
		this.end = end;
		this.expectedTime = expectedTime;
		this.idleTime = idleTime;
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
		return Optional.ofNullable(end)
				.map(e -> Duration.between(start, e))
				.orElse(Duration.ZERO);
	}

	@Override
	public Duration getTimeAssets() {
		return getRealTime().minus(expectedTime).minus(idleTime);
	}

}
