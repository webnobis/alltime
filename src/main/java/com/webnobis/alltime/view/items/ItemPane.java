package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemPane extends GridPane implements Supplier<Item> {

	private final ComboBox<Duration> duration;

	private final ComboBox<String> lastDescriptions;

	private final TextField description;

	public ItemPane(int itemDurationRasterMinutes, List<String> lastDescriptions, Duration durationRange, Item item) {
		super();

		Duration selectedDuration = (item != null) ? item.getValue() : null;
		duration = new ComboBox<>(FXCollections.observableArrayList(getSelectableDurations(itemDurationRasterMinutes, durationRange, selectedDuration)));
		duration.setConverter(new DurationStringConverter());
		duration.setValue((selectedDuration != null) ? selectedDuration : duration.getItems().stream().findFirst().orElse(null));

		description = new TextField((item != null) ? item.getKey() : "Beschreibung");

		this.lastDescriptions = new ComboBox<>(FXCollections.observableArrayList(lastDescriptions));
		this.lastDescriptions.setOnAction(this::setDescription);

		super.add(new Label("Zeitdauer:"), 0, 0);
		super.add(duration, 0, 1);
		super.add(new Label("Beschreibung:"), 1, 0);
		super.add(this.lastDescriptions, 1, 1);
		super.add(description, 1, 2);

		super.setHgap(5);
		super.setVgap(5);
	}

	private void setDescription(ActionEvent event) {
		Optional.ofNullable(this.lastDescriptions.getValue()).ifPresent(description::setText);
		event.consume();
	}

	private List<Duration> getSelectableDurations(int rasterMinutes, Duration durationRange, Duration selectedDuration) {
		Duration maxDuration = Optional.ofNullable(durationRange)
				.map(range -> Optional.ofNullable(selectedDuration).filter(selected -> range.compareTo(selected) < 0).orElse(range))
				.orElse(Duration.ZERO);

		List<Duration> durations = new ArrayList<>();
		Duration duration = Duration.ofMinutes(rasterMinutes);
		while (maxDuration.compareTo(duration) >= 0) {
			durations.add(duration);
			duration = duration.plusMinutes(rasterMinutes);
		}
		Collections.reverse(durations);
		return durations;
	}

	@Override
	public Item get() {
		return new Item(description.getText(), Optional.ofNullable(duration.getValue()).orElse(Duration.ZERO));
	}

}
