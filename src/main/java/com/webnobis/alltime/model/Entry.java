package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public interface Entry {

	LocalDate getDay();

	EntryType getType();

	default LocalTime getStart() {
		return null;
	}

	default LocalTime getEnd() {
		return null;
	}

	default Duration getExpectedTime() {
		return Duration.ZERO;
	}

	default Duration getIdleTime() {
		return Duration.ZERO;
	}

	default Duration getRealTime() {
		return Duration.ZERO;
	}

	default Duration getTimeAssets() {
		return Duration.ZERO;
	}

	Map<String, Duration> getItems();

}
