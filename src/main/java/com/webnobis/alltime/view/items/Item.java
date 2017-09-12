package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Map;

import com.webnobis.alltime.service.DurationFormatter;

public class Item implements Map.Entry<String, Duration>, Comparable<Item> {

	private final String key;

	private final Duration value;

	public Item(String key, Duration value) {
		this.key = key;
		this.value = value;
	}

	public Item(Map.Entry<String, Duration> entry) {
		this(entry.getKey(), entry.getValue());
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Duration getValue() {
		return value;
	}

	@Override
	public Duration setValue(Duration value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(Item other) {
		return key.compareTo(other.key);
	}

	@Override
	public String toString() {
		return DurationFormatter.toString(value).concat(", ").concat(key);
	}

}