package com.webnobis.alltime.persistence;

import static com.webnobis.alltime.persistence.LineDefinition.A;
import static com.webnobis.alltime.persistence.LineDefinition.ATTRIBUTE_SEPARATOR;
import static com.webnobis.alltime.persistence.LineDefinition.DAY_FORMAT;
import static com.webnobis.alltime.persistence.LineDefinition.E;
import static com.webnobis.alltime.persistence.LineDefinition.I;
import static com.webnobis.alltime.persistence.LineDefinition.MISSING_VALUE;
import static com.webnobis.alltime.persistence.LineDefinition.R;
import static com.webnobis.alltime.persistence.LineDefinition.TIME_FORMAT;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.DurationFormatter;

public abstract class LineToEntrySerializer {

	private LineToEntrySerializer() {
	}

	public static String toLine(Entry entry) {
		return Stream.concat(getAttributeStream(entry), getItemStream(entry))
				.collect(Collectors.joining(ATTRIBUTE_SEPARATOR));
	}

	private static Stream<String> getAttributeStream(Entry entry) {
		return Stream.of(toText(entry.getDay()),
				toText(entry.getType()),
				toText(entry.getStart()),
				toText(entry.getEnd()),
				R.name().concat(toText(entry.getRealTime())),
				E.name().concat(toText(entry.getExpectedTime())),
				I.name().concat(toText(entry.getIdleTime())),
				A.name().concat(toText(entry.getTimeAssets())));
	}

	private static Stream<String> getItemStream(Entry entry) {
		return new TreeMap<>(entry.getItems()).entrySet().stream()
				.flatMap(e -> Stream.of(toText(e.getValue()), e.getKey()));
	}

	private static String toText(LocalDate day) {
		return Optional.ofNullable(day)
				.map(d -> d.format(DateTimeFormatter.ofPattern(DAY_FORMAT)))
				.orElse(MISSING_VALUE);
	}

	private static String toText(EntryType type) {
		return Optional.ofNullable(type)
				.map(EntryType::name)
				.orElse(MISSING_VALUE);
	}

	private static String toText(LocalTime time) {
		return Optional.ofNullable(time)
				.map(t -> t.format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
				.orElse(MISSING_VALUE);
	}

	private static String toText(Duration duration) {
		return Optional.ofNullable(duration)
				.map(DurationFormatter::toString)
				.orElse(MISSING_VALUE);
	}

}
