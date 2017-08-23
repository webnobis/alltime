package com.webnobis.alltime.view;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class TimeTransformer {
	
	private TimeTransformer() {}
	
	private static final String TIME_FORMAT = "HH:mm";
	
	public static LocalTime toTime(String text) {
		return LocalTime.parse(text, DateTimeFormatter.ofPattern(TIME_FORMAT));
	}
	
	public static String toText(LocalTime time) {
		return time.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

}
