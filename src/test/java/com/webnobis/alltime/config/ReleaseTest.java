package com.webnobis.alltime.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReleaseTest {

	private static Map<Release, String> values;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		values = EnumSet.allOf(Release.class).stream()
				.collect(Collectors.toMap(e -> e, e -> "the ".concat(e.name().toLowerCase())));
	}

	@Test
	void testGetValue() {
		EnumSet.allOf(Release.class).forEach(e -> assertEquals(values.get(e), e.getValue()));
	}

}
