package com.webnobis.alltime.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.webnobis.alltime.model.Entry;

public class PdfTableHandler {
	
	private static final int COLS = 11;
	
	private static final float ROW_HEIGHT = 20f;
	
	private static final float CELL_FONT_SIZE = 12f;

	private final PDDocument doc;
	
	private final PDFont font;
	
	private final int maxRows;

	public PdfTableHandler(PDDocument doc, PDFont font) {
		this.doc = doc;
		this.font = font;
		maxRows = (int) (new PDPage().getMediaBox().getHeight() / ROW_HEIGHT);
	}

	public int getMaxRows() {
		return maxRows;
	}

	public PDPage createPageWithEntryTable(List<Entry> entries) {
		Objects.requireNonNull(entries, "entries is null");
		int rows = entries.size();
		if (rows > maxRows) {
			throw new TooMuchTableRowsException(rows, maxRows);
		}
		PDPage page = new PDPage();
		try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)){
			contentStream.setFont(font, CELL_FONT_SIZE);
			addCell(contentStream, 200, 400, "Hallo max");
			return page;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void addCell(PDPageContentStream contentStream, float tx, float ty, String content) throws IOException {
		contentStream.beginText();
		contentStream.newLineAtOffset(tx, ty);
		contentStream.showText(content);
		contentStream.endText();
	}

}
