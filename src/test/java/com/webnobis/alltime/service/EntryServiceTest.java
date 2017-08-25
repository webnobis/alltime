package com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.persistence.EntryStore;

public class EntryServiceTest {

	private static final Duration TIME_ASSETS_SUM_BEFORE = Duration.ofDays(1).plusHours(2).plusMinutes(30);

	private static final Duration EXPECTED_TIME1 = Duration.ofHours(8);

	private static final Duration EXPECTED_TIME2 = Duration.ofHours(6);

	private static final Duration IDLE_TIME_LIMIT = Duration.ofHours(6);

	private static final Duration IDLE_TIME = Duration.ofMinutes(30);

	private static final Duration REAL_TIME1 = Duration.ofHours(9).plusMinutes(30);

	private static final Duration TIME_ASSETS1 = Duration.ofHours(1);

	private static final LocalDate DAY1 = LocalDate.of(2017, Month.AUGUST, 24);

	private static final LocalDate DAY2 = LocalDate.of(2017, Month.SEPTEMBER, 1);

	private static final LocalDate DAY3 = LocalDate.of(2000, Month.JANUARY, 1);

	private static final LocalTime START1 = LocalTime.of(7, 15);

	private static final LocalTime END1 = LocalTime.of(16, 45);

	private static final LocalTime START2 = LocalTime.of(8, 0);

	private static final LocalTime END2 = LocalTime.of(16, 0);

	private static Map<DayOfWeek, Duration> expectedTimes;

	private static Map<Duration, Duration> idleTimes;
	
	private static Map<String, Duration> items;

	private EntryService service;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expectedTimes = new HashMap<>();
		expectedTimes.put(DayOfWeek.WEDNESDAY, EXPECTED_TIME1);
		expectedTimes.put(DayOfWeek.FRIDAY, EXPECTED_TIME2);
		expectedTimes.put(DayOfWeek.SATURDAY, Duration.ZERO);

		idleTimes = Collections.singletonMap(IDLE_TIME_LIMIT, IDLE_TIME);
		
		items = Collections.singletonMap("a key", Duration.ofMillis(1));
	}

	@Before
	public void setUp() throws Exception {
		service = new EntryService(expectedTimes, new IdleTimeHandler(idleTimes), new TestEntryStore());
	}
	
	@Test
	public void testStartAZ() {
		Entry e = service.startAZ(DAY1, START1);
		
		assertEquals(DAY1, e.getDay());
		assertEquals(START1, e.getStart());
		assertNull(e.getEnd());
		assertEquals(EXPECTED_TIME1, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(Duration.ZERO, e.getRealTime());
		assertEquals(EXPECTED_TIME1.negated(), e.getTimeAssets());
		assertEquals(Collections.emptyMap(), e.getItems());
	}
	
	@Test
	public void testEndAZ() {
		Entry e = service.endAZ(DAY1, START1, END1, items);
		
		assertEquals(DAY1, e.getDay());
		assertEquals(START1, e.getStart());
		assertEquals(END1, e.getEnd());
		assertEquals(EXPECTED_TIME1, e.getExpectedTime());
		assertEquals(IDLE_TIME, e.getIdleTime());
		assertEquals(REAL_TIME1, e.getRealTime());
		assertEquals(TIME_ASSETS1, e.getTimeAssets());
		assertEquals(items, e.getItems());
	}

	private class TestEntryStore implements EntryStore {

		private final Map<LocalDate, Entry> entries = new HashMap<>();

		@Override
		public List<LocalDate> getLastDays() {
			return new ArrayList<>(entries.keySet());
		}

		@Override
		public Entry getEntry(LocalDate day) {
			return entries.get(day);
		}

		@Override
		public Duration getTimeAssetsSumBefore(LocalDate day) {
			return TIME_ASSETS_SUM_BEFORE;
		}

		@Override
		public Entry storeEntry(Entry entry) {
			entries.put(entry.getDay(), entry);
			return entry;
		}

	}

}
