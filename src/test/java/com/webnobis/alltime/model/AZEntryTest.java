package com.webnobis.alltime.model;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AZEntryTest {

	private static final LocalDate DAY = LocalDate.of(2018, 8, 21);
	
	private static final LocalTime START = LocalTime.of(8, 30);
	
	private static final LocalTime END = LocalTime.of(16, 50);

	private static final Duration EXPECTED_TIME = Duration.ofHours(8);

	private static final Map<String, Duration> ITEMS = Collections.singletonMap("key", Duration.ofMinutes(1));

	private static final Duration IDLE_TIME = Duration.ofMinutes(30);
	
	private Entry entry;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		entry = new AZEntry(DAY, START, END, EXPECTED_TIME, IDLE_TIME, ITEMS);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetStart() {
		assertEquals(START, entry.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(END, entry.getEnd());
	}

	@Test
	public void testGetExpectedTime() {
		assertEquals(EXPECTED_TIME, entry.getExpectedTime());
	}

	@Test
	public void testGetIdleTime() {
		assertEquals(IDLE_TIME, entry.getIdleTime());
	}

	@Test
	public void testGetRealTime() {
		assertEquals(Duration.between(START, END), entry.getRealTime());
	}

	@Test
	public void testGetTimeAssets() {
		testGetTimeAssets(Duration.between(START, END));
	}

	@Test
	public void testGetTimeAssetsOverMidnight() {
		entry = new AZEntry(entry.getDay(), entry.getEnd(), entry.getStart(), entry.getExpectedTime(), entry.getIdleTime(), entry.getItems());
		testGetTimeAssets(Duration.between(END, START).plusDays(1));
	}

	private void testGetTimeAssets(Duration expectedStartEndRange) {
		Duration expectedTimeAssets = expectedStartEndRange.minus(EXPECTED_TIME).minus(IDLE_TIME);
		assertEquals(expectedTimeAssets, entry.getTimeAssets());
	}

	@Test
	public void testGetDay() {
		assertEquals(DAY, entry.getDay());
	}

	@Test
	public void testGetType() {
		assertEquals(EntryType.AZ, entry.getType());
	}

	@Test
	public void testGetItems() {
		assertEquals(ITEMS, entry.getItems());
	}

}
