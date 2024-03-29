package com.webnobis.alltime.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;

class EntryStoreTest {

	private static final LocalDate DAY1 = LocalDate.of(2017, Month.MAY, 1);

	private static final LocalDate DAY2 = DAY1.plusDays(3);

	private static final LocalDate DAY3 = DAY1.plusDays(13);

	private static final LocalDate DAY4 = DAY2.plusMonths(1).plusDays(1);

	private static final LocalDate DAY5 = DAY3.plusMonths(2).plusDays(1);

	private static final String LAST_DESCRIPTION_1 = "zuletzt test 1 2 3";

	private static final String LAST_DESCRIPTION_2 = "nach erster test 1 2 3";

	private static final String LAST_DESCRIPTION_3 = "nach zweiter test 1 2 3";

	private static final String LAST_DESCRIPTION_4 = "erster test 1 2 3";

	private static final int MAX_DAY_COUNT = 4;

	private static final int MAX_DESCRIPTION_COUNT = 3;

	private static final List<String> descriptions = Arrays.asList(LAST_DESCRIPTION_2, LAST_DESCRIPTION_4,
			LAST_DESCRIPTION_3, LAST_DESCRIPTION_1);

	private static final List<LocalDate> days = Arrays.asList(DAY5, DAY4, DAY3, DAY2);

	private static final Supplier<LocalDate> now = () -> DAY5;

	private static final Function<String, LocalDate> dayDeserializer = text -> LocalDate.parse(text);

	private static final Function<String, Entry> entryDeserializer = text -> {
		LocalDate day = dayDeserializer.apply(text);
		return new DayEntry(day, EntryType.UR, Optional.of(days.indexOf(day)).filter(i -> i > -1).map(descriptions::get)
				.map(d -> Collections.singletonMap(d, Duration.ZERO)).orElse(Collections.emptyMap()));
	};

	private static final Function<Entry, String> entrySerializer = entry -> entry.getDay()
			.format(DateTimeFormatter.ISO_LOCAL_DATE);

	private static final Function<TimeAssetsSum, String> timeAssetsSumSerializer = sum -> sum.day()
			.format(DateTimeFormatter.ISO_LOCAL_DATE);

	private static final Function<LocalDate, TimeAssetsSum> testTransformer = day -> new TimeAssetsSum(day,
			Duration.ofHours(day.getDayOfMonth()));

	private static final Function<String, TimeAssetsSum> timeAssetsSumDeserializer = text -> testTransformer
			.apply(LocalDate.parse(text));

	private Path tmpRoot;

	private EntryStore store;

	@BeforeEach
	void setUp() throws Exception {
		tmpRoot = Files.createTempDirectory(EntryStoreTest.class.getSimpleName());

		Path file1 = tmpRoot.resolve("201705.dat");
		Files.write(file1,
				Arrays.asList(DAY1.format(DateTimeFormatter.ISO_LOCAL_DATE),
						DAY2.format(DateTimeFormatter.ISO_LOCAL_DATE), DAY3.format(DateTimeFormatter.ISO_LOCAL_DATE)),
				StandardOpenOption.CREATE);
		Files.write(tmpRoot.resolve("201706.dat"), Collections.singleton(DAY4.format(DateTimeFormatter.ISO_LOCAL_DATE)),
				StandardOpenOption.CREATE);
		Files.write(tmpRoot.resolve("201707.dat"), Collections.singleton(DAY5.format(DateTimeFormatter.ISO_LOCAL_DATE)),
				StandardOpenOption.CREATE);
		Files.copy(file1, tmpRoot.resolve("timeassets.dat"));

		store = new FileStore(tmpRoot, now, MAX_DAY_COUNT, MAX_DESCRIPTION_COUNT, dayDeserializer, entryDeserializer,
				entrySerializer, timeAssetsSumDeserializer, timeAssetsSumSerializer);
	}

	@AfterEach
	void tearDown() throws Exception {
		Files.walkFileTree(tmpRoot, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@Test
	void testGetLastDays() throws IOException {
		List<LocalDate> expected = Arrays.asList(DAY5, DAY4, DAY3, DAY2);
		assertEquals(expected, store.getLastDays());
	}

	@Test
	void testGetEntry() {
		Entry expected = new DayEntry(DAY1, EntryType.UR, Collections.emptyMap());
		assertEquals(expected, store.getEntry(DAY1));
	}

	@Test
	void testGetTimeAssetsSumBefore() {
		TimeAssetsSum expected = testTransformer.apply(DAY3);
		assertEquals(expected, store.getTimeAssetsSumBefore(DAY5));
	}

	@Test
	void testStoreEntry() {
		LocalDate day = DAY1.minusDays(7);
		Entry expected = new DayEntry(day, EntryType.SO, Collections.singletonMap("a key", Duration.ofMinutes(5)));
		assertNull(store.getEntry(day));
		assertEquals(expected, store.storeEntry(expected));
		assertEquals(expected, store.getEntry(day));

		// test update
		TimeAssetsSum expectedAfterUpdate = testTransformer.apply(day);
		assertEquals(expectedAfterUpdate, store.getTimeAssetsSumBefore(DAY1));
	}

	@Test
	void testGetLastDescriptions() throws IOException {
		List<String> expected = Arrays.asList(LAST_DESCRIPTION_4, LAST_DESCRIPTION_2, LAST_DESCRIPTION_3);
		assertEquals(expected, store.getLastDescriptions());
	}

}
