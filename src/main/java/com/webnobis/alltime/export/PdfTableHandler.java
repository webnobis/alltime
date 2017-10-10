package com.webnobis.alltime.export;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.IntStream;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.TimeTransformer;

public class PdfTableHandler {

	private final Document document;

	private final PdfFont font;

	private final float headerFontSize;

	private final float cellFontSize;

	private final EnumSet<PdfTableColumn> columns;

	public PdfTableHandler(Document document, PdfFont font, float headerFontSize, float cellFontSize) {
		this.document = document;
		this.font = font;
		this.headerFontSize = headerFontSize;
		this.cellFontSize = cellFontSize;
		columns = EnumSet.allOf(PdfTableColumn.class);
	}

	public void addEntryTable(List<Entry> entries) {
		Table table = new Table(columns.stream()
				.map(PdfTableColumn::getWidthWeight)
				.map(UnitValue::createPointValue)
				.toArray(UnitValue[]::new), true);
		table.setDocument(document);
		
		addHeader(table);
		IntStream.range(0, entries.size()).forEach(i -> addRow(table, i, entries.get(i)));
		
		table.complete();		
	}

	private void addHeader(Table table) {
		columns.stream()
				.map(PdfTableColumn::getHeader)
				.map(this::headerToCell)
				.forEach(table::addHeaderCell);
	}

	private Cell headerToCell(String text) {
		return new Cell().setBold().setFont(font).setFontSize(headerFontSize).add(text);
	}

	private void addRow(Table table, int row, Entry entry) {
		columns.stream()
				.map(column -> {
					switch (column) {
					case DAY:
						return contentToCell(row, DayTransformer.toText(entry.getDay()));
					case START_TIME:
						return contentToCell(row, TimeTransformer.toText(entry.getStart()));
					case END_TIME:
						return contentToCell(row, TimeTransformer.toText(entry.getEnd()));
					default:
						return new Cell();
					}
				}).forEach(table::addCell);

	}

	private Cell contentToCell(int row, String text) {
		Cell cell = new Cell().setFont(font).setFontSize(cellFontSize).add(text);
		return (row % 2 > 0) ? cell.setBackgroundColor(Color.LIGHT_GRAY, 0.5f) : cell;
	}

}
