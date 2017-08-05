package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@FunctionalInterface
public interface IdleTime {

	default Duration getIdleTime(LocalDate day, LocalTime start, LocalTime end) {
		if (day == null || start == null || end == null) {
			return getIdleTime(Duration.ZERO);
		}
		return getIdleTime(Duration.between(LocalDateTime.of(day, start),
				LocalDateTime.of((start.isAfter(end)) ? day.plusDays(1) : day, end)));
	}

	Duration getIdleTime(Duration workTime);

}
