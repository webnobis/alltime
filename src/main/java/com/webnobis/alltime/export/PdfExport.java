package com.webnobis.alltime.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.FindService;

public class PdfExport implements EntryExport {

	private static final String MONTH_FORMAT = "yyyyMM";

	private static final String FILE_EXT = ".pdf";

	private final Path root;

	private final Supplier<LocalDate> now;

	private final FindService findService;

	private final List<String> header;

	private final List<String> footer;

	public PdfExport(Path root, Supplier<LocalDate> now, FindService findService, List<String> header, List<String> footer) {
		this.root = root;
		this.now = now;
		this.findService = findService;
		this.header = header;
		this.footer = footer;

		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	@Override
	public List<Entry> exportRange(LocalDate fromDay, LocalDate untilDay) {
        Objects.requireNonNull(fromDay, "fromDay is null");
        Objects.requireNonNull(untilDay, "untilDay is null");

        List<Entry> entries = findEntries(fromDay, untilDay);
        Path pdfFile = root.resolve(fromDay.format(DateTimeFormatter.ofPattern(MONTH_FORMAT)).concat(FILE_EXT));
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

	private List<Entry> findEntries(LocalDate fromDay, LocalDate untilDay) {
		long days = Duration.between(fromDay.atStartOfDay(), untilDay.atStartOfDay()).toDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(fromDay::plusDays)
				.map(findService::getEntry)
				.filter(entry -> entry != null)
				.collect(Collectors.toList());
	}

}
