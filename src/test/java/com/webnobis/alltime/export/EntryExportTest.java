package com.webnobis.alltime.export;

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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.webnobis.alltime.model.AZEntry;
import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.FindService;

public class EntryExportTest {

	private static final LocalDate date = LocalDate.of(2010, 1, 2);

	private static final TimeAssetsSum SUM_BEFORE = new TimeAssetsSum(date.minusWeeks(2), Duration.ofHours(4));

	private static SortedMap<LocalDate, Entry> entries;

	private Path tmpRoot;

	private EntryExport export;

	@BeforeClass
	public static void setUpClass() throws Exception {
		entries = Stream.concat(Stream.of(new AZEntry(date, LocalTime.of(7, 5), LocalTime.of(16, 20), Duration.ofHours(8), Duration.ofMinutes(30),
				LongStream.rangeClosed(2, 4)
						.boxed()
						.collect(Collectors.toMap(i -> "Projekt-Buchung " + i, Duration::ofHours)))),
				LongStream.rangeClosed(1, 100)
						.filter(l -> l < 5 || l > 20)
						.mapToObj(date::plusDays)
						.<Entry>map(day -> new DayEntry(day, EntryType.SM,
								Collections.singletonMap(EntryExportTest.class.getSimpleName(), Duration.ofMinutes(day.getDayOfYear())))))
				.collect(Collectors.toMap(Entry::getDay, entry -> entry, (day1, day2) -> day2, TreeMap::new));
	}

	@Before
	public void setUp() throws Exception {
		tmpRoot = Files.createTempDirectory(EntryExportTest.class.getSimpleName());
		export = new PdfExport(tmpRoot, new TestFindService());
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
	public void testExportRange() {
		List<Entry> expected = new ArrayList<>(entries.values());

		List<Entry> exported = export.exportRange(entries.firstKey(), entries.lastKey());
		assertEquals(expected, exported);
	}

	@Test
	public void testExportMonth() {
		YearMonth month = YearMonth.from(date);
		List<Entry> expected = entries.values().stream()
				.filter(entry -> month.getMonth().equals(entry.getDay().getMonth()))
				.collect(Collectors.toList());

		List<Entry> exported = export.exportMonth(month);
		assertEquals(expected, exported);
	}

	private class TestFindService implements FindService {

		@Override
		public List<LocalDate> getLastDays() {
			return new ArrayList<>(entries.keySet());
		}

		@Override
		public Entry getEntry(LocalDate day) {
			return entries.get(day);
		}

		@Override
		public List<String> getLastDescriptions() {
			throw new UnsupportedOperationException();
		}

		@Override
		public TimeAssetsSum getTimeAssetsSumBefore(LocalDate day) {
			return SUM_BEFORE;
		}

	}

}
