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
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.FindService;

public class PdfExport implements EntryExport {

	private static final String MONTH_FORMAT = "yyyyMM";

	private static final String DATE_FORMAT = "dd.MM.";

	private static final String DATE_FORMAT_LONG = "dd.MM.yyyy";

	private static final String FILE_EXT = ".pdf";

	private static final String PAGE_TITLE = "Buchungen vom %s bis %s";

	private static final String PAGE_NUMBER = "Seite %d";

	private static final float PAGE_MARGIN = 2 * (72 * 1 / 2.54f); // 2 x 1cm

	private static final float TABLE_HEADER_FONT_SIZE = 12f;

	private static final float TABLE_CELL_FONT_SIZE = 11f;

	private static final float HEADER_FOOTER_FONT_SIZE = 10f;

	private final Path root;

	private final FindService findService;

	public PdfExport(Path root, FindService findService) {
		this.root = root;
		this.findService = findService;

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
		TimeAssetsSum sumBefore = findService.getTimeAssetsSumBefore(fromDay);
		TimeAssetsSum sumNow = findService.getTimeAssetsSumBefore(untilDay.plusDays(1));
		Path pdfFile = root.resolve(fromDay.format(DateTimeFormatter.ofPattern(MONTH_FORMAT)).concat(FILE_EXT));

		try (PdfWriter writer = new PdfWriter(pdfFile.toFile()); PdfDocument pdfDocument = new PdfDocument(writer); Document document = new Document(pdfDocument, PageSize.A4.rotate())) {
			PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
			document.setMargins(PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, event -> addHeaderAndFooter((PdfDocumentEvent) event, font, getTitle(fromDay, untilDay)));

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

	private static String getTitle(LocalDate fromDay, LocalDate untilDay) {
		String from = fromDay.format(DateTimeFormatter.ofPattern((fromDay.getYear() == untilDay.getYear())? DATE_FORMAT: DATE_FORMAT_LONG));
		String until = untilDay.format(DateTimeFormatter.ofPattern(DATE_FORMAT_LONG));
		return String.format(PAGE_TITLE, from, until);
	}

	private List<Entry> findEntries(LocalDate fromDay, LocalDate untilDay) {
		long days = Duration.between(fromDay.atStartOfDay(), untilDay.atStartOfDay()).toDays();
		return LongStream.rangeClosed(0, days)
				.mapToObj(fromDay::plusDays)
				.map(findService::getEntry)
				.filter(entry -> entry != null)
				.collect(Collectors.toList());
	}

	private void addHeaderAndFooter(PdfDocumentEvent event, PdfFont font, String title) {
		PdfDocument pdfDocument = event.getDocument();
		PdfPage page = event.getPage();
		PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdfDocument);
		String pageNumber = String.format(PAGE_NUMBER, pdfDocument.getPageNumber(page));
		Rectangle pageSize = page.getPageSize();
		addHeaderAndFooter(pdfCanvas, pageSize, font, title, pageNumber);
	}

	private void addHeaderAndFooter(PdfCanvas pdfCanvas, Rectangle pageSize, PdfFont font, String title, String pageNumber) {
		float top = pageSize.getTop();
		float bottom = pageSize.getBottom();
		float width = pageSize.getWidth();
		float titleWidth = font.getWidth(title, HEADER_FOOTER_FONT_SIZE);
		float pageNumberWidth = font.getWidth(pageNumber, HEADER_FOOTER_FONT_SIZE);
		pdfCanvas.beginText()
				.setFontAndSize(font, HEADER_FOOTER_FONT_SIZE)
				.moveTo((width - titleWidth) / 2, top - PAGE_MARGIN + HEADER_FOOTER_FONT_SIZE)
				.showText(title)
				.moveTo((width - pageNumberWidth) / 2, bottom + PAGE_MARGIN - HEADER_FOOTER_FONT_SIZE)
				.showText(pageNumber)
				.endText();
		pdfCanvas.release();
	}

}
