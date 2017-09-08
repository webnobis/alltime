package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Map;

class Item implements Map.Entry<String, Duration>, Comparable<Item> {

	private final String key;

	private final Duration value;

	Item(String key, Duration value) {
		this.key = key;
		this.value = value;
	}

	Item(Map.Entry<String, Duration> entry) {
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

}