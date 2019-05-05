package com.webnobis.alltime.view.entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.BookingService;

import javafx.scene.control.ButtonType;

@Ignore
public class EntryRangeDialogTest {

	private static final LocalDate SA = LocalDate.of(2018, 6, 23);

	private static final LocalDate SO = SA.plusDays(1);

	private static final LocalDate MO = SO.plusDays(1);

	private static final List<Entry> ENTRIES = Collections.emptyList();

	private TestBookingService service;

//	@ClassRule
//	public static TestRule javaFxInitRule = new JavaFxInitRule();

//	@Rule
//	public TestRule javaFxRule = new JavaFxRule();

	@Before
	public void setUp() throws Exception {
		service = new TestBookingService();
	}

	@Test
	public void testWE() {
		EntryRangeDialog dialog = new EntryRangeDialog(service, 30, SA, SO, new TimeAssetsSum(SA, Duration.ZERO), Collections.emptyList(), Optional.empty());
		assertEquals(ENTRIES, dialog.get(ButtonType.APPLY));

		assertEquals(SA, service.getFromDay());
		assertEquals(SO, service.getUntilDay());
		assertEquals(EntryType.WE, service.getType());
		assertEquals(Collections.emptyMap(), service.getItems());
	}

	@Test
	public void testUR() {
		EntryRangeDialog dialog = new EntryRangeDialog(service, 30, SO, MO, new TimeAssetsSum(SO, Duration.ZERO), Collections.emptyList(), Optional.empty());
		assertEquals(ENTRIES, dialog.get(ButtonType.APPLY));

		assertEquals(SO, service.getFromDay());
		assertEquals(MO, service.getUntilDay());
		assertEquals(EntryType.UR, service.getType());
		assertEquals(Collections.emptyMap(), service.getItems());
	}

	private class TestBookingService implements BookingService {

		private LocalDate fromDay;

		private LocalDate untilDay;

		private EntryType type;

		private Map<String, Duration> items;

		@Override
		public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Duration idleTime, Map<String, Duration> items) {
			fail("unexpected call");
			return null;
		}

		@Override
		public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type, Map<String, Duration> items) {
			this.fromDay = fromDay;
			this.untilDay = untilDay;
			this.type = type;
			this.items = items;
			return ENTRIES;
		}

		public LocalDate getFromDay() {
			return fromDay;
		}

		public LocalDate getUntilDay() {
			return untilDay;
		}

		public EntryType getType() {
			return type;
		}

		public Map<String, Duration> getItems() {
			return items;
		}

	}

}
