package com.webnobis.alltime.persistence;

import static com.webnobis.alltime.persistence.LineDefinition.ATTRIBUTE_SEPARATOR;
import static com.webnobis.alltime.persistence.LineDefinition.DAY_FORMAT;
import static com.webnobis.alltime.persistence.LineDefinition.MISSING_VALUE;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

public abstract class LineToDayDeserializer {

	private LineToDayDeserializer() {
	}

	public static LocalDate toDay(String line) {
		if (!line.contains(ATTRIBUTE_SEPARATOR)) {
			throw new NoSuchElementException("missing day within line: ".concat(line));
		}

		String day = line.split(ATTRIBUTE_SEPARATOR)[0];
		if (MISSING_VALUE.equals(day)) {
			return null;
		}
		return LocalDate.parse(day, DateTimeFormatter.ofPattern(DAY_FORMAT));
	}

}
