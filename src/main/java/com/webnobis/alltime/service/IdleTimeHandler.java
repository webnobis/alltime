package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

class IdleTimeHandler {

	private final Map<Duration, Duration> idleTimes;

	IdleTimeHandler(Map<Duration, Duration> idleTimes) {
		this.idleTimes = idleTimes;
	}

	public Duration getIdleTime(LocalDate day, LocalTime start, LocalTime end) {
		if (day == null || start == null || end == null) {
			return getIdleTime(Duration.ZERO);
		}
		return getIdleTime(Duration.between(LocalDateTime.of(day, start),
				LocalDateTime.of((start.isAfter(end)) ? day.plusDays(1) : day, end)));
	}

	public Duration getIdleTime(Duration realTime) {
		return idleTimes.keySet().stream()
				.filter(limit -> limit.minus(realTime).isNegative())
				.sorted()
				.map(limit -> {
					Duration idleTime = idleTimes.get(limit);
					if (limit.plus(idleTime).minus(realTime).isNegative()) {
						return idleTime;
					} else {
						return realTime.minus(limit);
					}
				})
				.findFirst()
				.orElse(Duration.ZERO);
	}

}
