package com.webnobis.alltime.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.ExpectedTime;
import com.webnobis.alltime.model.GTEntry;
import com.webnobis.alltime.model.IdleTime;
import com.webnobis.alltime.persistence.EntryStore;

public class EntryService {

	private final Supplier<LocalTime> now;

	private final ExpectedTime expectedTime;

	private final IdleTime idleTime;

	private final EntryStore store;

	public EntryService(Supplier<LocalTime> now, ExpectedTime expectedTime, IdleTime idleTime, EntryStore store) {
		this.now = now;
		this.expectedTime = expectedTime;
		this.idleTime = idleTime;
		this.store = store;
	}

	private Duration getTimeAssetsBefore(LocalDate day) {
		return store.getLastEntries(2).stream()
				.filter(entry -> entry.getDay().isBefore(day))
				.findFirst()
				.map(entry -> entry.getTimeAssets())
				.orElse(Duration.ZERO);
	}

	public Entry startAZ(LocalDate day, LocalTime start) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		LocalTime now = this.now.get();
		Entry entry = new AZEntry(day, start, now, expectedTime.getExpectedTime(DayOfWeek.from(day)), idleTime.getIdleTime(day, start, now), timeAssetsBefore);
		store.storeEntry(entry);
		return entry;
	}

	public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Map<String, Duration> items) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		Entry entry = new AZEntry(day, start, end, expectedTime.getExpectedTime(DayOfWeek.from(day)), idleTime.getIdleTime(day, start, end), timeAssetsBefore, items);
		store.storeEntry(entry);
		return entry;
	}

	public Entry book(LocalDate day, EntryType type, Map<String, Duration> items) {
		if (EntryType.AZ.equals(type)) {
			throw new IllegalStateException(String.format("Please use 'startAZ' and 'endAZ' for %s type.", EntryType.AZ));
		}
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		Entry entry;
		if (EntryType.GT.equals(type)) {
			entry = new GTEntry(day, expectedTime.getExpectedTime(DayOfWeek.from(day)), timeAssetsBefore, items);
		} else {
			entry = new DayEntry(day, type, timeAssetsBefore, items);
		}
		store.storeEntry(entry);
		return entry;
	}

	public Entry book(LocalDate day, EntryType type) {
		return book(day, type, Collections.emptyMap());
	}

	public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type, Map<String, Duration> items) {
		long days = Period.between(fromDay, untilDay).getDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(l -> fromDay.plusDays(l))
				.map(day -> book(day, type, items))
				.collect(Collectors.toList());
	}

	public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type) {
		return book(fromDay, untilDay, type, Collections.emptyMap());
	}

	public List<Entry> getLastEntries(int maxCount) {
		return store.getLastEntries(maxCount);
	}

}
