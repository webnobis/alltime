package com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;

import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DurationFormatterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToHoursWithMinutesDuration() {
		Duration expected = Duration.ofHours(7).plusMinutes(5);

		assertEquals(expected, DurationFormatter.toDuration("07:05"));
	}

	@Test
	public void testToDaysWithHoursAndMinutesDuration() {
		Duration expected = Duration.ofDays(2).plusHours(17).plusMinutes(5);

		assertEquals(expected, DurationFormatter.toDuration("02:17:05"));
	}

}
