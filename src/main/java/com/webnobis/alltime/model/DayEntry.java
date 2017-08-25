package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class DayEntry extends AbstractEntry implements Entry {

	public DayEntry(LocalDate day, EntryType type, Map<String, Duration> items) {
		super(day, type, items);
	}

}
