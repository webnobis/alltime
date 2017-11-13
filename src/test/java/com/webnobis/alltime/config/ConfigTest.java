package com.webnobis.alltime.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

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

	private static final String CONFIG_FILE = ConfigTest.class.getSimpleName().toLowerCase() + ".properties";

	private Config config;

	@Before
	public void setUp() throws Exception {
		URI uri = this.getClass().getClassLoader().getResource(CONFIG_FILE).toURI();
		config = new Config(Paths.get(uri));
	}

	@Test
	public void testGetMaxCountOfDays() {
		assertEquals(MAX_COUNT_OF_DAYS, config.getMaxCountOfDays());
	}

	@Test
	public void testGetMaxCountOfRangeBookingDays() {
		assertEquals(MAX_COUNT_OF_RANGE_BOOKING_DAYS, config.getMaxCountOfRangeBookingDays());
	}

	@Test
	public void testGetMaxCountOfDescriptions() {
		assertEquals(MAX_COUNT_OF_DESCRIPTIONS, config.getMaxCountOfDescriptions());
	}

	@Test
	public void testGetTimeRasterMinutes() {
		assertEquals(TIME_RASTER_MINUTES, config.getTimeRasterMinutes());
	}

	@Test
	public void testGetTimeStartOffsetMinutes() {
		assertEquals(TIME_START_OFFSET_MINUTES, config.getTimeStartOffsetMinutes());
	}

	@Test
	public void testGetTimeEndOffsetMinutes() {
		assertEquals(TIME_END_OFFSET_MINUTES, config.getTimeEndOffsetMinutes());
	}

	@Test
	public void testGetItemDurationRasterMinutes() {
		assertEquals(ITEM_DURATION_RASTER_MINUTES, config.getItemDurationRasterMinutes());
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

	@Test
	public void testGetFileExportRootPath() {
		assertEquals(FILE_EXPORT_ROOT_PATH, config.getFileExportRootPath());
	}

}
