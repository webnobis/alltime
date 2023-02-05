package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class GTEntry extends AbstractEntry implements Entry {

	private final Duration expectedTime;

	public GTEntry(LocalDate day, Duration expectedTime, Map<String, Duration> items) {
		super(day, EntryType.GT, items);
		this.expectedTime = Objects.requireNonNull(expectedTime, "expectedTime is null");
	}

	@Override
	public Duration getExpectedTime() {
		return expectedTime;
	}

	@Override
	public Duration getTimeAssets() {
		return expectedTime.negated();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(expectedTime);
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
		GTEntry other = (GTEntry) obj;
		return Objects.equals(expectedTime, other.expectedTime);
	}

}
