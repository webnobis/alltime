package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.view.DayTransformer;

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

	private final ComboBox<Map.Entry<String, Duration>> items;

	private final ComboBox<Duration> duration;

	private final TextField description;

	private final Button add;

	private final Button change;

	public ItemsDialog(Entry entry, int minutesRaster) {
		super();
		this.entry = Objects.requireNonNull(entry, "entry is null");
		this.minutesRaster = minutesRaster;

		day = new TextField(DayTransformer.toText(entry.getDay()));
		day.setEditable(false);

		items = new ComboBox<>(FXCollections.observableArrayList(entry.getItems().entrySet().stream()
				.map(Item::new)
				.sorted()
				.collect(Collectors.toList())));
		items.setConverter(new ItemStringConverter());
		items.setOnAction(this::getItem);

		duration = new ComboBox<>(getSelectableDurations());
		duration.setConverter(new DurationStringConverter());
		duration.focusedProperty().addListener((observable, oldValue, newValue) -> updateSelectableDurations(newValue.booleanValue()));

		description = new TextField("Beschreibung");

		add = new Button(" + ");
		add.setOnAction(this::updateItem);

		change = new Button("a|b");
		change.setDisable(true);
		change.setOnAction(this::updateItem);

		GridPane pane = new GridPane();
		pane.add(this.day, 0, 0);
		pane.add(this.items, 0, 1);
		pane.add(duration, 0, 2);
		pane.add(description, 1, 2);
		pane.add(add, 2, 2);
		pane.add(change, 3, 2);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Einträge");

		super.setResultConverter(this::finish);
	}

	private void updateSelectableDurations(boolean hasFocus) {
		Optional.ofNullable(duration.getValue())
				.filter(unused -> !hasFocus)
				.ifPresent(selection -> {
					ObservableList<Duration> selectableDurations = getSelectableDurations();
					duration.setItems(selectableDurations);
					duration.setValue(selectableDurations.stream().findFirst()
							.map(maxDuration -> (maxDuration.compareTo(selection) < 1) ? maxDuration : selection)
							.orElse(null));
				});
	}

	private ObservableList<Duration> getSelectableDurations() {
		return getSelectableDurations(null);
	}

	private ObservableList<Duration> getSelectableDurations(Duration selectedDuration) {
		Duration maxDuration = Stream.concat(Stream.of(entry.getIdleTime()),
				items.getItems().stream()
						.map(Map.Entry::getValue))
				.reduce((d1, d2) -> d1.plus(d2))
				.map(entry.getRealTime()::minus)
				.orElse(Duration.ZERO);
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

	private void updateItem(ActionEvent event) {
		if (duration.getValue() == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(String.format("Es ist keine Dauer %s.", (duration.getItems().isEmpty()) ? "mehr verfügbar" : "ausgewählt"));
			alert.show();
		} else if (description.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Es ist keine Beschreibung eingetragen.");
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
