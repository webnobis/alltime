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

	private static final String FILE_EXT = ".pdf";

	private static final String PAGE_TITLE = "Buchungen von %s bis %s";

	private static final String PAGE_NUMBER = "Seite %d";

	private static final float PAGE_MARGIN = 2 * (72 * 1 / 2.54f); // 2 x 1cm

	private static final float TABLE_HEADER_FONT_SIZE = 12f;

	private static final float TABLE_CELL_FONT_SIZE = 11f;

	private static final float HEADER_FOOTER_FONT_SIZE = 10f;

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
			document.setMargins(PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, event -> addHeaderAndFooter((PdfDocumentEvent) event, String.format(PAGE_TITLE, fromDay, untilDay), pdfFile));

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

	private void addHeaderAndFooter(PdfDocumentEvent event, String title, Path pdfFile) {
		PdfDocument pdfDocument = event.getDocument();
		PdfPage page = event.getPage();
		String pageNumber = String.format(PAGE_NUMBER, pdfDocument.getPageNumber(page));
		Rectangle pageSize = page.getPageSize();
		float top = pageSize.getTop();
		float bottom = pageSize.getBottom();
		float width = pageSize.getWidth();
		float textWidth = font.getWidth(title, HEADER_FOOTER_FONT_SIZE);
		float pageNumberWidth = font.getWidth(pageNumber, HEADER_FOOTER_FONT_SIZE);
		PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdfDocument);
		pdfCanvas.beginText()
				.setFontAndSize(font, HEADER_FOOTER_FONT_SIZE)
				.moveTo((width - textWidth) / 2, top - PAGE_MARGIN + HEADER_FOOTER_FONT_SIZE)
				.showText(title)
				.moveTo((width - pageNumberWidth) / 2, bottom + PAGE_MARGIN)
				.showText(pageNumber)
				.endText();
		pdfCanvas.release();
	}

}
