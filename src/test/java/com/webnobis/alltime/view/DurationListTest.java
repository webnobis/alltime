package com.webnobis.alltime.view;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class DurationListTest {

	@Test
	void test() {
		List<Duration> durations = getSelectableDurations(30, Duration.ofHours(4).plusMinutes(34));
		durations.forEach(System.out::println);
	}

	private List<Duration> getSelectableDurations(int rasterMinutes, Duration durationRange) {
		return Optional.ofNullable(durationRange).filter(d -> !d.isNegative()).map(Duration::toMinutes)
				.map(m -> LongStream.rangeClosed(-m, 0).map(Math::abs).filter(l -> l % rasterMinutes < 1)
						.mapToObj(Duration::ofMinutes))
				.orElseGet(Stream::empty).collect(Collectors.toList());
	}

}
