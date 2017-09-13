package com.webnobis.alltime.view;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.view.items.Item;
import com.webnobis.alltime.view.items.OldItemsDialog;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class OldBookingDialog extends Dialog<Entry> {

	private final EntryService service;

	private final TimeTransformer timeTransformer;

	private final ValueField<Duration> timeAssetsSum;

	private final ValueField<LocalDate> day;

	private final ValueField<LocalTime> startTime;

	private final ValueField<LocalTime> endTime;

	private final ComboBox<EntryType> type;

	private final ListView<Map.Entry<String, Duration>> items;

	private final Button bookItems;

	private final Button startAZ;

	private final Button endAZ;

	private final Button bookDay;

	private Optional<Entry> entry;

	public OldBookingDialog(LocalDate day, EntryService service, TimeTransformer timeTransformer) {
		super();
		this.service = service;
		this.timeTransformer = timeTransformer;
		entry = Optional.ofNullable(service.getEntry(day));

		timeAssetsSum = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, service.getTimeAssetsSumBefore(day).getTimeAssetsSum());
		timeAssetsSum.setEditable(false);
		timeAssetsSum.setStyle(ViewStyle.READONLY);

		this.day = new ValueField<>(DayTransformer::toDay, DayTransformer::toText, day);
		this.day.setEditable(false);
		this.day.setStyle(ViewStyle.READONLY);

		LocalTime start = entry.flatMap(e -> Optional.ofNullable(e.getStart())).orElse(timeTransformer.now(true));
		startTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, start);

		LocalTime end = entry.flatMap(e -> Optional.ofNullable(e.getEnd())).orElse(timeTransformer.now(false));
		endTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, end);

		type = new ComboBox<>(FXCollections.observableArrayList(EntryType.values()));

		items = new ListView<>();
		// items.setCellFactory(ComboBoxListCell.forListView(items.getItems()));

		startAZ = new Button("Start AZ");
		startAZ.setOnAction(this::startAZ);

		endAZ = new Button("Ende AZ");
		endAZ.setOnAction(this::endAZ);

		bookDay = new Button("Tag buchen");
		bookDay.setOnAction(this::bookDay);

		bookItems = new Button("Einträge");
		bookItems.setOnAction(this::bookItems);

		entry.ifPresent(this::fill);

		GridPane pane = new GridPane();
		pane.add(timeAssetsSum, 0, 0);
		pane.add(this.day, 0, 1);
		pane.add(type, 1, 1);
		pane.add(items, 0, 2);
		pane.add(bookItems, 1, 2);
		pane.add(startTime, 0, 3);
		pane.add(endTime, 1, 3);
		pane.add(startAZ, 0, 4);
		pane.add(endAZ, 1, 4);
		pane.add(bookDay, 2, 4);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(new ButtonType("1"), ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Einträge");

		super.setResultConverter(this::finish);
	}

	private void fill(Entry entry) {
		timeAssetsSum.setValue(timeAssetsSum.getValue().plus(entry.getTimeAssets()));
		if (entry.getStart() != null) {
			startTime.setValue(entry.getStart());
			if (entry.getEnd() != null) {
				endTime.setValue(entry.getEnd());
			}
		}
		type.setValue(entry.getType());
		updateItems(entry.getItems());
	}

	private void updateItems(Map<String, Duration> items) {
		this.items.setItems(FXCollections.observableArrayList(items.entrySet().stream()
				.map(Item::new)
				.sorted()
				.collect(Collectors.toList())));
	}

	private void bookItems(ActionEvent event) {
		Duration bookableDuration = entry
				.filter(e -> EntryType.AZ.equals(e.getType()))
				.map(e -> e.getRealTime().minus(e.getIdleTime()))
				.orElse(Duration.ofDays(1));

		Dialog<Map<String, Duration>> itemsDialog = new OldItemsDialog(day.getValue(), bookableDuration, items.getItems(), 30, service.getLastDescriptions());
		itemsDialog.showAndWait().ifPresent(this::updateItems);
	}

	private void startAZ(ActionEvent event) {
		entry = Optional.ofNullable(service.startAZ(day.getValue(), startTime.getValue()));
		entry.ifPresent(this::fill);
	}

	private void endAZ(ActionEvent event) {
		entry = Optional.ofNullable(service.endAZ(day.getValue(), startTime.getValue(), endTime.getValue(), getItems()));
		entry.ifPresent(this::fill);
	}

	private void bookDay(ActionEvent event) {
		entry = Optional.ofNullable(service.book(day.getValue(), type.getValue(), getItems()));
		entry.ifPresent(this::fill);
	}

	private Map<String, Duration> getItems() {
		return items.getItems().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private Entry finish(ButtonType type) {
		return Optional.ofNullable(type)
				.map(ButtonType::getButtonData)
				.filter(ButtonData.OK_DONE::equals)
				.flatMap(data -> entry)
				.orElse(null);
	}

}
