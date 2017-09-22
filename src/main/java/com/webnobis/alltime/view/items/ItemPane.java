package com.webnobis.alltime.view.items;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemPane extends GridPane implements Supplier<Item> {

	private final ComboBox<Duration> duration;
	
	private final Button delete;

	private final ComboBox<String> lastDescriptions;

	private final TextField description;

	public ItemPane(int itemDurationRasterMinutes, List<String> lastDescriptions, Duration durationRange, Item item) {
		super();

		duration = new ComboBox<>(FXCollections.observableArrayList(getSelectableDurations(itemDurationRasterMinutes, durationRange)));
		duration.setConverter(new DurationStringConverter());
		Duration selectedDuration = (item != null) ? item.getValue() : null;
		duration.setValue((selectedDuration != null) ? selectedDuration : duration.getItems().stream().findFirst().orElse(null));

		delete = new Button("Löschen");
		
		description = new TextField((item != null) ? item.getKey() : "Beschreibung");

		this.lastDescriptions = new ComboBox<>(FXCollections.observableArrayList(lastDescriptions));
		this.lastDescriptions.setOnAction(this::setDescription);

		super.add(new Label("Zeitdauer:"), 0, 0);
		super.add(duration, 0, 1);
		super.add(new Label("Beschreibung:"), 1, 0);
		super.add(this.lastDescriptions, 1, 1);
		super.add(delete, 0, 2);
		super.add(description, 1, 2);

		super.setHgap(5);
		super.setVgap(5);
	}

	private void setDescription(ActionEvent event) {
		Optional.ofNullable(this.lastDescriptions.getValue()).ifPresent(description::setText);
		event.consume();
	}

	private List<Duration> getSelectableDurations(int rasterMinutes, Duration durationRange) {
		return Optional.ofNullable(durationRange)
				.filter(range -> !range.isNegative())
				.map(range -> range.toMinutes() / rasterMinutes)
				.map(count -> LongStream.rangeClosed(-count, 0)
						.map(Math::abs)
						.mapToObj(l -> Duration.ofMinutes(l * rasterMinutes))
						.collect(Collectors.toList()))
				.orElse(Collections.emptyList());
	}

	@Override
	public Item get() {
		return new Item(description.getText(), Optional.ofNullable(duration.getValue()).orElse(Duration.ZERO));
	}

}
