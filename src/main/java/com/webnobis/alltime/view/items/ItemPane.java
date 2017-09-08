package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class ItemPane extends GridPane {

	static final Item NEW_ITEM = new Item("--- Neuer Eintrag ---", Duration.ZERO);

	private final Consumer<Map.Entry<String, Duration>> itemAdd;

	private final Consumer<Map.Entry<String, Duration>> itemChange;

	private final Consumer<Map.Entry<String, Duration>> itemDelete;

	private final ComboBox<Duration> duration;

	private final ComboBox<String> lastDescriptions;

	private final TextField description;

	private final Button add;

	private final Button change;

	private final Button delete;

	public ItemPane(Consumer<Entry<String, Duration>> itemAdd, Consumer<Entry<String, Duration>> itemChange, Consumer<Entry<String, Duration>> itemDelete, List<String> lastDescriptions) {
		super();

		this.itemAdd = itemAdd;
		this.itemChange = itemChange;
		this.itemDelete = itemDelete;

		duration = new ComboBox<>();
		duration.setConverter(new DurationStringConverter());

		this.lastDescriptions = new ComboBox<>(FXCollections.observableArrayList(lastDescriptions));
		this.lastDescriptions.setOnAction(this::copyDescription);

		description = new TextField();

		add = new Button(" + ");
		add.setOnAction(this::update);

		change = new Button("a|b");
		change.setOnAction(this::update);

		delete = new Button(" - ");
		delete.setOnAction(this::update);

		disableAll();

		super.add(this.lastDescriptions, 0, 0);
		super.add(duration, 0, 1);
		super.add(description, 1, 1);
		super.add(add, 2, 1);
		super.add(change, 3, 1);
		super.add(delete, 4, 1);
	}

	private void disableAll() {
		this.lastDescriptions.setDisable(true);
		duration.setDisable(true);
		description.setDisable(true);
		add.setDisable(true);
		change.setDisable(true);
		delete.setDisable(true);
	}

	public void setItem(Map.Entry<String, Duration> item, List<Duration> selectableDurations) {
		Objects.requireNonNull(item, "item is null");
		Objects.requireNonNull(selectableDurations, "selectableDurations is null");

		if (selectableDurations.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Es sind keine weiteren Zeiten buchbar.");
		} else {
			duration.setItems(FXCollections.observableArrayList(selectableDurations));

			if (NEW_ITEM.equals(item)) {
				lastDescriptions.setDisable(false);
				add.setDisable(false);
				change.setDisable(true);
				delete.setDisable(true);

				duration.setValue(selectableDurations.stream().findFirst()
						.orElse(null));
				description.setText("");
			} else {
				lastDescriptions.setDisable(true);
				add.setDisable(true);
				change.setDisable(false);
				delete.setDisable(false);

				duration.setValue(item.getValue());
				description.setText(item.getKey());
			}

			duration.setDisable(false);
			description.setDisable(false);
		}
	}

	private void copyDescription(ActionEvent event) {
		description.setText(lastDescriptions.getValue());
	}

	private void update(ActionEvent event) {
		Map.Entry<String, Duration> item = new Item(description.getText(), duration.getValue());
		boolean addToLastDescriptions = !lastDescriptions.getItems().contains(item.getKey());

		if (add.equals(event.getSource())) {
			itemAdd.accept(item);
		} else if (change.equals(event.getSource())) {
			itemChange.accept(item);
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText(String.format("Soll der Eintrag %s wirklich gelöscht werden?", item.getKey()));
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			if (alert.showAndWait()
					.filter(ButtonType.OK::equals)
					.isPresent()) {
				itemDelete.accept(item);
				addToLastDescriptions = false;
			}
		}

		if (addToLastDescriptions) {
			lastDescriptions.getItems().add(0, item.getKey());
		}

		disableAll();
	}

}
