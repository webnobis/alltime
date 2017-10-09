package com.webnobis.alltime.export;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.webnobis.alltime.model.DayEntry;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.FindService;

public class EntryExportTest {

	private static final Supplier<LocalDate> now = () -> LocalDate.of(2010, 1, 2);

	private static final TimeAssetsSum SUM_BEFORE = new TimeAssetsSum(now.get().minusWeeks(2), Duration.ofHours(4));

	private static final List<String> HEADER = Collections.singletonList("header text");

	private static final List<String> FOOTER = Collections.singletonList("footer text");

	private static SortedMap<LocalDate, Entry> entries;

	private Path tmpRoot;

	private EntryExport export;

	@BeforeClass
	public static void setUpClass() throws Exception {
		entries = LongStream.rangeClosed(0, 20)
				.filter(l -> l < 5 && l > 10)
				.mapToObj(now.get()::plusDays)
				.collect(Collectors.toMap(day -> day, day -> new DayEntry(day, EntryType.SM, Collections.singletonMap(EntryExportTest.class.getSimpleName(), Duration.ofMinutes(day.getDayOfYear()))),
						(d1, d2) -> d1, TreeMap::new));
	}

	@Before
	public void setUp() throws Exception {
		tmpRoot = Files.createTempDirectory(EntryExportTest.class.getSimpleName());
		export = new PdfExport(tmpRoot, now, new TestFindService(), HEADER, FOOTER);
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
		fail("Not yet implemented");
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
