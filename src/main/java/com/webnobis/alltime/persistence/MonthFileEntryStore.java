package com.webnobis.alltime.persistence;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;

public class MonthFileEntryStore implements EntryStore {
	
	private final int maxCount;
	
	private final Function<String,List<String>> lineToAttributes;
	
	private final Function<List<String>, LocalDate> attributesToDay;
	
	private final Function<List<String>, Duration> attributesToTimeAssets;
	
	private final BiFunction<List<String>, Duration, Entry> attributesToEntry;
	
	private List<String> getDayLines(YearMonth month) {
		// TODO
		return null;
	}

	@Override
	public List<LocalDate> getLastDays(LocalDate untilDay) {
		return getLastDaysAttributes(untilDay, maxCount)
				.map(attributesToDay::apply)
				.collect(Collectors.toList());
	}

	private Stream<List<String>> getLastDaysAttributes(LocalDate untilDay, int maxCount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entry getEntry(LocalDate day) {
		List<List<String>> attributes = getLastDaysAttributes(day, 2).collect(Collectors.toList());
		if (attributes.isEmpty()) {
			return null;
		} else if (attributes.size() < 2) {
			return attributesToEntry.apply(attributes.iterator().next(), null);
		} else {
			return attributesToEntry.apply(attributes.get(0), attributesToTimeAssets.apply(attributes.get(1)));
		}
	}

	@Override
	public void storeEntry(Entry entry) {
		// TODO Auto-generated method stub
		
	}

}
