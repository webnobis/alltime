package com.webnobis.alltime.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfigTest {

	private static final int MAX_COUNT_OF_DAYS = 77;

	private static final int MAX_COUNT_OF_RANGE_BOOKING_DAYS = 88;

	private static final int MAX_COUNT_OF_DESCRIPTIONS = 99;

	private static final int TIME_RASTER_MINUTES = 3;

	private static final int TIME_START_OFFSET_MINUTES = 4;

	private static final int TIME_END_OFFSET_MINUTES = 5;

	private static final int ITEM_DURATION_RASTER_MINUTES = 6;

	private static final Duration EXPECTED_TIME_DI = Duration.ofHours(7).plusMinutes(36);

	private static final Duration EXPECTED_TIME_MI = Duration.ofHours(8);

	private static final Duration EXPECTED_TIME_FR = Duration.ofHours(6);

	private static final Duration IDLE_TIME_LIMIT_1 = Duration.ofHours(6);

	private static final Duration IDLE_TIME_1 = Duration.ofMinutes(30);

	private static final Duration IDLE_TIME_LIMIT_2 = Duration.ofHours(9);

	private static final Duration IDLE_TIME_2 = Duration.ofMinutes(15);

	private static final Path FILE_STORE_ROOT_PATH = Paths.get("the", "store", "path");

	private static final Path FILE_EXPORT_ROOT_PATH = Paths.get("the", "export", "path");

	private static final String CONFIG_FILE = "configtest.properties";

	private Config config;

	@BeforeEach
	void setUp() throws Exception {
		config = new Config(ClassLoader.getSystemResourceAsStream(CONFIG_FILE));
	}

	@Test
	void testGetMaxCountOfDays() {
		assertEquals(MAX_COUNT_OF_DAYS, config.getMaxCountOfDays());
	}

	@Test
	void testGetMaxCountOfRangeBookingDays() {
		assertEquals(MAX_COUNT_OF_RANGE_BOOKING_DAYS, config.getMaxCountOfRangeBookingDays());
	}

	@Test
	void testGetMaxCountOfDescriptions() {
		assertEquals(MAX_COUNT_OF_DESCRIPTIONS, config.getMaxCountOfDescriptions());
	}

	@Test
	void testGetTimeRasterMinutes() {
		assertEquals(TIME_RASTER_MINUTES, config.getTimeRasterMinutes());
	}

	@Test
	void testGetTimeStartOffsetMinutes() {
		assertEquals(TIME_START_OFFSET_MINUTES, config.getTimeStartOffsetMinutes());
	}

	@Test
	void testGetTimeEndOffsetMinutes() {
		assertEquals(TIME_END_OFFSET_MINUTES, config.getTimeEndOffsetMinutes());
	}

	@Test
	void testGetItemDurationRasterMinutes() {
		assertEquals(ITEM_DURATION_RASTER_MINUTES, config.getItemDurationRasterMinutes());
	}

	@Test
	void testGetExpectedTimes() {
		Map<DayOfWeek, Duration> map = config.getExpectedTimes();
		assertEquals(7, map.size());

		assertEquals(EXPECTED_TIME_DI, map.remove(DayOfWeek.TUESDAY));
		assertEquals(EXPECTED_TIME_MI, map.remove(DayOfWeek.WEDNESDAY));
		assertEquals(EXPECTED_TIME_FR,map.remove(DayOfWeek.FRIDAY));
		assertTrue(map.values().stream().allMatch(Duration.ZERO::equals));
	}

	@Test
	void testGetIdleTimes() {
		Map<Duration, Duration> map = config.getIdleTimes();
		assertEquals(2, map.size());

		assertEquals(IDLE_TIME_1, map.get(IDLE_TIME_LIMIT_1));
		assertEquals(IDLE_TIME_2,map.get(IDLE_TIME_LIMIT_2));
	}

	@Test
	void testGetFileStoreRootPath() {
		assertEquals(FILE_STORE_ROOT_PATH, config.getFileStoreRootPath());
	}

	@Test
	void testGetFileExportRootPath() {
		assertEquals(FILE_EXPORT_ROOT_PATH, config.getFileExportRootPath());
	}

}
