package com.webnobis.alltime.service;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public abstract class DurationFormatter {

	private static final int HOUR_MINUTES = 60;

	private static final int DAY_HOURS = 24;

	private static final String TIME_SEPARATOR = ":";

	private static final String TWICE_ZERO = "00";

	private static final Pattern durationPattern = Pattern.compile("^([0-9]{2})".concat(TIME_SEPARATOR).concat("([0-9]{2})$"));

	private DurationFormatter() {
	}

	public static String toString(Duration duration) {
		long minutes = duration.toMinutes();
		long hours = minutes / HOUR_MINUTES;
		minutes %= HOUR_MINUTES;
		long days = hours / DAY_HOURS;
		hours %= DAY_HOURS;

		LongStream stream;
		if (days > 0) {
			stream = LongStream.of(days, hours, minutes);
		} else {
			stream = LongStream.of(hours, minutes);
		}
		return stream.mapToObj(l -> new DecimalFormat(TWICE_ZERO).format(l))
				.collect(Collectors.joining(TIME_SEPARATOR));
	}

	public static Duration toDuration(String duration) {
		Matcher matcher = durationPattern.matcher(duration);
		if (!matcher.find()) {
			return null;
		}

		return Duration.ofHours(Integer.parseInt(matcher.group(1))).plusMinutes(Integer.parseInt(matcher.group(2)));
	}

}
