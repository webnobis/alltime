package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class DayTransformer {
	
	private DayTransformer() {}
	
	private static final String DAY_FORMAT = "dd.MM.yyyy";
	
	public static LocalDate toDay(String text) {
		return LocalDate.parse(text, DateTimeFormatter.ofPattern(DAY_FORMAT));
	}
	
	public static String toText(LocalDate day) {
		return day.format(DateTimeFormatter.ofPattern(DAY_FORMAT));
	}

}
