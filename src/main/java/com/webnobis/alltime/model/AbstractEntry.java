package com.webnobis.alltime.model;

import java.time.LocalDate;

abstract class AbstractEntry implements Entry {

	private final LocalDate day;

	private final EntryType type;

	AbstractEntry(LocalDate day, EntryType type) {
		this.day = day;
		this.type = type;
	}

	@Override
	public LocalDate getDay() {
		return day;
	}

	@Override
	public EntryType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s [day=%s, type=%s]", this.getClass().getSimpleName(), day, type);
	}

}