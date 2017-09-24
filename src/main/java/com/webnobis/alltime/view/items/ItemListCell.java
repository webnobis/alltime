package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;

public class ItemListCell extends ListCell<Item> {

	private final int itemDurationRasterMinutes;

	private final List<String> lastDescriptions;

	private final Supplier<Duration> maxDurationRange;

	public ItemListCell(int itemDurationRasterMinutes, List<String> lastDescriptions, Supplier<Duration> maxDurationRange) {
		super();
		this.itemDurationRasterMinutes = itemDurationRasterMinutes;
		this.lastDescriptions = lastDescriptions;
		this.maxDurationRange = maxDurationRange;
	}

	@Override
	protected void updateItem(Item item, boolean empty) {
		super.setGraphic(Optional.ofNullable(item)
				.map(Item::toString)
				.map(Label::new)
				.orElse(null));

		super.updateItem(item, empty);
	}

	@Override
	public void startEdit() {
		super.startEdit();

		ItemPane itemPane = new ItemPane(itemDurationRasterMinutes, lastDescriptions, getAvailableDurationRange(this.getItem()), this.getItem(), this::deleteItemAndFinishEdit);
		itemPane.setOnKeyReleased(handleKeyReleased(itemPane));
		super.setGraphic(itemPane);
	}

	private EventHandler<? super KeyEvent> handleKeyReleased(ItemPane itemPane) {
		return event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				this.cancelEdit();
				event.consume();
			} else if (event.getCode() == KeyCode.ENTER) {
				Item newItem = itemPane.get();
				if (validateDescription(newItem.getKey(), ItemListView.NEW_TRIGGER.equals(this.getItem()))) {
					super.commitEdit(itemPane.get());
				} else {
					super.cancelEdit();
				}
				event.consume();
			}
		};
	}

	private Duration getAvailableDurationRange(Item selectedItem) {
		Duration booked = super.getListView().getItems().stream()
				.filter(item -> !item.equals(selectedItem))
				.map(Item::getValue)
				.reduce((d1, d2) -> d1.plus(d2))
				.orElse(Duration.ZERO);

		return Optional.ofNullable(maxDurationRange.get())
				.map(d -> d.minus(booked))
				.orElse(Duration.ZERO);
	}

	private boolean validateDescription(String description, boolean newItem) {
		if (description.isEmpty() || (newItem && super.getListView().getItems().stream().map(Item::getKey).anyMatch(description::equals))) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Fehlerhafter Eintrag");
			alert.setContentText(String.format("Die Beschreibung %s.", (description.isEmpty()) ? "darf nicht leer sein" : "ist bereits vorhanden"));
			alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
			alert.showAndWait();
			return false;
		} else {
			return true;
		}
	}

	private void deleteItemAndFinishEdit(ActionEvent event) {
		Optional.ofNullable(super.getItem())
				.filter(this::shouldDelete)
				.ifPresent(item -> {
					super.getListView().getItems().remove(item);
					super.commitEdit(null);
				});
		event.consume();
	}

	private boolean shouldDelete(Item item) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText("Rückfrage zum Eintrag");
		alert.setContentText(String.format("Soll der Eintrag '%s' wirklich gelöscht werden?", item));
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		return alert.showAndWait()
				.filter(ButtonType.OK::equals)
				.isPresent();
	}

}
