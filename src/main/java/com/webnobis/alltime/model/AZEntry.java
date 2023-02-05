package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class AZEntry extends AbstractEntry implements Entry {

	private final LocalTime start;

	private final LocalTime end;

	private final Duration expectedTime;

	private final Duration idleTime;

	public AZEntry(LocalDate day, LocalTime start, LocalTime end, Duration expectedTime, Duration idleTime,
			Map<String, Duration> items) {
		super(day, EntryType.AZ, items);
		this.start = start;
		this.end = end;
		this.expectedTime = Objects.requireNonNull(expectedTime, "expectedTime is null");
		this.idleTime = Objects.requireNonNull(idleTime, "idleTime is null");
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
		return Optional.ofNullable(start)
				.flatMap(startTime -> Optional.ofNullable(end).map(endTime -> Duration.between(startTime, endTime)))
				.map(realTime -> (realTime.isNegative()) ? realTime.plusDays(1L) : realTime).orElse(Duration.ZERO);
	}

	@Override
	public Duration getTimeAssets() {
		return getRealTime().minus(expectedTime).minus(idleTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(end, expectedTime, idleTime, start);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AZEntry other = (AZEntry) obj;
		return Objects.equals(end, other.end) && Objects.equals(expectedTime, other.expectedTime)
				&& Objects.equals(idleTime, other.idleTime) && Objects.equals(start, other.start);
	}

}
