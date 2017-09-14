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
import com.webnobis.alltime.view.ViewStyle;
import com.webnobis.alltime.view.items.Item;
import com.webnobis.alltime.view.items.ItemListView;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class EntryPane extends GridPane {
	
	private static final int PREF_WIDTH = 90;

	private final EntryService service;

	private final ValueField<Duration> timeAssetsSum;

	private final TextField day;

	private final ValueField<LocalTime> startTime;

	private final ValueField<Duration> idleTime;

	private final ValueField<LocalTime> endTime;

	private final ComboBox<EntryType> type;

	private final ListView<Item> items;

	private final RadioButton startAZ;

	private final RadioButton endAZ;

	private final RadioButton bookDay;

	private final boolean disabledIdleTimeAndEndAZ;

	public EntryPane(EntryService service, TimeTransformer timeTransformer, int minutesRaster, LocalDate day) {
		super();
		this.service = service;

		Optional<Entry> entry = Optional.ofNullable(service.getEntry(day));
		disabledIdleTimeAndEndAZ = !entry.map(Entry::getStart).isPresent();
		
		TimeAssetsSum sum = service.getTimeAssetsSumBefore(day);
		timeAssetsSum = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, sum.getTimeAssetsSum());
		timeAssetsSum.setEditable(false);
		timeAssetsSum.setStyle(ViewStyle.READONLY + ViewStyle.BIG);
		timeAssetsSum.setTooltip(new Tooltip(String.format("Stand: %s", DayTransformer.toText(sum.getDay()))));
		timeAssetsSum.setPrefWidth(PREF_WIDTH * 2);
		timeAssetsSum.setAlignment(Pos.CENTER);
		
		this.day = new TextField(DayTransformer.toText(day));
		this.day.setEditable(false);
		this.day.setStyle(ViewStyle.READONLY);
		this.day.setPrefWidth(PREF_WIDTH);
		this.day.setAlignment(Pos.CENTER);
		
		LocalTime start = entry.map(Entry::getStart).orElse(timeTransformer.now(true));
		startTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, start);
		startTime.setPrefWidth(PREF_WIDTH);
		
		idleTime = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, entry.map(Entry::getIdleTime).orElse(Duration.ZERO));
		idleTime.setPrefWidth(PREF_WIDTH);

		LocalTime end = entry.map(Entry::getEnd).orElse(timeTransformer.now(false));
		endTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, end);
		endTime.setPrefWidth(PREF_WIDTH);

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
		type.setPrefWidth(PREF_WIDTH);

		GridPane buttonPane = new GridPane();
		buttonPane.add(startAZ, 0, 0);
		buttonPane.add(endAZ, 0, 1);
		buttonPane.add(bookDay, 0, 2);
		
		super.add(new Label("Zeitguthaben: "), 0, 0);
		super.add(timeAssetsSum, 1, 0, 3, 1);

		super.add(new Label("Buchungstag: "), 0, 1);
		super.add(this.day, 1, 1);
		super.add(new Label(" Buchungstyp: "), 2, 1);
		super.add(type, 3, 1);
		
		super.add(new Label("Start: "), 0, 2);
		super.add(startTime, 1, 2);
		super.add(new Label(" Pause: "), 2, 2);
		super.add(idleTime, 3, 2);

		super.add(new Label("Ende:  "), 0, 3);
		super.add(endTime, 1, 3);
		
		super.add(new Label("Einträge:"), 0, 4, 4, 1);
		super.add(items, 0, 5, 4, 1);
		super.add(buttonPane, 0, 6);
		
		super.setHgap(5);
		super.setVgap(5);
	}

	private void selectRadiobutton(EntryType newType) {
		if (EntryType.AZ.equals(newType)) {
			if (disabledIdleTimeAndEndAZ) {
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
		idleTime.setDisable(!az || disabledIdleTimeAndEndAZ);
		endTime.setDisable(!az || disabledIdleTimeAndEndAZ);
		items.setDisable(az && disabledIdleTimeAndEndAZ);
		startAZ.setDisable(!startAZ.isSelected());
		endAZ.setDisable(!endAZ.isSelected());
		bookDay.setDisable(!bookDay.isSelected());
	}

}
