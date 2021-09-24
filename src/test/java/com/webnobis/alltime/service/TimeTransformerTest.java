package com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

public class TimeTransformerTest {

	private static final Supplier<LocalTime> NOW = () -> LocalTime.of(4, 51, 14);

	private static final int START_OFFSET = 2;

	private static final int END_OFFSET = 4;

	private static final int MINUTES_RASTER = 5;

	private TimeTransformer transformer;

	@Before
	public void setUp() throws Exception {
		transformer = new TimeTransformer(NOW, START_OFFSET, END_OFFSET, MINUTES_RASTER);
	}

	@Test
	public void testToTime() {
		LocalTime expected = LocalTime.of(23, 59);
		assertEquals(expected, TimeTransformer.toTime("23:59"));
	}

	@Test
	public void testToText() {
		String expected = "12:13";
		assertEquals(expected, TimeTransformer.toText(LocalTime.of(12, 13, 59)));
	}

	@Test
	public void testStartNow() {
		LocalTime expected = LocalTime.of(4, 45);
		assertEquals(expected, transformer.now(true));
	}

	@Test
	public void testEndNow() {
		LocalTime expected = LocalTime.of(5, 0);
		assertEquals(expected, transformer.now(false));
	}

}
