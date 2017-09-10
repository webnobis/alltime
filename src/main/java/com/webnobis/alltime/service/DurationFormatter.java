package com.webnobis.alltime.service;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public abstract class DurationFormatter {

	private static final int HOUR_MINUTES = 60;

	private static final int DAY_HOURS = 24;

	private static final String TIME_SEPARATOR = ":";

	private static final String TWICE_ZERO = "00";

	private static final Pattern durationPattern = Pattern.compile("^(-?)([0-9]{0,2})".concat(TIME_SEPARATOR).concat("?([0-9]{2})").concat(TIME_SEPARATOR).concat("([0-9]{2})$"));

	private DurationFormatter() {
	}

	public static String toString(Duration duration) {
		long minutes = Objects.requireNonNull(duration, "duration is null").toMinutes();
		boolean negative = minutes < 0;
		long hours = minutes / HOUR_MINUTES;
		minutes = Math.abs(minutes % HOUR_MINUTES);
		long days = Math.abs(hours / DAY_HOURS);
		hours = Math.abs(hours % DAY_HOURS);

		LongStream stream;
		if (days > 0) {
			stream = LongStream.of(days, hours, minutes);
		} else {
			stream = LongStream.of(hours, minutes);
		}
		return ((negative) ? "-" : "").concat(stream.mapToObj(l -> new DecimalFormat(TWICE_ZERO).format(l))
				.collect(Collectors.joining(TIME_SEPARATOR)));
	}

	public static Duration toDuration(String duration) {
		Matcher matcher = durationPattern.matcher(Objects.requireNonNull(duration, "duration is null"));
		if (!matcher.find()) {
			return null;
		}

		boolean negative = "-".equals(matcher.group(1));
		int days = (matcher.group(2).isEmpty()) ? 0 : Integer.parseInt(matcher.group(2));
		int hours = Integer.parseInt(matcher.group(3));
		int minutes = Integer.parseInt(matcher.group(4));
		if (negative) {
			return Duration.ofDays(days).minusHours(hours).minusMinutes(minutes);
		} else {
			return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
		}
	}

}
