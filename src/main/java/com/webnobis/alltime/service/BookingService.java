package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;

public interface BookingService {

	default Entry startAZ(LocalDate day, LocalTime start) {
		return endAZ(day, start, null, Collections.emptyMap());
	}

	Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Map<String, Duration> items);

	default Entry book(LocalDate day, EntryType type) {
		return book(day, type, Collections.emptyMap());
	}

	default Entry book(LocalDate day, EntryType type, Map<String, Duration> items) {
		return book(day, day, type, items).stream().findFirst().orElse(null);
	}

	default List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type) {
		return book(fromDay, untilDay, type, Collections.emptyMap());
	}

	List<Entry> book(LocalDate fromDay, LocalDate untilDay, EntryType type, Map<String, Duration> items);

}