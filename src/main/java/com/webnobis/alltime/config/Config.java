package com.webnobis.alltime.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

	private static final Logger log = LoggerFactory.getLogger(Config.class);

	private static final String KEY_VALUE_SEPARATOR = "=";

	private static final String COMMEND_LINE = "#";

	private final Map<String, String> properties;

	public Config(InputStream configStream) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(configStream)))) {
			log.info("Configuration load:");
			this.properties = in.lines().filter(line -> line.contains(KEY_VALUE_SEPARATOR))
					.filter(line -> !line.startsWith(COMMEND_LINE)).peek(log::info)
					.map(line -> line.split(KEY_VALUE_SEPARATOR, 2))
					.collect(Collectors.toMap(array -> array[0], array -> array[1]));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new UncheckedIOException(e.getMessage(), e);
		}
	}

	public int getMaxCountOfDays() {
		return Integer.parseInt(properties.get(Key.MAX_COUNT_OF_DAYS.getKey()));
	}

	public int getMaxCountOfRangeBookingDays() {
		return Integer.parseInt(properties.get(Key.MAX_COUNT_OF_RANGE_BOOKING_DAYS.getKey()));
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
		return EnumSet.allOf(WeekdayKey.class).stream().collect(Collectors.toMap(WeekdayKey::getWeekday,
				weekdayKey -> toDuration(properties.get(weekdayKey.getKey()))));
	}

	private static Duration toDuration(String value) {
		if (value == null) {
			return Duration.ZERO;
		}

		LocalTime timeValue = LocalTime.parse(value);
		return Duration.ofHours(timeValue.getHour()).plusMinutes(timeValue.getMinute());
	}

	public Map<Duration, Duration> getIdleTimes() {
		return properties.keySet().stream().filter(key -> key.startsWith(IdleLimitKey.IDLE_LIMIT_KEY.getKey()))
				.collect(Collectors.toMap(key -> toDuration(IdleLimitKey.IDLE_LIMIT_KEY.getDurationPart(key)),
						key -> toDuration(properties.get(key))));
	}

	public Path getFileStoreRootPath() {
		return Paths.get(properties.get(Key.FILE_STORE_ROOT_PATH.getKey()));
	}

	public Path getFileExportRootPath() {
		return Paths.get(properties.get(Key.FILE_EXPORT_ROOT_PATH.getKey()));
	}

}
