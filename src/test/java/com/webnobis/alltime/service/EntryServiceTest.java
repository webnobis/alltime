package com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
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

	private static final LocalTime START = LocalTime.of(7, 15);

	private static final LocalTime END = LocalTime.of(16, 45);

	private static Map<DayOfWeek, Duration> expectedTimes;

	private static Map<Duration, Duration> idleTimes;

	private static Map<String, Duration> items;

	private EntryService service;

	private EntryStore store;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		expectedTimes = new HashMap<>();
		expectedTimes.put(DayOfWeek.THURSDAY, EXPECTED_TIME1);
		expectedTimes.put(DayOfWeek.FRIDAY, EXPECTED_TIME2);
		expectedTimes.put(DayOfWeek.SATURDAY, Duration.ZERO);

		idleTimes = Collections.singletonMap(IDLE_TIME_LIMIT, IDLE_TIME);

		items = Collections.singletonMap("a key", Duration.ofMillis(1));
	}

	@Before
	public void setUp() throws Exception {
		store = new TestEntryStore();
		service = new EntryService(expectedTimes, new IdleTimeHandler(idleTimes), store);
	}

	@Test
	public void testStartAZ() {
		Entry e = service.startAZ(DAY1, START);

		assertEquals(DAY1, e.getDay());
		assertEquals(EntryType.AZ, e.getType());
		assertEquals(START, e.getStart());
		assertNull(e.getEnd());
		assertEquals(EXPECTED_TIME1, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(Duration.ZERO, e.getRealTime());
		assertEquals(EXPECTED_TIME1.negated(), e.getTimeAssets());
		assertEquals(Collections.emptyMap(), e.getItems());
	}

	@Test
	public void testEndAZ() {
		Entry e = service.endAZ(DAY1, START, END, items);

		assertEquals(DAY1, e.getDay());
		assertEquals(EntryType.AZ, e.getType());
		assertEquals(START, e.getStart());
		assertEquals(END, e.getEnd());
		assertEquals(EXPECTED_TIME1, e.getExpectedTime());
		assertEquals(IDLE_TIME, e.getIdleTime());
		assertEquals(REAL_TIME1, e.getRealTime());
		assertEquals(TIME_ASSETS1, e.getTimeAssets());
		assertEquals(items, e.getItems());
	}

	@Test
	public void testBookGT() {
		Entry e = service.book(DAY2, EntryType.GT, Collections.emptyMap());

		assertEquals(DAY2, e.getDay());
		assertEquals(EntryType.GT, e.getType());
		assertNull(e.getStart());
		assertNull(e.getEnd());
		assertEquals(EXPECTED_TIME2, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(Duration.ZERO, e.getRealTime());
		assertEquals(EXPECTED_TIME2.negated(), e.getTimeAssets());
		assertEquals(Collections.emptyMap(), e.getItems());
	}

	@Test
	public void testBookURRange() {
		List<Entry> entries = service.book(DAY1, DAY2, EntryType.UR, items);
		assertEquals(9, entries.size());

		assertEquals(DAY1, entries.get(0).getDay());
		assertEquals(DAY2, entries.get(entries.size() - 1).getDay());
		entries.forEach(e -> {
			assertEquals(EntryType.UR, e.getType());
			assertNull(e.getStart());
			assertNull(e.getEnd());
			assertEquals(Duration.ZERO, e.getExpectedTime());
			assertEquals(Duration.ZERO, e.getIdleTime());
			assertEquals(Duration.ZERO, e.getRealTime());
			assertEquals(Duration.ZERO, e.getTimeAssets());
			assertEquals(items, e.getItems());
		});
	}

	@Test(expected = IllegalStateException.class)
	public void testBookAZFailed() {
		service.book(DAY1, EntryType.AZ, Collections.emptyMap());
	}

	@Test
	public void testGetLastDays() {
		Stream.of(DAY1, DAY2, DAY3)
				.map(day -> new DayEntry(day, EntryType.KR, items))
				.forEach(store::storeEntry);

		List<LocalDate> expected = Arrays.asList(DAY3, DAY1, DAY2);
		assertEquals(expected, service.getLastDays());
	}

	@Test
	public void testGetEntry() {
		Entry expected = new DayEntry(DAY3, EntryType.KR, items);
		store.storeEntry(expected);

		assertEquals(expected, service.getEntry(DAY3));
		assertNull(service.getEntry(DAY2));
	}

	@Test
	public void testGetTimeAssetsSum() {
		TimeAssetsSum expected = new TimeAssetsSum(DAY2, TIME_ASSETS_SUM_BEFORE);
		assertEquals(expected, service.getTimeAssetsSumBefore(DAY2));
	}

	@Test
	public void testGetLastDescriptions() {
		assertEquals(items.keySet(), new HashSet<>(service.getLastDescriptions()));
	}

	private class TestEntryStore implements EntryStore {

		private final Map<LocalDate, Entry> entries = new HashMap<>();

		@Override
		public List<LocalDate> getLastDays() {
			return new ArrayList<>(new TreeSet<>(entries.keySet()));
		}

		@Override
		public Entry getEntry(LocalDate day) {
			return entries.get(day);
		}

		@Override
		public TimeAssetsSum getTimeAssetsSumBefore(LocalDate day) {
			return new TimeAssetsSum(day, TIME_ASSETS_SUM_BEFORE);
		}

		@Override
		public Entry storeEntry(Entry entry) {
			entries.put(entry.getDay(), entry);
			return entry;
		}

		@Override
		public List<String> getLastDescriptions() {
			return new ArrayList<>(items.keySet());
		}

	}

}
