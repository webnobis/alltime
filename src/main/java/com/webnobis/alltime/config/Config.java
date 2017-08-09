package com.webnobis.alltime.config;

import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;
import java.util.Properties;

public class Config {

	private final InputStream configuration;

	private final Properties properties;

	public Config(InputStream configuration) {
		this.properties = properties;
	}

	public int getMaxCountOfDays() {
		return Integer.parseInt(properties.getProperty(Key.MAX_COUNT_OF_DAYS.getKey()));
	}

	public Map<DayOfWeek, Duration> getExpectedTimes() {
		return EnumSet.allOf(WeekdayKey.class).stream()
				.collect(Collectors.toMap(WeekdayKey::getWeekday, weekdayKey -> toDuration(properties.getProperty(weekdayKey.getKey()))))
	}

	private static Duration toDuration(String value) {
		LocalTime timeValue = LocalTime.parse(value);
		return Duration.ofHours(timeValue.getHour()).plusMinutes(timeValue.getMinute());
	}

}
