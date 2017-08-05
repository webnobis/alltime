package com.webnobis.alltime.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.ExpectedTime;
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
		return new AZEntry(day, start, now, expectedTime.getExpectedTime(DayOfWeek.from(day)), idleTime.getIdleTime(day, start, now), timeAssetsBefore);
	}

	public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Map<String, Duration> items) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		return new AZEntry(day, start, end, expectedTime.getExpectedTime(DayOfWeek.from(day)), idleTime.getIdleTime(day, start, end), timeAssetsBefore, items);
	}

	public Entry book(LocalDate day, EntryType type, Map<String, Duration> items) {
		Duration timeAssetsBefore = getTimeAssetsBefore(day);
		return new DayEntry(day, type, timeAssetsBefore, items);
	}

	public Entry book(LocalDate day, EntryType type) {
		return book(day, type, Collections.emptyMap());
	}

}
