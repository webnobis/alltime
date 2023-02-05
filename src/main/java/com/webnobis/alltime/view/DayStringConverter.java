package com.webnobis.alltime.view;

import java.text.MessageFormat;
import java.time.LocalDate;

import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.WeekDayTransformer;

import javafx.util.StringConverter;

public class DayStringConverter extends StringConverter<LocalDate> {

	@Override
	public String toString(LocalDate day) {
		if (day == null) {
			return null;
		}
		return MessageFormat.format("{0} ({1})", DayTransformer.toText(day), WeekDayTransformer.toText(day));
	}

	@Override
	public LocalDate fromString(String text) {
		return DayTransformer.toDay(text.substring(text.indexOf(' ')));
	}

}
