package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Optional.ofNullable(value).filter(v -> !Duration.ZERO.equals(v))
				.map(v -> DurationFormatter.toString(v).concat(", ").concat(key)).orElse(key);
	}

}