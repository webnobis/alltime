package com.webnobis.alltime.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AZEntryTest {

	private static final LocalDate DAY = LocalDate.of(2018, 8, 21);

	private static final LocalTime START = LocalTime.of(8, 30);

	private static final LocalTime END = LocalTime.of(16, 50);

	private static final Duration EXPECTED_TIME = Duration.ofHours(8);

	private static final Map<String, Duration> ITEMS = Collections.singletonMap("key", Duration.ofMinutes(1));

	private static final Duration IDLE_TIME = Duration.ofMinutes(30);

	private Entry entry;

	@BeforeEach
	void setUp() throws Exception {
		entry = new AZEntry(DAY, START, END, EXPECTED_TIME, IDLE_TIME, ITEMS);
	}

	@Test
	void testGetStart() {
		assertEquals(START, entry.getStart());
	}

	@Test
	void testGetEnd() {
		assertEquals(END, entry.getEnd());
	}

	@Test
	void testGetExpectedTime() {
		assertEquals(EXPECTED_TIME, entry.getExpectedTime());
	}

	@Test
	void testGetIdleTime() {
		assertEquals(IDLE_TIME, entry.getIdleTime());
	}

	@Test
	void testGetRealTime() {
		assertEquals(Duration.between(START, END), entry.getRealTime());
	}

	@Test
	void testGetTimeAssets() {
		testGetTimeAssets(Duration.between(START, END));
	}

	@Test
	void testGetTimeAssetsOverMidnight() {
		entry = new AZEntry(entry.getDay(), entry.getEnd(), entry.getStart(), entry.getExpectedTime(),
				entry.getIdleTime(), entry.getItems());
		testGetTimeAssets(Duration.between(END, START).plusDays(1));
	}

	private void testGetTimeAssets(Duration expectedStartEndRange) {
		Duration expectedTimeAssets = expectedStartEndRange.minus(EXPECTED_TIME).minus(IDLE_TIME);
		assertEquals(expectedTimeAssets, entry.getTimeAssets());
	}

	@Test
	void testGetDay() {
		assertEquals(DAY, entry.getDay());
	}

	@Test
	void testGetType() {
		assertEquals(EntryType.AZ, entry.getType());
	}

	@Test
	void testGetItems() {
		assertEquals(ITEMS, entry.getItems());
	}

}
