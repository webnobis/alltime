package com.webnobis.alltime.view.items;

import java.time.Duration;

import com.webnobis.alltime.service.DurationFormatter;

import javafx.util.StringConverter;

class DurationStringConverter extends StringConverter<Duration> {

	@Override
	public String toString(Duration duration) {
		return DurationFormatter.toString(duration);
	}

	@Override
	public Duration fromString(String duration) {
		throw new UnsupportedOperationException();
	}

}