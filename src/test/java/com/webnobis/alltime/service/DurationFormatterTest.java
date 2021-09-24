package com.webnobis.alltime.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class DurationFormatterTest {

	@Test
	void testToHoursWithMinutesDuration() {
		Duration expected = Duration.ofHours(7).plusMinutes(5);

		assertEquals(expected, DurationFormatter.toDuration("07:05"));
	}

	@Test
	void testToDaysWithHoursAndMinutesDuration() {
		Duration expected = Duration.ofDays(2).plusHours(17).plusMinutes(5);

		assertEquals(expected, DurationFormatter.toDuration("02:17:05"));
	}

	@Test
	void testToMinusDuration() {
		Duration expected = Duration.ofHours(-3).minusMinutes(11);

		assertEquals(expected, DurationFormatter.toDuration("-03:11"));
	}

	@Test
	void testToMinusText() {
		String expected = "-02:09:01";

		assertEquals(expected, DurationFormatter.toString(Duration.ofDays(-2).minusHours(9).minusMinutes(1)));
	}

}
