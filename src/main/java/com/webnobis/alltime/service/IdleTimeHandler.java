package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

class IdleTimeHandler {

	private final NavigableMap<Duration, Duration> idleTimes;

	IdleTimeHandler(Map<Duration, Duration> idleTimes) {
		this.idleTimes = Collections.unmodifiableNavigableMap(new TreeMap<>(Objects.requireNonNull(idleTimes, "idleTimes is null")));
	}

	public Duration getIdleTime(LocalDate day, LocalTime start, LocalTime end) {
		if (day == null || start == null || end == null) {
			return getIdleTime(Duration.ZERO);
		}
		return getIdleTime(Duration.between(LocalDateTime.of(day, start),
				LocalDateTime.of((start.isAfter(end)) ? day.plusDays(1) : day, end)));
	}

	public Duration getIdleTime(Duration realTime) {
		Objects.requireNonNull(realTime, "reatTime is null");
		
		Duration limit = idleTimes.floorKey(realTime);
		if (limit == null) {
			return Duration.ZERO;
		}
		Duration lastIdleTime = idleTimes.get(limit);
		Duration idleTimesBefore = Duration.ofMillis(idleTimes.headMap(limit)
				.values().stream()
				.mapToLong(Duration::toMillis)
				.sum());
		Duration idleTimePart = realTime.minus(limit);
		return idleTimesBefore.plus((lastIdleTime.minus(idleTimePart).isNegative()) ? lastIdleTime : idleTimePart);
	}

}
