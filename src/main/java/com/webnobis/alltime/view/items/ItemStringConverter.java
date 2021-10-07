package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.webnobis.alltime.service.DurationFormatter;

import javafx.util.StringConverter;

public class ItemStringConverter extends StringConverter<Item> {

	private static final Pattern textPattern = Pattern.compile("^([0-9:]*),? ?(.+)$");

	@Override
	public String toString(Item item) {
		return item.toString();
	}

	@Override
	public Item fromString(String text) {
		Matcher matcher = textPattern.matcher(text);
		return (matcher.find())
				? new Item(matcher.group(2),
						(matcher.group(1).isEmpty()) ? Duration.ZERO : DurationFormatter.toDuration(matcher.group(1)))
				: null;
	}

}
