package com.webnobis.alltime.persistence;

import static com.webnobis.alltime.persistence.LineDefinition.ATTRIBUTE_SEPARATOR;

import java.util.NoSuchElementException;
import java.util.Objects;

import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.DurationFormatter;

public abstract class TimeAssetsSumDeserializer {

	private TimeAssetsSumDeserializer() {
	}

	public static TimeAssetsSum toTimeAssetsSum(String line) {
		Objects.requireNonNull(line, "line is null");

		if (!line.contains(ATTRIBUTE_SEPARATOR)) {
			throw new NoSuchElementException(String.format("missing attributes, separated with %s within line: %s", ATTRIBUTE_SEPARATOR, line));
		}

		return new TimeAssetsSum(LineToDayDeserializer.toDay(line), DurationFormatter.toDuration(line.split(ATTRIBUTE_SEPARATOR)[1]));
	}

}
