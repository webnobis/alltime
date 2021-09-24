package com.webnobis.alltime.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;

class EntryToLineSerializerTest {

	private static final LocalDate DAY = LocalDate.of(1999, Month.DECEMBER, 31);

	private static final LocalTime START = LocalTime.of(8, 30);

	private static final LocalTime END = LocalTime.of(17, 15);

	private static final Duration EXPECTED_TIME = Duration.ofHours(8);

	private static final Duration IDLE_TIME = Duration.ofMinutes(5);

	private static final String ITEM1_KEY = "Projekt mit ß";

	private static final Duration ITEM1_VALUE = Duration.ofHours(4).plusMinutes(30);

	private static final String ITEM2_KEY = "Etwas Schöneres";

	private static final Duration ITEM2_VALUE = Duration.ofHours(3).plusMinutes(15);

	private static final String AZ_LINE = "31.12.1999;AZ;08:30;17:15;R08:45;E08:00;I00:05;A00:40;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;

	private static final String UR_LINE = "31.12.1999;UR;-;-;R00:00;E00:00;I00:00;A00:00;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;

	private static final String GT_LINE = "31.12.1999;GT;-;-;R00:00;E08:00;I00:00;A-08:00;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;

	private static Map<String, Duration> items;

	private Function<Entry, String> serializer;

	@BeforeAll
	static void setUpClass() {
		items = new HashMap<>();
		items.put(ITEM1_KEY, ITEM1_VALUE);
		items.put(ITEM2_KEY, ITEM2_VALUE);
	}

	@BeforeEach
	void setUp() {
		serializer = EntryToLineSerializer::toLine;
	}

	@Test
	void testAZEntryToLine() {
		Entry entry = new AZEntry(DAY, START, END, EXPECTED_TIME, IDLE_TIME, items);

		assertEquals(AZ_LINE, serializer.apply(entry));
	}

	@Test
	void testUREntryToLine() {
		Entry entry = new DayEntry(DAY, EntryType.UR, items);

		assertEquals(UR_LINE, serializer.apply(entry));
	}

	@Test
	void testGTEntryToLine() {
		Entry entry = new GTEntry(DAY, EXPECTED_TIME, items);

		assertEquals(GT_LINE, serializer.apply(entry));
	}

}
