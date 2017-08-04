package com.webnobis.alltime.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;

public class EntryStoreTest {

	private static final LocalDate DAY = LocalDate.of(2017, Month.JULY, 1);

	private static final String JUNE_FILE = "201706.dat";

	private static final String JULY_FILE = "201707.dat";

	private static final String CONTENT = EntryStoreTest.class.getName();

	private static final Entry ENTRY = new Entry1();

	private Path tmpFolder;

	private Path juneFile;

	private Path julyFile;

	private EntryStore store;

	@Before
	public void setUp() throws Exception {
		tmpFolder = Files.createTempDirectory(EntryStoreTest.class.getSimpleName());
		juneFile = tmpFolder.resolve(JUNE_FILE);
		julyFile = tmpFolder.resolve(JULY_FILE);

		store = new EntryStore(tmpFolder, line -> ENTRY, entry -> CONTENT);
	}

	@After
	public void tearDown() throws Exception {
		Files.delete(juneFile);
		Files.delete(julyFile);
		Files.delete(tmpFolder);
	}

	@Test
	public void testStoreAndGet() throws IOException {
		store.storeEntry(ENTRY);
		assertTrue(Files.exists(julyFile));
		assertEquals(Collections.singletonList(CONTENT), Files.readAllLines(julyFile, StandardCharsets.UTF_8));

		{
			Entry e = store.getLastEntry();
			assertSame(ENTRY, e);
			assertEquals(DAY, e.getDay());
		}

		assertEquals(Collections.singletonList(ENTRY), store.getLastEntries(Integer.MAX_VALUE));

		{
			Entry e = new Entry2();
			store.storeEntry(e);
			assertTrue(Files.exists(juneFile));

			assertEquals(Arrays.asList(ENTRY, ENTRY), store.getLastEntries(Integer.MAX_VALUE));
		}
	}

	private static class Entry1 implements Entry {

		@Override
		public LocalDate getDay() {
			return DAY;
		}

		@Override
		public EntryType getType() {
			return EntryType.GT;
		}

		@Override
		public LocalTime getStart() {
			return null;
		}

		@Override
		public Map<String, Duration> getItems() {
			return null;
		}

		@Override
		public Duration getTimeAssets() {
			return null;
		}

	}

	private class Entry2 extends Entry1 {

		@Override
		public LocalDate getDay() {
			return DAY.minusDays(1);
		}

	}

}
