package com.webnobis.alltime.view;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class TimeTransformer {

	private static final String TIME_FORMAT = "HH:mm";

	private final Supplier<LocalTime> now;

	private final int startOffset;

	private final int endOffset;

	private final int minutesRaster;

	public TimeTransformer(Supplier<LocalTime> now, int startOffset, int endOffset, int minutesRaster) {
		this.now = now;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.minutesRaster = minutesRaster;
	}

	public static LocalTime toTime(String text) {
		if (text == null || text.isEmpty()) {
			return null;
		}

		return LocalTime.parse(text, DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

	public static String toText(LocalTime time) {
		if (time == null) {
			return null;
		}

		return time.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

	public String nowToText(boolean start) {
		LocalTime time = (start) ? now.get().minusMinutes(startOffset) : now.get().plusMinutes(endOffset);
		int minutes = (int) (time.getMinute() / (double) minutesRaster) * minutesRaster;
		time = (start) ? time.withMinute(minutes) : time.withMinute(minutes + minutesRaster);
		return toText(time);
	}

}
