package com.webnobis.alltime.persistence;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.Test;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;

public class LineToEntryDeserializerTest {

	private static final Function<Entry, String> serializer = EntryToLineSerializer::toLine;

	private static final Function<String, Entry> deserializer = LineToEntryDeserializer::toEntry;

	@Test
	public void testToAZEntry() {
		Entry expected = new AZEntry(LocalDate.of(2000, 3, 4), LocalTime.of(7, 5), LocalTime.of(16, 2), Duration.ZERO, Duration.ofMinutes(2), Collections.emptyMap());
		String line = serializer.apply(expected);

		assertEquals(expected, deserializer.apply(line));
	}

	@Test
	public void testToGTEntry() {
		Entry expected = new GTEntry(LocalDate.of(2002, 5, 27), Duration.ofHours(2), Collections.emptyMap());
		String line = serializer.apply(expected);

		assertEquals(expected, deserializer.apply(line));
	}

	@Test
	public void testToDayEntry() {
		Entry expected = new DayEntry(LocalDate.of(2007, 12, 31), EntryType.KR, LongStream.rangeClosed(1, 60 * 60)
				.boxed()
				.collect(Collectors.toMap(String::valueOf, Duration::ofMinutes)));
		String line = serializer.apply(expected);

		assertEquals(expected, deserializer.apply(line));
	}

}
