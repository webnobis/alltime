package com.webnobis.alltime.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public abstract class WeekDayTransformer {

	private WeekDayTransformer() {
	};

	private static final List<String> WEEKDAYS = Arrays.asList("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So");

	public static DayOfWeek toWeekday(String text) {
		if (text == null || text.isEmpty() || !WEEKDAYS.contains(text)) {
			return null;
		}

		return DayOfWeek.of(WEEKDAYS.indexOf(text) + 1);
	}

	public static String toText(DayOfWeek weekday) {
		if (weekday == null) {
			return null;
		}

		return WEEKDAYS.get(weekday.getValue() - 1);
	}

	public static String toText(LocalDate day) {
		if (day == null) {
			return null;
		}

		return toText(DayOfWeek.from(day));
	}

}
