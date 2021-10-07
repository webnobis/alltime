package com.webnobis.alltime.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReleaseTest {

	private static Map<Release, String> values;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(ClassLoader.getSystemResourceAsStream(Release.RELEASE_FILE)))) {
			values = in.lines().filter(line -> line.contains("=")).map(line -> line.split("=", 2))
					.collect(Collectors.toMap(a -> Release.valueOf(a[0].toUpperCase()), a -> a[1]));
		}
	}

	@Test
	void testGetValue() {
		EnumSet.allOf(Release.class).forEach(e -> assertEquals(values.get(e), e.getValue()));

	}

}
