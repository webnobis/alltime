package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemsDialog extends Dialog<Map<String, Duration>> {

	private final Duration bookableDuration;

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

	public ItemsDialog(LocalDate day, Duration bookableDuration, ObservableList<Map.Entry<String, Duration>> items, int minutesRaster, List<String> lastDescriptions) {
		super();
		this.bookableDuration = Objects.requireNonNull(bookableDuration, "bookableDuration is null");
		this.minutesRaster = minutesRaster;

		this.day = new TextField(DayTransformer.toText(Objects.requireNonNull(day, "day is null")));
		this.day.setEditable(false);
		this.day.setStyle(ViewStyle.READONLY);

		this.items = new ComboBox<>(Objects.requireNonNull(items, "items is null"));
		this.items.setOnAction(this::getItem);

		this.durationsToBook = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, getDurationsToBook());
		this.durationsToBook.setEditable(false);
		this.durationsToBook.setStyle(ViewStyle.READONLY);

		this.lastDescriptions = new ComboBox<>(FXCollections.observableArrayList(Objects.requireNonNull(lastDescriptions, "lastDescriptions is null")));
		this.lastDescriptions.setOnAction(this::getDescription);

		duration = new ComboBox<>();
		duration.setConverter(new DurationStringConverter());
		duration.itemsProperty().addListener((observable, oldValue, newValue) -> selectFirstOrDisableDuration(newValue));
		duration.setItems(getSelectableDurations());

		description = new TextField();

		add = new Button(" + ");
		add.setOnAction(this::updateItem);

		change = new Button("a|b");
		change.setOnAction(this::updateItem);

		delete = new Button(" - ");
		delete.setOnAction(this::updateItem);

		toggleButtons(false);

		GridPane pane = new GridPane();

		pane.add(new Label("Buchungstag: "), 0, 0);
		pane.add(this.day, 1, 0);
		pane.add(new Label("Verbleibende buchbare Zeit: "), 3, 0);
		pane.add(durationsToBook, 4, 0);

		pane.add(new Label("Buchungen: "), 0, 1);
		pane.add(this.items, 1, 1);
		pane.add(new Label("Bisherige Buchungstexte: "), 3, 1);
		pane.add(this.lastDescriptions, 4, 1);

		pane.add(new Label("Zeitdauer: "), 0, 2);
		pane.add(duration, 1, 2);
		pane.add(new Label("Buchungstext: "), 3, 2);
		pane.add(description, 4, 2);
		pane.add(new Label("   "), 5, 2);
		pane.add(add, 6, 2);
		pane.add(change, 7, 2);
		pane.add(delete, 8, 2);

		pane.add(new Label("   "), 2, 0, 1, 3);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Buchungen");

		super.setResultConverter(this::finish);
	}

	private void toggleButtons(boolean addDisabled) {
		add.setDisable(addDisabled);
		change.setDisable(!addDisabled);
		delete.setDisable(!addDisabled);
	}

	private Duration getDurationsToBook() {
		Duration booked = items.getItems().stream()
				.map(Map.Entry::getValue)
				.reduce((d1, d2) -> d1.plus(d2))
				.orElse(Duration.ZERO);
		return bookableDuration.minus(booked);
	}

	private ObservableList<Duration> getSelectableDurations() {
		return getSelectableDurations(null);
	}

	private ObservableList<Duration> getSelectableDurations(Duration selectedDuration) {
		Duration maxDuration = getDurationsToBook();
		if (selectedDuration != null && maxDuration.compareTo(selectedDuration) < 0) {
			maxDuration = selectedDuration;
		}

		List<Duration> durations = new ArrayList<>();
		Duration duration = Duration.ofMinutes(minutesRaster);
		while (maxDuration.compareTo(duration) >= 0) {
			durations.add(duration);
			duration = duration.plusMinutes(minutesRaster);
		}
		Collections.reverse(durations);
		return FXCollections.observableArrayList(durations);
	}

	private void selectFirstOrDisableDuration(ObservableList<Duration> items) {
		duration.setValue(items.stream().findFirst().orElse(null));
		duration.setDisable(items.isEmpty());
	}

	private void getItem(ActionEvent event) {
		Optional.ofNullable(items.getValue())
				.ifPresent(item -> {
					duration.setItems(getSelectableDurations(item.getValue()));
					duration.setValue(item.getValue());
					description.setText(item.getKey());

					toggleButtons(true);
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
			alert.setContentText(String.format("Es ist keine Zeitdauer %s.", (duration.getItems().isEmpty()) ? "mehr verfügbar" : "ausgewählt"));
			alert.show();
		} else if (description.getText().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Es ist kein Buchungstext eingetragen.");
			alert.show();
		} else if (add.equals(event.getSource()) && items.getItems().stream()
				.anyMatch(entry -> description.getText().equals(entry.getKey()))) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(String.format("'%s' als Buchungstext ist bereits vorhanden.", description.getText()));
			alert.show();
		} else {
			Map.Entry<String, Duration> item = new Item(description.getText(), duration.getValue());
			if (add.equals(event.getSource())) {
				items.getItems().add(item);
			} else if (change.equals(event.getSource())) {
				int index = items.getSelectionModel().selectedIndexProperty().intValue();
				items.getItems().set(index, item);
			} else {
				int index = items.getSelectionModel().selectedIndexProperty().intValue();
				items.getItems().remove(index);
			}

			durationsToBook.setValue(getDurationsToBook());
			duration.setItems(getSelectableDurations());
			duration.setValue(duration.getItems().stream().findFirst().orElse(null));
			description.setText("");

			toggleButtons(false);
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

}
