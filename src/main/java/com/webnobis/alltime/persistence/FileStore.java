package com.webnobis.alltime.persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.TimeAssetsSum;

public class FileStore implements EntryStore {

	private static final Function<LocalDate, TimeAssetsSum> alternateStartIfMissingFunction = day -> new TimeAssetsSum(day.minusDays(1), Duration.ZERO);

	private static final String MONTH_FORMAT = "yyyyMM";

	private static final String FILE_EXT = ".dat";

	private static final String TIME_ASSETS_FILE = "timeassets".concat(FILE_EXT);

	private static final Comparator<LocalDate> dayReverseComparator = (d1, d2) -> d2.compareTo(d1);

	private static final Comparator<Entry> entryReverseComparator = (e1, e2) -> dayReverseComparator.compare(e1.getDay(), e2.getDay());

	private static final Comparator<TimeAssetsSum> timeAssetsSumReverseComparator = (s1, s2) -> dayReverseComparator.compare(s1.getDay(), s2.getDay());

	private final Path root;

	private final Supplier<LocalDate> now;

	private final int maxDayCount;

	private final int maxDescriptionCount;

	private final Function<String, LocalDate> dayDeserializer;

	private final Function<String, Entry> entryDeserializer;

	private final Function<Entry, String> entrySerializer;

	private final Function<String, TimeAssetsSum> timeAssetsSumDeserializer;

	private final Function<TimeAssetsSum, String> timeAssetsSumSerializer;

	public FileStore(Path root, Supplier<LocalDate> now, int maxDayCount, int maxDescriptionCount,
			Function<String, LocalDate> dayDeserializer,
			Function<String, Entry> entryDeserializer,
			Function<Entry, String> entrySerializer,
			Function<String, TimeAssetsSum> timeAssetsSumDeserializer,
			Function<TimeAssetsSum, String> timeAssetsSumSerializer) {
		this.root = root;
		this.now = now;
		this.maxDayCount = maxDayCount;
		this.maxDescriptionCount = maxDescriptionCount;
		this.dayDeserializer = dayDeserializer;
		this.entryDeserializer = entryDeserializer;
		this.entrySerializer = entrySerializer;
		this.timeAssetsSumDeserializer = timeAssetsSumDeserializer;
		this.timeAssetsSumSerializer = timeAssetsSumSerializer;

		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	@Override
	public List<LocalDate> getLastDays() {
		YearMonth currentMonth = YearMonth.from(now.get());
		return LongStream.range(0, maxDayCount)
				.mapToObj(currentMonth::minusMonths)
				.map(this::toMonthFile)
				.filter(Files::exists)
				.flatMap(this::getDayStream)
				.limit(maxDayCount)
				.collect(Collectors.toList());
	}

	private Path toMonthFile(YearMonth month) {
		return root.resolve(month.format(DateTimeFormatter.ofPattern(MONTH_FORMAT)).concat(FILE_EXT));
	}

	private Stream<LocalDate> getDayStream(Path file) {
		LocalDate now = this.now.get();
		return readLines(file)
				.map(dayDeserializer::apply)
				.filter(day -> !day.isAfter(now))
				.sorted(dayReverseComparator);
	}

	private Stream<String> readLines(Path file) {
		if (!Files.exists(file)) {
			return Stream.empty();
		}

		try {
			return Files.readAllLines(file, StandardCharsets.UTF_8).stream();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Entry getEntry(Path file, LocalDate day) {
		return readLines(file)
				.filter(line -> day.equals(dayDeserializer.apply(line)))
				.findFirst()
				.map(entryDeserializer::apply)
				.orElse(null);
	}

	@Override
	public Entry getEntry(LocalDate day) {
		Objects.requireNonNull(day, "day is null");

		return getEntry(toMonthFile(YearMonth.from(day)), day);
	}

	@Override
	public TimeAssetsSum getTimeAssetsSumBefore(LocalDate day) {
		return getTimeAssetsSumsBeforeStream(Objects.requireNonNull(day, "day is null"))
				.findFirst()
				.orElseGet(() -> alternateStartIfMissingFunction.apply(day));
	}

	private Stream<TimeAssetsSum> getTimeAssetsSumsBeforeStream(LocalDate day) {
		return readLines(toTimeAssetsFile())
				.map(timeAssetsSumDeserializer::apply)
				.filter(sum -> sum.getDay().isBefore(day))
				.sorted(timeAssetsSumReverseComparator);
	}

	private Path toTimeAssetsFile() {
		return root.resolve(TIME_ASSETS_FILE);
	}

	private void updateTimeAssetsSums(Entry entry) {
		LocalDate day = entry.getDay();
		TimeAssetsSum lastSum = getTimeAssetsSumBefore(day);
		long limit = Duration.between(lastSum.getDay().atStartOfDay(), day.atStartOfDay()).toDays();
		List<String> lines = Stream.concat(getTimeAssetsSumsBeforeStream(day), Stream.concat(
						Stream.iterate(lastSum.getDay(), d -> d.plusDays(1))
							.limit(limit)
							.map(d -> new TimeAssetsSum(d, lastSum.getTimeAssetsSum())),
						Stream.of(new TimeAssetsSum(day, lastSum.getTimeAssetsSum().plus(entry.getTimeAssets())))))
				.distinct()
				.sorted(timeAssetsSumReverseComparator)
				.map(timeAssetsSumSerializer::apply)
				.collect(Collectors.toList());

		try {
			Files.write(toTimeAssetsFile(), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Entry storeEntry(Entry entry) {
		Objects.requireNonNull(entry, "entry is null");

		LocalDate day = entry.getDay();
		Path monthFile = toMonthFile(YearMonth.from(day));
		List<String> lines = readLines(monthFile)
				.filter(line -> !day.equals(dayDeserializer.apply(line)))
				.collect(Collectors.toList());
		lines.add(entrySerializer.apply(entry));

		writeLines(monthFile, lines);
		updateTimeAssetsSums(entry);
		return getEntry(day);
	}

	private void writeLines(Path file, List<String> lines) {
		try {
			Files.write(file, lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public List<String> getLastDescriptions() {
		YearMonth currentMonth = YearMonth.from(now.get());
		return LongStream.range(0, maxDayCount)
				.mapToObj(currentMonth::minusMonths)
				.map(this::toMonthFile)
				.filter(Files::exists)
				.flatMap(this::readLines)
				.map(entryDeserializer::apply)
				.sorted(entryReverseComparator)
				.flatMap(entry -> entry.getItems()
						.keySet().stream()
						.sorted())
				.distinct()
				.limit(maxDescriptionCount)
				.sorted()
				.collect(Collectors.toList());
	}

}
