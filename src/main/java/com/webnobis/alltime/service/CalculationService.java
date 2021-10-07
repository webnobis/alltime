package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.CalculationType;
import com.webnobis.alltime.model.EntryType;

@FunctionalInterface
public interface CalculationService {

	default Duration calculateTimeAssetsSum(LocalDate fromDay, LocalDate untilDay, EntryType type) {
		long days = Duration.between(fromDay.atStartOfDay(), untilDay.atStartOfDay()).toDays();
		return LongStream.rangeClosed(0, days).mapToObj(l -> fromDay.plusDays(l))
				.map(day -> Optional.ofNullable(calculate(day, type, null, null, null)).orElse(Collections.emptyMap()))
				.map(map -> Optional.ofNullable(map.get(CalculationType.TIME_ASSETS)).orElse(Duration.ZERO))
				.reduce((d1, d2) -> d1.plus(d2)).orElse(Duration.ZERO);
	}

	Map<CalculationType, Duration> calculate(LocalDate day, EntryType type, LocalTime start, LocalTime end,
			Duration idleTime);

}
