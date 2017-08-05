package com.webnobis.alltime.model;

import java.time.DayOfWeek;
import java.time.Duration;

@FunctionalInterface
public interface ExpectedTime {
	
	Duration getExpectedTime(DayOfWeek weekDay);

}
