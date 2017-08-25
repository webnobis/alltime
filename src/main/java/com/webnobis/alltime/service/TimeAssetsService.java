package com.webnobis.alltime.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;

public interface TimeAssetsService {

	Duration getTimeAssetsSumBefore(LocalDate day);

	default Duration getTimeAssetsSum(List<Entry> entries) {
		if (entries.isEmpty()) {
			return Duration.ZERO;
		}

		return entries.stream()
				.map(Entry::getDay)
				.sorted((d1, d2) -> d1.compareTo(d2))
				.findFirst()
				.flatMap(firstDay -> Stream.concat(Stream.of(getTimeAssetsSumBefore(firstDay)),
						entries.stream()
								.filter(entry -> !firstDay.equals(entry.getDay()))
								.map(Entry::getTimeAssets))
						.reduce((d1, d2) -> d1.plus(d2)))
				.orElse(Duration.ZERO);
	}

}
