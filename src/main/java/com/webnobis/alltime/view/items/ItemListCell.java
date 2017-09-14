package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;

public class ItemListCell extends ListCell<Item> {

	private final int itemDurationRasterMinutes;

	private final List<String> lastDescriptions;

	private final Duration durationRange;

	private ItemPane itemPane;

	public ItemListCell(int itemDurationRasterMinutes, List<String> lastDescriptions, Duration durationRange) {
		super();
		this.itemDurationRasterMinutes = itemDurationRasterMinutes;
		this.lastDescriptions = lastDescriptions;
		this.durationRange = durationRange;
	}

	@Override
	protected void updateItem(Item item, boolean empty) {
		super.updateItem(item, empty);

		if (item != null) {
			super.setGraphic(new Label(item.toString()));
		}
	}

	@Override
	public void startEdit() {
		super.startEdit();

		itemPane = new ItemPane(itemDurationRasterMinutes, lastDescriptions, getAvailableDurationRange(), this.getItem());
		itemPane.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				this.cancelEdit();
				e.consume();
			} else if (e.getCode() == KeyCode.ENTER) {
				Item newItem = itemPane.get();
				if (validateDescription(newItem.getKey())) {
					this.commitEdit(itemPane.get());
				} else {
					this.cancelEdit();
				}
				e.consume();
			}
		});
		super.setGraphic(itemPane);
	}

	private Duration getAvailableDurationRange() {
		return durationRange.minus(super.getListView().getItems().stream()
				.map(Item::getValue)
				.reduce((d1, d2) -> d1.plus(d2))
				.orElse(Duration.ZERO));
	}

	private boolean validateDescription(String description) {
		if (description.isEmpty() || super.getListView().getItems().stream().map(Item::getKey).anyMatch(description::equals)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Fehlerhafter Eintrag");
			alert.setContentText(String.format("Die Beschreibung %s.", (description.isEmpty()) ? "darf nicht leer sein" : "ist bereits vorhanden"));
			alert.showAndWait();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void commitEdit(Item newItem) {
		super.commitEdit(newItem);

		Optional.ofNullable(newItem)
				.ifPresent(item -> super.setGraphic(new Label(item.toString())));
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		itemPane = null;
		super.setGraphic(new Label(super.getItem().toString()));
	}

}
