package com.webnobis.alltime.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	private static final int MAX_COUNT_OF_DAYS = 77;

	private static final int MAX_COUNT_OF_DESCRIPTIONS = 99;

	private static final Duration EXPECTED_TIME_DI = Duration.ofHours(7).plusMinutes(36);

	private static final Duration EXPECTED_TIME_MI = Duration.ofHours(8);

	private static final Duration EXPECTED_TIME_FR = Duration.ofHours(6);

	private static final Duration IDLE_TIME_LIMIT_1 = Duration.ofHours(6);

	private static final Duration IDLE_TIME_1 = Duration.ofMinutes(30);

	private static final Duration IDLE_TIME_LIMIT_2 = Duration.ofHours(9);

	private static final Duration IDLE_TIME_2 = Duration.ofMinutes(15);

	private static final Path FILE_STORE_ROOT_PATH = Paths.get("the", "store", "path");

	private static final String CONFIG_FILE = ConfigTest.class.getSimpleName().toLowerCase() + ".properties";

	private Config config;

	@Before
	public void setUp() throws Exception {
		String path = this.getClass().getClassLoader().getResource(CONFIG_FILE).getPath();
		config = new Config(Paths.get(path));
	}

	@Test
	public void testGetMaxCountOfDays() {
		assertEquals(MAX_COUNT_OF_DAYS, config.getMaxCountOfDays());
	}

	@Test
	public void testGetMaxCountOfDescriptions() {
		assertEquals(MAX_COUNT_OF_DESCRIPTIONS, config.getMaxCountOfDescriptions());
	}

	@Test
	public void testGetExpectedTimes() {
		Map<DayOfWeek, Duration> map = config.getExpectedTimes();
		assertEquals(7, map.size());

		assertEquals(map.remove(DayOfWeek.TUESDAY), EXPECTED_TIME_DI);
		assertEquals(map.remove(DayOfWeek.WEDNESDAY), EXPECTED_TIME_MI);
		assertEquals(map.remove(DayOfWeek.FRIDAY), EXPECTED_TIME_FR);
		assertTrue(map.values().stream().allMatch(Duration.ZERO::equals));
	}

	@Test
	public void testGetIdleTimes() {
		Map<Duration, Duration> map = config.getIdleTimes();
		assertEquals(2, map.size());

		assertEquals(map.get(IDLE_TIME_LIMIT_1), IDLE_TIME_1);
		assertEquals(map.get(IDLE_TIME_LIMIT_2), IDLE_TIME_2);
	}

	@Test
	public void testGetFileStoreRootPath() {
		assertEquals(FILE_STORE_ROOT_PATH, config.getFileStoreRootPath());
	}

}
