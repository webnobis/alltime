package com.webnobis.alltime.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TimeTransformerTest {

	private static final Supplier<LocalTime> NOW = () -> LocalTime.of(4, 51, 14);

	private static final int START_OFFSET = 2;

	private static final int END_OFFSET = 4;

	private static final int MINUTES_RASTER = 5;

	private TimeTransformer transformer;

	@BeforeEach
	void setUp() throws Exception {
		transformer = new TimeTransformer(NOW, START_OFFSET, END_OFFSET, MINUTES_RASTER);
	}

	@Test
	void testToTime() {
		LocalTime expected = LocalTime.of(23, 59);
		assertEquals(expected, TimeTransformer.toTime("23:59"));
	}

	@Test
	void testToText() {
		String expected = "12:13";
		assertEquals(expected, TimeTransformer.toText(LocalTime.of(12, 13, 59)));
	}

	@Test
	void testStartNow() {
		LocalTime expected = LocalTime.of(4, 45);
		assertEquals(expected, transformer.now(true));
	}

	@Test
	void testEndNow() {
		LocalTime expected = LocalTime.of(5, 0);
		assertEquals(expected, transformer.now(false));
	}

}
