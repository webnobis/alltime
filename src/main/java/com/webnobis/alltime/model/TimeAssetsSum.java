package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;

public class TimeAssetsSum {

	private final LocalDate day;
	
	private final Duration timeAssetsSum;

	public TimeAssetsSum(LocalDate day, Duration timeAssetsSum) {
		this.day = day;
		this.timeAssetsSum = timeAssetsSum;
	}

	public LocalDate getDay() {
		return day;
	}

	public Duration getTimeAssetsSum() {
		return timeAssetsSum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((timeAssetsSum == null) ? 0 : timeAssetsSum.hashCode());
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
		TimeAssetsSum other = (TimeAssetsSum) obj;
		if (day == null) {
			if (other.day != null)
				return false;
		} else if (!day.equals(other.day))
			return false;
		if (timeAssetsSum == null) {
			if (other.timeAssetsSum != null)
				return false;
		} else if (!timeAssetsSum.equals(other.timeAssetsSum))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimeAssetsSum [day=" + day + ", timeAssetsSum=" + timeAssetsSum + "]";
	}

}
