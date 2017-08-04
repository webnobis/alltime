package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;

import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.persistence.EntryStore;

public class EntryService {

	private final EntryStore store;

	public Entry startAZ(LocalDate day, LocalTime start) {
		return null; // TODO
	}

	public Entry endAZ(LocalDate day, LocalTime start, LocalTime end, Map<String, Duration> items) {
		return null; // TODO
	}

	public Entry book(LocalDate day, EntryType type, Map<String, Duration> items) {
		return new DayEntry(day, type, items);
	}

	public Entry book(LocalDate day, EntryType type) {
		return book(day, type, Collections.emptyMap());
	}

}
