package com.webnobis.alltime.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.Entry;

public class EntryStore {
	
	private static final int MAX_COUNT = 100;

	private static final String MONTH_FILE_NAME_FORMAT = "yyyyMM";

	private static final String FILE_EXTENTION = ".dat";

	private static final Comparator<Entry> entryReverseComparator = (e1, e2) -> e2.getDay().compareTo(e1.getDay());

	private final Path root;

	private final Supplier<LocalDate> today;

	private final Function<String, Entry> deserializer;

	private final Function<Entry, String> serializer;

	public EntryStore(Path root, Supplier<LocalDate> today, Function<String, Entry> deserializer, Function<Entry, String> serializer) {
		this.root = root;
		this.today = today;
		this.deserializer = deserializer;
		this.serializer = serializer;
	}

	public Entry getLastEntry() {
		return getLastEntries(1).stream().findFirst().orElse(null);
	}

	public List<Entry> getLastEntries(int maxCount) {
		final int count = Math.min(maxCount, MAX_COUNT);
		return LongStream.range(0, count)
				.mapToObj(today.get()::minusMonths)
				.map(this::getMonthFile)
				.map(this::getEntriesOfMonth)
				.flatMap(List::stream)
				.sorted(entryReverseComparator)
				.limit(count)
				.collect(Collectors.toList());
	}

	private List<Entry> getEntriesOfMonth(Path monthFile) {
		try {
			if (Files.exists(monthFile)) {
				return Files.readAllLines(monthFile, StandardCharsets.UTF_8).stream()
						.map(deserializer::apply)
						.collect(Collectors.toList());
			} else {
				return Collections.emptyList();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Path getMonthFile(LocalDate day) {
		return root.resolve(day.format(DateTimeFormatter.ofPattern(MONTH_FILE_NAME_FORMAT)).concat(FILE_EXTENTION));
	}

	public void storeEntry(Entry entry) {
		Path monthFile = getMonthFile(entry.getDay());
		List<Entry> entries = new ArrayList<>(getEntriesOfMonth(monthFile));

		entries.stream()
				.filter(e -> e.getDay().equals(entry.getDay()))
				.findFirst()
				.ifPresent(entries::remove);
		entries.add(entry);

		Collections.sort(entries, entryReverseComparator);
		try {
			Files.write(monthFile, entries.stream()
					.map(serializer::apply)
					.collect(Collectors.toList()), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}
