package com.webnobis.alltime.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

	private static final String KEY_VALUE_SEPARATOR = "=";

	private static final String COMMEND_LINE = "#";

	private final Map<String, String> properties;

	public Config(Path configFile) {
		try {
			this.properties = Files.readAllLines(Objects.requireNonNull(configFile, "configFile is null"), StandardCharsets.UTF_8).stream()
					.filter(line -> line.contains(KEY_VALUE_SEPARATOR))
					.filter(line -> !line.startsWith(COMMEND_LINE))
					.map(line -> line.split(KEY_VALUE_SEPARATOR))
					.collect(Collectors.toMap(array -> array[0], array -> array[1]));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public int getMaxCountOfDays() {
		return Integer.parseInt(properties.get(Key.MAX_COUNT_OF_DAYS.getKey()));
	}

	public int getMaxCountOfDescriptions() {
		return Integer.parseInt(properties.get(Key.MAX_COUNT_OF_DESCRIPTIONS.getKey()));
	}

	public int getTimeRasterMinutes() {
		return Integer.parseInt(properties.get(Key.TIME_RASTER_MINUTES.getKey()));
	}

	public int getTimeStartOffsetMinutes() {
		return Integer.parseInt(properties.get(Key.TIME_START_OFFSET_MINUTES.getKey()));
	}

	public int getTimeEndOffsetMinutes() {
		return Integer.parseInt(properties.get(Key.TIME_END_OFFSET_MINUTES.getKey()));
	}

	public int getItemDurationRasterMinutes() {
		return Integer.parseInt(properties.get(Key.ITEM_DURATION_RASTER_MINUTES.getKey()));
	}

	public Map<DayOfWeek, Duration> getExpectedTimes() {
		return EnumSet.allOf(WeekdayKey.class).stream()
				.filter(weekdayKey -> properties.containsKey(weekdayKey.getKey()))
				.collect(Collectors.toMap(WeekdayKey::getWeekday, weekdayKey -> toDuration(properties.get(weekdayKey.getKey()))));
	}

	private static Duration toDuration(String value) {
		LocalTime timeValue = LocalTime.parse(value);
		return Duration.ofHours(timeValue.getHour()).plusMinutes(timeValue.getMinute());
	}

	public Map<Duration, Duration> getIdleTimes() {
		return properties.keySet().stream()
				.filter(key -> key.startsWith(IdleLimitKey.IDLE_LIMIT_KEY.getKey()))
				.collect(Collectors.toMap(key -> toDuration(IdleLimitKey.IDLE_LIMIT_KEY.getDurationPart(key)), key -> toDuration(properties.get(key))));
	}

	public Path getFileStoreRootPath() {
		return Paths.get(properties.get(Key.FILE_STORE_ROOT_PATH.getKey()));
	}

}
