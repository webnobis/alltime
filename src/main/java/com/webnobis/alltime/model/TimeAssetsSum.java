package com.webnobis.alltime.model;

import java.time.Duration;
import java.time.LocalDate;

public record TimeAssetsSum(LocalDate day, Duration timeAssetsSum) {

	@Override
	public String toString() {
		return "TimeAssetsSum [day=" + day + ", timeAssetsSum=" + timeAssetsSum + "]";
	}

}
