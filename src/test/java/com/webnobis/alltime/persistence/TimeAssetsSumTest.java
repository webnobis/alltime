package com.webnobis.alltime.persistence;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.GTEntry;
import com.webnobis.alltime.model.TimeAssetsSum;

public class TimeAssetsSumTest {

	private static final String SPLIT = ";";

	private static final List<LocalDate> days = IntStream.rangeClosed(1, 5).mapToObj(d -> LocalDate.of(2000, 1, d))
			.collect(Collectors.toList());

	private static final Function<TimeAssetsSum, String> timeAssetsSumSerializer = sum -> sum.getDay()
			.format(DateTimeFormatter.ISO_LOCAL_DATE).concat(SPLIT).concat(sum.getTimeAssetsSum().toString());

	private static final Function<String, TimeAssetsSum> timeAssetsSumDeserializer = text -> {
		String[] split = text.split(SPLIT);
		return new TimeAssetsSum(LocalDate.parse(split[0], DateTimeFormatter.ISO_LOCAL_DATE), Duration.parse(split[1]));
	};

	private Supplier<LocalDate> now;

	private Path tmpRoot;

	private EntryStore store;

	@Before
	public void setUp() throws Exception {
		now = new Supplier<LocalDate>() {

			private int i;

			@Override
			public LocalDate get() {
				if (i < days.size()) {
					return days.get(i++);
				}
				throw new NoSuchElementException();
			}

		};

		tmpRoot = Files.createTempDirectory(EntryStoreTest.class.getSimpleName());

		store = new FileStore(tmpRoot, now, 0, 0, text -> null, text -> null, entry -> entry.toString(),
				timeAssetsSumDeserializer, timeAssetsSumSerializer);
	}

	@After
	public void tearDown() throws Exception {
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
	public void testGetTimeAssetsSumBefore() {
		Duration d1 = Duration.ofDays(2).plusHours(2);
		Duration d2 = Duration.ofHours(-8);
		Duration d3 = Duration.ZERO;
		Duration d4 = Duration.ofMinutes(-45);

		LocalTime startAndEnd = LocalTime.of(10, 10);
		Stream.of(d1, d2, d3, d4).map(Duration::negated)
				.map(d -> new AZEntry(now.get(), startAndEnd, startAndEnd, d, Duration.ZERO, Collections.emptyMap()))
				.forEach(store::storeEntry);

		Duration expected = d1.plus(d2).plus(d3).plus(d4);
		assertEquals(expected, store.getTimeAssetsSumBefore(now.get()).getTimeAssetsSum());
	}

	@Test
	public void testGetTimeAssetsSumBeforeOutOfRange() {
		assertEquals(new TimeAssetsSum(LocalDate.now().minusDays(1), Duration.ZERO),
				store.getTimeAssetsSumBefore(LocalDate.now()));

		LocalDate day = now.get();
		store.storeEntry(new GTEntry(day, Duration.ofHours(8), Collections.emptyMap()));
		assertEquals(new TimeAssetsSum(day.minusDays(1), Duration.ZERO), store.getTimeAssetsSumBefore(day));
	}

	@Test
	public void testGetTimeAssetsSumDayBefore() {
		LocalDate day = LocalDate.of(2018, 10, 17);
		TimeAssetsSum s1 = new TimeAssetsSum(day.minusDays(32), Duration.ZERO);
		TimeAssetsSum s2 = new TimeAssetsSum(day.minusMonths(1), Duration.ZERO);
		TimeAssetsSum s3 = new TimeAssetsSum(day.minusDays(3), Duration.ZERO);
		TimeAssetsSum s4 = new TimeAssetsSum(day, Duration.ZERO);

		Stream.of(s3, s1, s2, s4).map(s -> new DayEntry(s.getDay(), EntryType.KR, Collections.emptyMap()))
				.peek(store::storeEntry)
				.map(DayEntry::getDay)
				.forEach(d -> assertEquals(d.minusDays(1), store.getTimeAssetsSumBefore(d).getDay()));
	}
}
