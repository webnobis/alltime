package com.webnobis.alltime.config;

import java.time.DayOfWeek;

public enum WeekdayKey {

	EXPECTED_TIME_MO(DayOfWeek.MONDAY, "expected.time.mo"), EXPECTED_TIME_DI(DayOfWeek.TUESDAY, "expected.time.di"),
	EXPECTED_TIME_MI(DayOfWeek.WEDNESDAY, "expected.time.mi"), EXPECTED_TIME_DO(DayOfWeek.THURSDAY, "expected.time.do"),
	EXPECTED_TIME_FR(DayOfWeek.FRIDAY, "expected.time.fr"), EXPECTED_TIME_SA(DayOfWeek.SATURDAY, "expected.time.sa"),
	EXPECTED_TIME_SO(DayOfWeek.SUNDAY, "expected.time.so");

	private final DayOfWeek weekday;

	private final String key;

	private WeekdayKey(DayOfWeek weekday, String key) {
		this.weekday = weekday;
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public DayOfWeek getWeekday() {
		return weekday;
	}

}