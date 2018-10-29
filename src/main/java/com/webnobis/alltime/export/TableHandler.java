package com.webnobis.alltime.export;

import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.service.TimeTransformer;
import com.webnobis.alltime.service.WeekDayTransformer;

public class TableHandler {

	private final Document document;

	private final PdfFont font;

	private final float headerFontSize;

	private final float cellFontSize;

	private final EnumSet<EntryTableColumn> entryTableColumns;

	private final List<TimeAssetsSumTableRow> timeAssetsSumTableRows;

	public TableHandler(Document document, PdfFont font, float headerFontSize, float cellFontSize) {
		this.document = document;
		this.font = font;
		this.headerFontSize = headerFontSize;
		this.cellFontSize = cellFontSize;
		entryTableColumns = EnumSet.allOf(EntryTableColumn.class);
		timeAssetsSumTableRows = new ArrayList<>(EnumSet.allOf(TimeAssetsSumTableRow.class));
	}

	public void addEntryTable(List<Entry> entries) {
		Objects.requireNonNull(entries, "entries is null");

		Table table = new Table(entryTableColumns.stream()
				.map(EntryTableColumn::getWidthWeight)
				.map(UnitValue::createPointValue)
				.toArray(UnitValue[]::new), true);
		table.setDocument(document);

		addEntryTableHeader(table);
		IntStream.range(0, entries.size()).forEach(i -> addEntryTableRow(table, i, entries.get(i)));

		table.complete();
	}

	private void addEntryTableHeader(Table table) {
		entryTableColumns.stream()
				.map(EntryTableColumn::getHeader)
				.map(this::headerToCell)
				.forEach(table::addHeaderCell);
	}

	private Cell headerToCell(String text) {
		return new Cell().setBold().setFont(font).setFontSize(headerFontSize).setBackgroundColor(ColorConstants.GRAY, 0.5f)
				.add(new Paragraph(text));
	}

	private void addEntryTableRow(Table table, int row, Entry entry) {
		entryTableColumns.stream()
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
		Cell cell = new Cell().setFont(font).setFontSize(cellFontSize)
				.add(new Paragraph(Optional.ofNullable(text)
						.orElse("")));
		return (row % 2 > 0) ? cell.setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.5f) : cell;
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

	public void addTimeAssetsSumTable(TimeAssetsSum sumBefore, TimeAssetsSum sumNow) {
		Objects.requireNonNull(sumBefore, "sumBefore is null");
		Objects.requireNonNull(sumNow, "sumNow is null");

		Table table = new Table(TimeAssetsSumTableRow.WEIDTH_WEIGHTS, true);
		table.setDocument(document);
		table.setWidth(new UnitValue(UnitValue.PERCENT, 40f));
		table.setHorizontalAlignment(HorizontalAlignment.RIGHT);

		addTimeAssetsSumTableRows(table, sumBefore, sumNow);

		table.complete();
	}

	private void addTimeAssetsSumTableRows(Table table, TimeAssetsSum sumBefore, TimeAssetsSum sumNow) {
		IntStream.range(0, timeAssetsSumTableRows.size())
				.boxed()
				.flatMap(i -> {
					TimeAssetsSumTableRow row = timeAssetsSumTableRows.get(i);
					String description;
					Duration duration;
					switch (row) {
					case SUM_BEFORE:
						description = String.format(row.getDescription(), DayTransformer.toText(sumBefore.getDay()));
						duration = sumBefore.getTimeAssetsSum();
						break;
					case SUM_NEW:
						description = String.format(row.getDescription(), DayTransformer.toText(sumNow.getDay()));
						duration = sumNow.getTimeAssetsSum();
						break;
					default:
						description = row.getDescription();
						duration = Optional.ofNullable(sumNow.getTimeAssetsSum())
								.flatMap(d -> Optional.ofNullable(sumBefore.getTimeAssetsSum()).map(d::minus))
								.orElse(null);
					}
					return Stream.of(contentToCell(i, description), contentToCell(i, DurationFormatter.toString(duration)));
				}).forEach(table::addCell);
	}

}
