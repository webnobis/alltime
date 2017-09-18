package com.webnobis.alltime.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.persistence.EntryStore;

public class EntryService implements FindService, BookingService {

	private final Map<DayOfWeek, Duration> expectedTimes;

	private final IdleTimeHandler idleTimeHandler;

	private final EntryStore store;

	public EntryService(Map<DayOfWeek, Duration> expectedTimes, IdleTimeHandler idleTimeHandler, EntryStore store) {
		this.expectedTimes = expectedTimes;
		this.idleTimeHandler = idleTimeHandler;
		this.store = store;
	}

	@Override
	public TimeAssetsSum getTimeAssetsSumBefore(LocalDate day) {
		return store.getTimeAssetsSumBefore(day);
	}

	@Override
	public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Duration idleTime, Map<String, Duration> items) {
		Objects.requireNonNull(day, "day is null");

		return store.storeEntry(new AZEntry(day, start, end, expectedTimes.get(DayOfWeek.from(day)), idleTimeHandler.getIdleTime(day, start, end, idleTime), items));
	}

	@Override
	public List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type, Map<String, Duration> items) {
		if (EntryType.AZ.equals(type)) {
			throw new IllegalStateException(String.format("Please use 'startAZ' and 'endAZ' for %s type.", EntryType.AZ));
		}
		Objects.requireNonNull(fromDay, "fromDay is null");
		Objects.requireNonNull(untilDay, "untilDay is null");

		boolean gtType = EntryType.GT.equals(type);
		long days = Period.between(fromDay, untilDay).getDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(l -> fromDay.plusDays(l))
				.<Entry>map(day -> (gtType) ? new GTEntry(day, expectedTimes.get(DayOfWeek.from(day)), items) : new DayEntry(day, type, items))
				.map(store::storeEntry)
				.collect(Collectors.toList());
	}

	@Override
	public List<LocalDate> getLastDays() {
		return store.getLastDays();
	}

	@Override
	public Entry getEntry(LocalDate day) {
		return store.getEntry(day);
	}

	@Override
	public List<String> getLastDescriptions() {
		return store.getLastDescriptions();
	}

}
