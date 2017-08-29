package com.webnobis.alltime.persistence;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.util.function.Function;

import org.junit.Test;

import com.webnobis.alltime.model.TimeAssetsSum;

public class TimeAssetsSumSerializerAndDeserializerTest {

	private static final Function<TimeAssetsSum, String> serializer = TimeAssetsSumSerializer::toLine;
	
	private static final Function<String, TimeAssetsSum> deserializer = TimeAssetsSumDeserializer::toTimeAssetsSum;

	@Test
	public void test() {
		TimeAssetsSum expected = new TimeAssetsSum(LocalDate.of(1980, 4, 1), Duration.ofDays(99).plusMinutes(1).plusHours(4));
		String line = serializer.apply(expected);
		
		assertEquals(expected, deserializer.apply(line));
	}

}
