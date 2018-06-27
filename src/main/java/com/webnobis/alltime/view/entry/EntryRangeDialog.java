package com.webnobis.alltime.view.entry;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.webnobis.alltime.Alltime;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.view.ValueField;
import com.webnobis.alltime.view.ViewStyle;
import com.webnobis.alltime.view.items.Item;
import com.webnobis.alltime.view.items.ItemListView;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class EntryRangeDialog extends Dialog<List<Entry>> {

	private static final int PREF_WIDTH = 90;
	
	private static final Set<EntryType> SELECTABLE_TYPES = EnumSet.of(EntryType.WE, EntryType.UR, EntryType.KR, EntryType.SM, EntryType.FT, EntryType.SO);

	private final BookingService bookingService;

	final ValueField<Duration> timeAssetsSum;

	final ValueField<LocalDate> fromDay;

	final ValueField<LocalDate> untilDay;

	final ComboBox<EntryType> type;

	final ListView<Item> items;

	public EntryRangeDialog(BookingService bookingService,
			int itemDurationRasterMinutes, LocalDate fromDay, LocalDate untilDay,
			TimeAssetsSum sum, List<String> lastDescriptions, Optional<Entry> firstEntry) {
		super();
		this.bookingService = bookingService;
		
		timeAssetsSum = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, sum.getTimeAssetsSum());
		timeAssetsSum.setEditable(false);
		timeAssetsSum.setStyle(ViewStyle.READONLY + ViewStyle.BIG);
		timeAssetsSum.setPrefWidth(PREF_WIDTH * 2);
		timeAssetsSum.setAlignment(Pos.CENTER);
		timeAssetsSum.setTooltip(new Tooltip(String.format("Stand: %s", DayTransformer.toText(sum.getDay()))));

		this.fromDay = new ValueField<>(DayTransformer::toDay, DayTransformer::toText, fromDay);
		this.fromDay.setEditable(false);
		this.fromDay.setStyle(ViewStyle.READONLY);
		this.fromDay.setPrefWidth(PREF_WIDTH);
		this.fromDay.setAlignment(Pos.CENTER);

		this.untilDay = new ValueField<>(DayTransformer::toDay, DayTransformer::toText, untilDay);
		this.untilDay.setEditable(false);
		this.untilDay.setStyle(ViewStyle.READONLY);
		this.untilDay.setPrefWidth(PREF_WIDTH);
		this.untilDay.setAlignment(Pos.CENTER);

		items = new ItemListView(itemDurationRasterMinutes, lastDescriptions,
				() -> Duration.ZERO,
				firstEntry.map(Entry::getItems)
				.orElse(Collections.emptyMap()));

		type = new ComboBox<>(FXCollections.observableArrayList(SELECTABLE_TYPES));
		type.setPrefWidth(PREF_WIDTH);

		type.setValue(firstEntry.map(Entry::getType)
				.filter(t -> type.getItems().stream()
						.anyMatch(t::equals))
				.orElse(getDefaultType(fromDay, untilDay)));

		GridPane pane = new GridPane();
		pane.add(new Label("Zeitguthaben:"), 0, 0);
		pane.add(timeAssetsSum, 1, 0, 5, 1);

		pane.add(new Label("Buchungszeitraum:"), 0, 1);
		pane.add(this.fromDay, 1, 1);
		pane.add(new Label("-"), 2, 1);
		pane.add(this.untilDay, 3, 1);
		pane.add(new Label("Buchungstyp:"), 4, 1);
		pane.add(type, 5, 1);

		pane.add(new Label("Einträge:"), 0, 4, 6, 1);
		pane.add(items, 0, 5, 6, 1);

		pane.setHgap(5);
		pane.setVgap(5);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Buchungen");

		super.setTitle(Alltime.TITLE);
		super.setResultConverter(this::get);
	}

	List<Entry> get(ButtonType button) {
		if (Optional.ofNullable(button)
				.filter(ButtonType.APPLY::equals)
				.isPresent()) {
			Map<String, Duration> items = this.items.getItems().stream()
					.filter(item -> !ItemListView.NEW_TRIGGER.equals(item))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			return bookingService.book(fromDay.getValue(), untilDay.getValue(), type.getValue(), items);
		}
		return null;
	}
	
	private static EntryType getDefaultType(LocalDate fromDay, LocalDate untilDay) {
		if (fromDay != null && untilDay != null) {
			if (Period.between(fromDay, untilDay).getDays() < 3) {
				if (DayOfWeek.SATURDAY.equals(fromDay.getDayOfWeek()) && DayOfWeek.SUNDAY.equals(untilDay.getDayOfWeek())) {
					return EntryType.WE;
				}
			}
		}
		return EntryType.UR;
	}

}
