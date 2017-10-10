package com.webnobis.alltime.export;

import java.time.Duration;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.service.TimeTransformer;
import com.webnobis.alltime.service.WeekDayTransformer;

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
		return new Cell().setBold().setFont(font).setFontSize(headerFontSize).setBackgroundColor(Color.GRAY, 0.5f).add(text);
	}

	private void addRow(Table table, int row, Entry entry) {
		columns.stream()
				.map(column -> {
					switch (column) {
					case DAY:
						return contentToCell(row, DayTransformer.toText(entry.getDay()));
					case WEEKDAY:
						return contentToCell(row, WeekDayTransformer.toText(entry.getDay()));
					case TYPE:
						return contentToCell(row, entry.getType().name());
					case START_TIME:
						return contentToCell(row, TimeTransformer.toText(entry.getStart()));
					case END_TIME:
						return contentToCell(row, TimeTransformer.toText(entry.getEnd()));
					case REAL_TIME:
						return contentToCell(row, DurationFormatter.toString(entry.getRealTime()));
					case EXPECTED_TIME:
						return contentToCell(row, DurationFormatter.toString(entry.getExpectedTime()));
					case IDLE_TIME:
						return contentToCell(row, DurationFormatter.toString(entry.getIdleTime()));
					case TIME_ASSETS:
						return contentToCell(row, DurationFormatter.toString(entry.getTimeAssets()));
					default:
						return contentToCell(row, toString(entry.getItems()));
					}
				}).forEach(table::addCell);

	}

	private Cell contentToCell(int row, String text) {
		Cell cell = new Cell().setFont(font).setFontSize(cellFontSize).add(Optional.ofNullable(text).orElse(""));
		return (row % 2 > 0) ? cell.setBackgroundColor(Color.LIGHT_GRAY, 0.5f) : cell;
	}

	private String toString(Map<String, Duration> items) {
		return new TreeMap<>(items).entrySet().stream()
				.map(entry -> {
					if (Duration.ZERO.equals(entry.getValue())) {
						return entry.getKey();
					} else {
						return DurationFormatter.toString(entry.getValue()).concat(", ").concat(entry.getKey());
					}
				}).collect(Collectors.joining(String.valueOf((char) Character.LINE_SEPARATOR)));
	}

}
