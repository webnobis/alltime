package com.webnobis.alltime.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;

public class FileEntryStore implements EntryStore {

	private static final int MAX_COUNT = 100;

	private static final String MONTH_FILE_NAME_FORMAT = "yyyyMM";

	private static final String FILE_EXTENTION = ".dat";

	private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^([0-9]{" + MONTH_FILE_NAME_FORMAT.length() + "})\\" + FILE_EXTENTION + '$');

	private static final Comparator<Entry> entryReverseComparator = (e1, e2) -> e2.getDay().compareTo(e1.getDay());

	private static final Comparator<YearMonth> monthReverseComparator = (m1, m2) -> m2.compareTo(m1);

	private final Path root;

	private final Function<String, Entry> deserializer;

	private final Function<Entry, String> serializer;

	public FileEntryStore(Path root, Function<String, Entry> deserializer, Function<Entry, String> serializer) {
		this.root = root;
		this.deserializer = deserializer;
		this.serializer = serializer;
	}

	@Override
	public List<Entry> getLastEntries(int maxCount) {
		final int count = Math.min(maxCount, MAX_COUNT);
		return findMonths(count)
				.map(this::getMonthFile)
				.map(this::getEntriesOfMonth)
				.flatMap(List::stream)
				.sorted(entryReverseComparator)
				.limit(count)
				.collect(Collectors.toList());
	}

	private Stream<YearMonth> findMonths(int maxCount) {
		try {
			return Files.walk(root)
					.filter(file -> Files.isRegularFile(file))
					.map(Path::getFileName)
					.map(Path::toString)
					.map(FILE_NAME_PATTERN::matcher)
					.filter(Matcher::find)
					.map(matcher -> matcher.group(1))
					.map(month -> YearMonth.parse(month, DateTimeFormatter.ofPattern(MONTH_FILE_NAME_FORMAT)))
					.sorted(monthReverseComparator)
					.limit(maxCount);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
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

	private Path getMonthFile(YearMonth month) {
		return root.resolve(month.format(DateTimeFormatter.ofPattern(MONTH_FILE_NAME_FORMAT)).concat(FILE_EXTENTION));
	}

	private Path getMonthFile(LocalDate day) {
		return getMonthFile(YearMonth.of(day.getYear(), day.getMonth()));
	}

	@Override
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
