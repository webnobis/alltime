package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.view.DayTransformer;
import com.webnobis.alltime.view.ValueField;
import com.webnobis.alltime.view.ViewStyle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemsDialog extends Dialog<Map<String, Duration>> {

	private final Entry entry;

	private final int minutesRaster;

	private final TextField day;

	private final ValueField<Duration> durationsToBook;

	private final ComboBox<Map.Entry<String, Duration>> items;

	private final ComboBox<String> lastDescriptions;

	private final ComboBox<Duration> duration;

	private final TextField description;

	private final Button add;

	private final Button change;

	private final Button delete;

	public ItemsDialog(Entry entry, int minutesRaster, List<String> lastDescriptions) {
		super();
		this.entry = Objects.requireNonNull(entry, "entry is null");
		this.minutesRaster = minutesRaster;

		day = new TextField(DayTransformer.toText(entry.getDay()));
		day.setEditable(false);
		day.setStyle(ViewStyle.READONLY);

		items = new ComboBox<>(FXCollections.observableArrayList(entry.getItems().entrySet().stream()
				.map(Item::new)
				.sorted()
				.collect(Collectors.toList())));
		items.setConverter(new ItemStringConverter());
		items.setOnAction(this::getItem);

		durationsToBook = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, getDurationsToBook());
		durationsToBook.setEditable(false);
		durationsToBook.setStyle(ViewStyle.READONLY);

		this.lastDescriptions = new ComboBox<>(FXCollections.observableArrayList(Objects.requireNonNull(lastDescriptions, "lastDescriptions is null")));
		this.lastDescriptions.setOnAction(this::getDescription);

		duration = new ComboBox<>(getSelectableDurations());
		duration.setConverter(new DurationStringConverter());

		description = new TextField();

		add = new Button(" + ");
		add.setOnAction(this::updateItem);

		change = new Button("a|b");
		change.setOnAction(this::updateItem);

		delete = new Button(" - ");
		delete.setOnAction(this::updateItem);

		GridPane pane = new GridPane();
		pane.add(day, 0, 0);
		pane.add(durationsToBook, 1, 0);
		pane.add(items, 0, 1);
		pane.add(this.lastDescriptions, 1, 1);
		pane.add(duration, 0, 2);
		pane.add(description, 1, 2);
		pane.add(add, 2, 2);
		pane.add(change, 3, 2);
		pane.add(delete, 4, 2);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Einträge");

		super.setResultConverter(this::finish);
	}

	private Duration getDurationsToBook() {
		Duration booked = items.getItems().stream()
				.map(Map.Entry::getValue)
				.reduce((d1, d2) -> d1.plus(d2))
				.orElse(Duration.ZERO);
		return entry.getRealTime().minus(entry.getIdleTime()).minus(booked);
	}

	private ObservableList<Duration> getSelectableDurations() {
		return getSelectableDurations(null);
	}

	private ObservableList<Duration> getSelectableDurations(Duration selectedDuration) {
		Duration maxDuration = getDurationsToBook();
		if (selectedDuration != null && maxDuration.minus(selectedDuration).isNegative()) {
			maxDuration = selectedDuration;
		}

		List<Duration> durations = new ArrayList<>();
		Duration duration = Duration.ZERO;
		while (maxDuration.compareTo(duration) > 0) {
			duration = duration.plusMinutes(minutesRaster);
			durations.add(duration);
		}
		Collections.reverse(durations);
		return FXCollections.observableArrayList(durations);
	}

	private void getItem(ActionEvent event) {
		Optional.ofNullable(items.getValue())
				.ifPresent(item -> {
					duration.setItems(getSelectableDurations(item.getValue()));
					duration.setValue(item.getValue());
					description.setText(item.getKey());

					change.setDisable(false);
					duration.requestFocus();
				});
	}

	private void getDescription(ActionEvent event) {
		Optional.ofNullable(lastDescriptions.getValue())
				.ifPresent(description::setText);
	}

	private void updateItem(ActionEvent event) {
		if (duration.getValue() == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(String.format("Es ist keine Dauer %s.", (duration.getItems().isEmpty()) ? "mehr verfügbar" : "ausgewählt"));
			alert.show();
		} else if (description.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Es ist keine Beschreibung eingetragen.");
			alert.show();
		} else if (items.getItems().stream()
				.anyMatch(entry -> description.getText().equals(entry.getKey()))) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(String.format("'%s' als Beschreibung ist bereits vorhanden.", description.getText()));
			alert.show();
		} else {
			Map.Entry<String, Duration> item = new Item(description.getText(), duration.getValue());

			if (add.equals(event.getSource())) {
				items.getItems().add(item);

				items.setValue(item);
			} else {
				int index = items.getSelectionModel().selectedIndexProperty().intValue();
				items.getItems().set(index, item);
			}
			
			durationsToBook.setValue(getDurationsToBook());
			duration.setItems(getSelectableDurations());
		}
	}

	private Map<String, Duration> finish(ButtonType type) {
		return Optional.ofNullable(type)
				.map(ButtonType::getButtonData)
				.filter(ButtonData.OK_DONE::equals)
				.map(data -> items.getItems().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
				.orElse(null);
	}

	private class Item implements Map.Entry<String, Duration>, Comparable<Item> {

		private final String key;

		private final Duration value;

		public Item(String key, Duration value) {
			this.key = key;
			this.value = value;
		}

		public Item(Map.Entry<String, Duration> entry) {
			this(entry.getKey(), entry.getValue());
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Duration getValue() {
			return value;
		}

		@Override
		public Duration setValue(Duration value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(Item other) {
			return key.compareTo(other.key);
		}

	}

}
