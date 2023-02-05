package com.webnobis.alltime.persistence;

import static com.webnobis.alltime.persistence.LineDefinition.ATTRIBUTE_SEPARATOR;
import static com.webnobis.alltime.persistence.LineDefinition.DAY_FORMAT;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.DurationFormatter;

public record TimeAssetsSumSerializer() {

	public static String toLine(TimeAssetsSum timeAssetsSum) {
		Objects.requireNonNull(timeAssetsSum, "timeAssetsSum is null");
		return toText(timeAssetsSum.day()).concat(ATTRIBUTE_SEPARATOR)
				.concat(toText(timeAssetsSum.timeAssetsSum()));
	}

	private static String toText(LocalDate day) {
		return Objects.requireNonNull(day, "day is null").format(DateTimeFormatter.ofPattern(DAY_FORMAT));
	}

	private static String toText(Duration duration) {
		return DurationFormatter.toString(Objects.requireNonNull(duration, "duration is null"));
	}

}
