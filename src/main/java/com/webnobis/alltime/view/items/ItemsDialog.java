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
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ItemsDialog extends Dialog<Map<String, Duration>> {

	private final Entry entry;

	private final int durationMinutesRaster;

	private final TextField day;

	private final ComboBox<Map.Entry<String, Duration>> items;

	private final ItemPane itemPane;

	public ItemsDialog(Entry entry, int durationMinutesRaster, List<String> lastDescriptions) {
		super();
		this.entry = Objects.requireNonNull(entry, "entry is null");
		this.durationMinutesRaster = durationMinutesRaster;

		day = new TextField(DayTransformer.toText(entry.getDay()));
		day.setEditable(false);

		items = new ComboBox<>(FXCollections.observableArrayList(Stream.concat(Stream.of(ItemPane.NEW_ITEM),
				entry.getItems().entrySet().stream()
						.map(Item::new))
				.sorted()
				.collect(Collectors.toList())));
		items.setConverter(new ItemStringConverter());
		items.setOnAction(this::setItem);

		itemPane = new ItemPane(this::addItem, this::changeItem, this::deleteItem, lastDescriptions);

		GridPane pane = new GridPane();
		pane.add(this.day, 0, 0);
		pane.add(this.items, 0, 1);
		pane.add(itemPane, 0, 2);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Einträge");

		super.setResultConverter(this::finish);
	}

	private List<Duration> getSelectableDurations() {
		Duration maxDuration = Stream.concat(Stream.of(entry.getIdleTime()),
				items.getItems().stream()
						.map(Map.Entry::getValue))
				.reduce((d1, d2) -> d1.plus(d2))
				.map(entry.getRealTime()::minus)
				.orElse(Duration.ZERO);

		List<Duration> durations = new ArrayList<>();
		Duration duration = Duration.ZERO;
		while (maxDuration.compareTo(duration) > 0) {
			duration = duration.plusMinutes(durationMinutesRaster);
			durations.add(duration);
		}
		Collections.reverse(durations);
		return durations;
	}

	private void setItem(ActionEvent event) {
		Optional.ofNullable(items.getValue())
				.ifPresent(item -> itemPane.setItem(item, getSelectableDurations()));
	}

	private void addItem(Map.Entry<String, Duration> item) {
		items.getItems().add(item);
	}

	private void changeItem(Map.Entry<String, Duration> item) {
		int index = items.getSelectionModel().selectedIndexProperty().intValue();
		items.getItems().set(index, item);
	}

	private void deleteItem(Map.Entry<String, Duration> item) {
		items.getItems().remove(item);
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
