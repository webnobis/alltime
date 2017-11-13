package com.webnobis.alltime.view;

import java.time.LocalDate;

import com.webnobis.alltime.service.DayTransformer;

import javafx.util.StringConverter;

public class DayStringConverter extends StringConverter<LocalDate> {

	@Override
	public String toString(LocalDate day) {
		return DayTransformer.toText(day);
	}

	@Override
	public LocalDate fromString(String text) {
		return DayTransformer.toDay(text);
	}

}
