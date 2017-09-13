package com.webnobis.alltime.view.entry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.view.DayTransformer;
import com.webnobis.alltime.view.TimeTransformer;
import com.webnobis.alltime.view.ValueField;
import com.webnobis.alltime.view.items.Item;
import com.webnobis.alltime.view.items.ItemListView;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class EntryPane extends GridPane {

	private final EntryService service;

	private final ValueField<Duration> timeAssetsSum;

	private final TextField day;

	private final ValueField<LocalTime> startTime;

	private final ValueField<LocalTime> endTime;

	private final ComboBox<EntryType> type;

	private final ListView<Item> items;

	private final RadioButton startAZ;

	private final RadioButton endAZ;

	private final RadioButton bookDay;

	private final boolean disabledEndAZ;

	public EntryPane(EntryService service, TimeTransformer timeTransformer, int minutesRaster, LocalDate day) {
		super();
		this.service = service;

		Optional<Entry> entry = Optional.ofNullable(service.getEntry(day));

		TimeAssetsSum sum = service.getTimeAssetsSumBefore(day);
		timeAssetsSum = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, sum.getTimeAssetsSum());
		timeAssetsSum.setTooltip(new Tooltip(String.format("Stand: %s", DayTransformer.toText(sum.getDay()))));

		this.day = new TextField(DayTransformer.toText(day));

		LocalTime start = entry.map(Entry::getStart).orElse(timeTransformer.now(true));
		startTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, start);

		disabledEndAZ = !entry.map(Entry::getStart).isPresent();
		LocalTime end = entry.map(Entry::getEnd).orElse(timeTransformer.now(false));
		endTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, end);

		items = new ItemListView(minutesRaster, service.getLastDescriptions(),
				entry.map(e -> e.getRealTime().minus(e.getIdleTime())).orElse(Duration.ZERO),
				entry.map(Entry::getItems).orElse(Collections.emptyMap()));

		ToggleGroup group = new ToggleGroup();
		startAZ = new RadioButton("Start AZ");
		startAZ.setToggleGroup(group);
		endAZ = new RadioButton("Ende AZ");
		endAZ.setToggleGroup(group);
		bookDay = new RadioButton("Ganzen Tag buchen");
		bookDay.setToggleGroup(group);

		type = new ComboBox<>(FXCollections.observableArrayList(EnumSet.allOf(EntryType.class)));
		type.valueProperty().addListener((observable, oldValue, newValue) -> selectRadiobutton(newValue));
		type.valueProperty().addListener((observable, oldValue, newValue) -> enableElements(newValue));
		type.setValue(entry.map(Entry::getType).orElse(EntryType.AZ));

		GridPane buttonPane = new GridPane();
		buttonPane.add(startAZ, 0, 0);
		buttonPane.add(endAZ, 0, 1);
		buttonPane.add(bookDay, 0, 2);

		super.add(this.day, 0, 0);
		super.add(type, 1, 0);
		super.add(timeAssetsSum, 2, 0);
		super.add(startTime, 0, 1);
		super.add(endTime, 2, 1);
		super.add(items, 0, 2, 3, 1);
		super.add(buttonPane, 0, 3);
	}

	private void selectRadiobutton(EntryType newType) {
		if (EntryType.AZ.equals(newType)) {
			if (disabledEndAZ) {
				startAZ.setSelected(true);
			} else {
				endAZ.setSelected(true);
			}
		} else {
			bookDay.setSelected(true);
		}
	}

	private void enableElements(EntryType newType) {
		boolean az = EntryType.AZ.equals(newType);
		startTime.setDisable(!az);
		endTime.setDisable(!az || disabledEndAZ);
		items.setDisable(az && disabledEndAZ);
		startAZ.setDisable(!startAZ.isSelected());
		endAZ.setDisable(!endAZ.isSelected());
		bookDay.setDisable(!bookDay.isSelected());
	}

}
