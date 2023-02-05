package com.webnobis.alltime.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record DayTransformer() {

	private static final String DAY_FORMAT = "dd.MM.yyyy";

	public static LocalDate toDay(String text) {
		if (text == null || text.isEmpty()) {
			return null;
		}

		return LocalDate.parse(text, DateTimeFormatter.ofPattern(DAY_FORMAT));
	}

	public static String toText(LocalDate day) {
		if (day == null) {
			return null;
		}

		return day.format(DateTimeFormatter.ofPattern(DAY_FORMAT));
	}

}
