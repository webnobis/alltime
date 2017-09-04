package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Map;

import com.webnobis.alltime.service.DurationFormatter;

import javafx.util.StringConverter;

class ItemStringConverter extends StringConverter<Map.Entry<String, Duration>> {

	@Override
	public String toString(Map.Entry<String, Duration> entry) {
		return DurationFormatter.toString(entry.getValue()).concat(", ").concat(entry.getKey());
	}

	@Override
	public Map.Entry<String, Duration> fromString(String entry) {
		throw new UnsupportedOperationException();
	}

}