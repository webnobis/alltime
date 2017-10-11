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

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.FindService;

public class PdfExport implements EntryExport {

	private static final String MONTH_FORMAT = "yyyyMM";

	private static final String FILE_EXT = ".pdf";

	private static final float TABLE_HEADER_FONT_SIZE = 12.5f;

	private static final float TABLE_CELL_FONT_SIZE = 12f;

	private final Path root;

	private final Supplier<LocalDate> now;

	private final FindService findService;

	private final PdfFont font;

	public PdfExport(Path root, Supplier<LocalDate> now, FindService findService) {
		this.root = root;
		this.now = now;
		this.findService = findService;

		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		try {
			font = PdfFontFactory.createFont(FontConstants.HELVETICA);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public List<Entry> exportRange(LocalDate fromDay, LocalDate untilDay) {
		Objects.requireNonNull(fromDay, "fromDay is null");
		Objects.requireNonNull(untilDay, "untilDay is null");

		List<Entry> entries = findEntries(fromDay, untilDay);
		TimeAssetsSum sumBefore = findService.getTimeAssetsSumBefore(fromDay);
		TimeAssetsSum sumNow = findService.getTimeAssetsSumBefore(untilDay.plusDays(1));
		Path pdfFile = root.resolve(fromDay.format(DateTimeFormatter.ofPattern(MONTH_FORMAT)).concat(FILE_EXT));

		try (PdfWriter writer = new PdfWriter(pdfFile.toFile()); PdfDocument pdfDocument = new PdfDocument(writer); Document document = new Document(pdfDocument, PageSize.A4.rotate())) {
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, this::addHeaderAndFooter);
			
			TableHandler tableHandler = new TableHandler(document, font, TABLE_HEADER_FONT_SIZE, TABLE_CELL_FONT_SIZE);
			tableHandler.addEntryTable(entries);
			document.add(new Paragraph());
			document.add(new Paragraph());
			tableHandler.addTimeAssetsSumTable(sumBefore, sumNow);
			
			return entries;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private List<Entry> findEntries(LocalDate fromDay, LocalDate untilDay) {
		long days = Duration.between(fromDay.atStartOfDay(), untilDay.atStartOfDay()).toDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(fromDay::plusDays)
				.map(findService::getEntry)
				.filter(entry -> entry != null)
				.collect(Collectors.toList());
	}
	
	private void addHeaderAndFooter(Event event) {
		
	}

}
