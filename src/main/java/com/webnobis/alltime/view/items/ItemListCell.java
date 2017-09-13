package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;

public class ItemListCell extends ListCell<Item> {

	private final int minutesRaster;

	private final List<String> lastDescriptions;

	private final Duration durationRange;

	private ItemPane itemPane;

	public ItemListCell(int minutesRaster, List<String> lastDescriptions, Duration durationRange) {
		super();
		this.minutesRaster = minutesRaster;
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

		itemPane = new ItemPane(minutesRaster, lastDescriptions, durationRange, this.getItem());
		itemPane.setOnKeyReleased(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				this.cancelEdit();
				e.consume();
			} else if (e.getCode() == KeyCode.ENTER) {
				this.commitEdit(itemPane.getItem());
				e.consume();
			}
		});
		super.setGraphic(itemPane);
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
