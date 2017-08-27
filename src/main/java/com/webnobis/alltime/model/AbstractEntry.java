package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

abstract class AbstractEntry implements Entry {

	private final LocalDate day;

	private final EntryType type;

	private final Map<String, Duration> items;

	AbstractEntry(LocalDate day, EntryType type, Map<String, Duration> items) {
		this.day = day;
		this.type = type;
		this.items = Collections.unmodifiableMap(items);
	}

	@Override
	public LocalDate getDay() {
		return day;
	}

	@Override
	public EntryType getType() {
		return type;
	}

	public Map<String, Duration> getItems() {
		return items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
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
		AbstractEntry other = (AbstractEntry) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s [day=%s, type=%s]", this.getClass().getSimpleName(), day, type);
	}

}
