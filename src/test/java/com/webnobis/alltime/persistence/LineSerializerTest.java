package com.webnobis.alltime.persistence;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;

public class LineSerializerTest {
	
	private static final LocalDate DAY = LocalDate.of(1999, Month.DECEMBER, 31);
	
	private static final LocalTime START = LocalTime.of(8, 30);
	
	private static final LocalTime END = LocalTime.of(17, 15);
	
	private static final Duration EXPECTED_TIME = Duration.ofHours(8);
	
	private static final Duration IDLE_TIME = Duration.ofMinutes(5);
	
	private static final Duration TIME_ASSETS_BEFORE = Duration.ofMinutes(1).plusDays(1);
	
	private static final String ITEM1_KEY = "Projekt mit ß";
	
	private static final Duration ITEM1_VALUE = Duration.ofHours(4).plusMinutes(30);
	
	private static final String ITEM2_KEY = "Etwas Schöneres";
	
	private static final Duration ITEM2_VALUE = Duration.ofHours(3).plusMinutes(15);
	
	private static final String AZ_LINE = "31.12.1999;AZ;08:30;17:15;R08:45;E08:00;I00:05;A01:00:41;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;
	
	private static final String UR_LINE = "31.12.1999;UR;00:00;-;R00:00;E00:00;I00:00;A01:00:01;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;
	
	private static final String GT_LINE = "31.12.1999;GT;00:00;-;R00:00;E08:00;I00:00;A16:01;03:15;" + ITEM2_KEY + ";04:30;" + ITEM1_KEY;
	
	private static Map<String,Duration> items;
	
	private Function<Entry, String> serializer;

	@BeforeClass
	public static void setUpClass() {
		items = new HashMap<>();
		items.put(ITEM1_KEY, ITEM1_VALUE);
		items.put(ITEM2_KEY, ITEM2_VALUE);
	}
	
	@Before
	public void setUp() {
		serializer = LineSerializer::toLine;
	}

	@Test
	public void testAZEntryToLine() {
		Entry entry = new AZEntry(DAY, START, END, EXPECTED_TIME, IDLE_TIME, TIME_ASSETS_BEFORE, items);
		
		assertEquals(AZ_LINE, serializer.apply(entry));
	}

	@Test
	public void testUREntryToLine() {
		Entry entry = new DayEntry(DAY, EntryType.UR, TIME_ASSETS_BEFORE, items);
		
		assertEquals(UR_LINE, serializer.apply(entry));
	}

	@Test
	public void testGTEntryToLine() {
		Entry entry = new GTEntry(DAY, EXPECTED_TIME, TIME_ASSETS_BEFORE, items);
		
		assertEquals(GT_LINE, serializer.apply(entry));
	}

}
