package com.webnobis.alltime.persistence;

import static com.webnobis.alltime.persistence.LineDefinition.ATTRIBUTE_SEPARATOR;
import static com.webnobis.alltime.persistence.LineDefinition.MISSING_VALUE;
import static com.webnobis.alltime.persistence.LineDefinition.TIME_FORMAT;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;
import com.webnobis.alltime.service.DurationFormatter;

public abstract class LineToEntryDeserializer {

	private static final int MIN_ATTRIBUTE_SEPARATOR = 8;

	private static final Pattern durationPattern = Pattern.compile("^?(.+)$");

	private LineToEntryDeserializer() {
	}

	public static Entry toEntry(String line) {
		if (!line.contains(ATTRIBUTE_SEPARATOR)) {
			throw new NoSuchElementException(String.format("missing attributes, separated with %s within line: %s", ATTRIBUTE_SEPARATOR, line));
		}
		String[] attributes = line.split(ATTRIBUTE_SEPARATOR);
		if (attributes.length < MIN_ATTRIBUTE_SEPARATOR) {
			throw new NoSuchElementException(String.format("found %d instead of %d attributes within line: %s", attributes.length, MIN_ATTRIBUTE_SEPARATOR, line));
		}

		LocalDate day = LineToDayDeserializer.toDay(line);
		EntryType type = toType(attributes[1]);
		Duration expectedTime = toDuration(attributes[5]);
		Map<String, Duration> items;
		if (attributes.length > MIN_ATTRIBUTE_SEPARATOR) {
			items = toItems(Arrays.copyOfRange(attributes, MIN_ATTRIBUTE_SEPARATOR, attributes.length));
		} else {
			items = Collections.emptyMap();
		}
		switch (type) {
		case AZ:
			LocalTime start = toTime(attributes[2]);
			LocalTime end = toTime(attributes[3]);
			Duration idleTime = toDuration(attributes[6]);
			return new AZEntry(day, start, end, expectedTime, idleTime, items);
		case GT:
			return new GTEntry(day, expectedTime, items);
		default:
			return new DayEntry(day, type, items);
		}
	}

	private static LocalTime toTime(String time) {
		if (MISSING_VALUE.equals(time)) {
			return null;
		}
		return LocalTime.parse(time, DateTimeFormatter.ofPattern(TIME_FORMAT));
	}

	private static EntryType toType(String type) {
		if (MISSING_VALUE.equals(type)) {
			return null;
		}
		return EntryType.valueOf(type);
	}

	private static Duration toDuration(String duration) {
		Matcher matcher = durationPattern.matcher(duration);
		if (!matcher.find()) {
			return null;
		}
		String durationPart = matcher.group(1);
		if (MISSING_VALUE.equals(durationPart)) {
			return null;
		}

		return DurationFormatter.toDuration(durationPart);
	}

	private static Map<String, Duration> toItems(String[] itemsPart) {
		Map<String, Duration> items = new TreeMap<>();
		for (int k = 1, v = 0; k < itemsPart.length; k++, v++) {
			items.put(itemsPart[k], DurationFormatter.toDuration(itemsPart[v]));
		}
		return items;
	}

}
