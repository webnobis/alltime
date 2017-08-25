package old.com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.persistence.EntryStore;

import old.com.webnobis.alltime.service.EntryService;

@Ignore
public class EntryServiceTest {

	private static final LocalDate DAY = LocalDate.of(-999999999, Month.AUGUST, 1);

	private static final LocalTime START = LocalTime.of(8, 30);

	private static final LocalTime NOW = LocalTime.of(10, 00);

	private static final LocalTime END = LocalTime.of(18, 25);

	private static final Duration EXPECTED_TIME = Duration.ofHours(8);

	private static final Duration IDLE_TIME_LIMIT = Duration.ofHours(9);

	private static final Duration IDLE_TIME = Duration.ofMinutes(45);

	private static final Duration REAL_TIME_START = Duration.ofHours(1).plusMinutes(30);

	private static final Duration REAL_TIME_START_END = Duration.ofHours(9).plusMinutes(55);

	private static final Duration TIME_ASSETS_BEFORE = Duration.ofDays(1).plusMinutes(30);

	private static final Duration TIME_ASSETS_START = Duration.ofHours(18);

	private static final Duration TIME_ASSETS_START_END = Duration.ofDays(1).plusHours(1).plusMinutes(40);

	private static final Duration TIME_ASSETS_GT = Duration.ofHours(16).plusMinutes(30);

	private static final Map<String, Duration> ITEMS = Collections.singletonMap("a key", Duration.ofMillis(Long.MAX_VALUE));

	private Entry lastEntry;

	private List<Entry> storedEntries;

	private EntryStore store;

	private EntryService service;

	@Before
	public void setUp() throws Exception {
		lastEntry = new DayEntry(DAY.minusMonths(7), EntryType.KR, TIME_ASSETS_BEFORE, Collections.emptyMap());
		storedEntries = new ArrayList<>();
		store = new EntryStore() {

			@Override
			public List<Entry> getLastEntries(int maxCount) {
				List<Entry> lastEntries = new ArrayList<>(storedEntries);
				lastEntries.add(0, lastEntry);
				return lastEntries.subList(0, Math.max(0, Math.min(lastEntries.size(), maxCount)));
			}

			@Override
			public void storeEntry(Entry entry) {
				storedEntries.add(entry);
			}

		};

		service = new EntryService(() -> NOW,
				Integer.MAX_VALUE,
				Collections.singletonMap(DayOfWeek.WEDNESDAY, EXPECTED_TIME),
				Collections.singletonMap(IDLE_TIME_LIMIT, IDLE_TIME),
				store);
	}

	@Test
	public void testStartAZ() {
		Entry e = service.startAZ(DAY, START);

		assertEquals(DAY, e.getDay());
		assertEquals(START, e.getStart());
		assertNull(e.getEnd());
		assertEquals(EXPECTED_TIME, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(REAL_TIME_START, e.getRealTime());
		assertEquals(TIME_ASSETS_START, e.getTimeAssets());
		assertEquals(Collections.emptyMap(), e.getItems());

		assertEquals(Collections.singletonList(e), storedEntries);
	}

	@Test
	public void testEndAZ() {
		Entry e = service.endAZ(DAY, START, END, ITEMS);

		assertEquals(DAY, e.getDay());
		assertEquals(START, e.getStart());
		assertEquals(END, e.getEnd());
		assertEquals(EXPECTED_TIME, e.getExpectedTime());
		assertEquals(IDLE_TIME, e.getIdleTime());
		assertEquals(REAL_TIME_START_END, e.getRealTime());
		assertEquals(TIME_ASSETS_START_END, e.getTimeAssets());
		assertEquals(ITEMS, e.getItems());

		assertEquals(Collections.singletonList(e), storedEntries);
	}

	@Test
	public void testBookSMDay() {
		Map<String, Duration> items = new HashMap<>();
		items.put("1st key", Duration.ofHours(2));
		items.put("2nd key", null);
		Entry e = service.book(DAY, EntryType.SM, items);

		assertEquals(DAY, e.getDay());
		assertEquals(LocalTime.of(0, 0), e.getStart());
		assertNull(e.getEnd());
		assertEquals(Duration.ZERO, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(Duration.ZERO, e.getRealTime());
		assertEquals(TIME_ASSETS_BEFORE, e.getTimeAssets());
		assertEquals(items, e.getItems());

		assertEquals(Collections.singletonList(e), storedEntries);
	}

	@Test
	public void testBookGTDay() {
		Entry e = service.book(DAY, EntryType.GT, ITEMS);

		assertEquals(DAY, e.getDay());
		assertEquals(LocalTime.of(0, 0), e.getStart());
		assertNull(e.getEnd());
		assertEquals(EXPECTED_TIME, e.getExpectedTime());
		assertEquals(Duration.ZERO, e.getIdleTime());
		assertEquals(Duration.ZERO, e.getRealTime());
		assertEquals(TIME_ASSETS_GT, e.getTimeAssets());
		assertEquals(ITEMS, e.getItems());

		assertEquals(Collections.singletonList(e), storedEntries);
	}

	@Test
	public void testBookURDayRange() {
		List<LocalDate> days = Stream.of(LocalDate.of(1999, Month.DECEMBER, 30),
				LocalDate.of(1999, Month.DECEMBER, 31),
				LocalDate.of(2000, Month.JANUARY, 1),
				LocalDate.of(2000, Month.JANUARY, 2),
				LocalDate.of(2000, Month.JANUARY, 3))
				.collect(Collectors.toList());

		List<Entry> expectedEntries = days.stream()
				.map(day -> new DayEntry(day, EntryType.UR, TIME_ASSETS_BEFORE, ITEMS))
				.collect(Collectors.toList());
		assertEquals(expectedEntries, service.book(days.get(0), days.get(days.size() - 1), EntryType.UR, ITEMS));

		assertEquals(expectedEntries, storedEntries);
	}

	@Test
	public void testGetLastEntries() {
		assertEquals(Collections.singletonList(lastEntry), service.getLastEntries());

		assertTrue(new EntryService(() -> null, 0, Collections.emptyMap(), Collections.emptyMap(), store).getLastEntries().isEmpty());
		assertTrue(new EntryService(() -> null, Integer.MIN_VALUE, Collections.emptyMap(), Collections.emptyMap(), store).getLastEntries().isEmpty());
	}

	@Test(expected = IllegalStateException.class)
	public void testBookAZFailed() {
		service.book(DAY, EntryType.AZ);
	}

}
