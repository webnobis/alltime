package com.webnobis.alltime.view.entry;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.config.Release;
import com.webnobis.alltime.model.CalculationType;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.model.TimeAssetsSum;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.CalculationService;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.DurationFormatter;
import com.webnobis.alltime.service.TimeTransformer;
import com.webnobis.alltime.view.ValueField;
import com.webnobis.alltime.view.ViewStyle;
import com.webnobis.alltime.view.items.Item;
import com.webnobis.alltime.view.items.ItemListView;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class EntryDialog extends Dialog<Entry> {

	private static final int PREF_WIDTH = 90;

	private final CalculationService calculationService;

	private final BookingService bookingService;

	private final Duration sumBeforeDay;

	private final ValueField<Duration> timeAssetsSum;

	private final ValueField<LocalDate> day;

	private final ValueField<LocalTime> startTime;

	private final ValueField<Duration> idleTime;

	private final ValueField<LocalTime> endTime;

	private final ValueField<Duration> bookableTime;

	private final ComboBox<EntryType> type;

	private final ListView<Item> items;

	private final RadioButton startAZ;

	private final RadioButton endAZ;

	private final RadioButton bookDay;

	private final boolean disabledIdleTimeAndEndAZ;

	public EntryDialog(CalculationService calculationService, BookingService bookingService,
			TimeTransformer timeTransformer, int itemDurationRasterMinutes, LocalDate day, TimeAssetsSum sum,
			List<String> lastDescriptions, Optional<Entry> entry) {
		super();
		this.calculationService = calculationService;
		this.bookingService = bookingService;
		sumBeforeDay = sum.timeAssetsSum();

		disabledIdleTimeAndEndAZ = !entry.map(Entry::getStart).isPresent();

		timeAssetsSum = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, sumBeforeDay);
		timeAssetsSum.setEditable(false);
		timeAssetsSum.setStyle(ViewStyle.READONLY + ViewStyle.BIG);
		timeAssetsSum.setPrefWidth(PREF_WIDTH * 2.0);
		timeAssetsSum.setAlignment(Pos.CENTER);
		setTimeAssetsSumTooltip(sum.day());

		this.day = new ValueField<>(DayTransformer::toDay, DayTransformer::toText, day);
		this.day.setEditable(false);
		this.day.setStyle(ViewStyle.READONLY);
		this.day.setPrefWidth(PREF_WIDTH);
		this.day.setAlignment(Pos.CENTER);

		LocalTime start = entry.map(Entry::getStart).orElse(timeTransformer.now(true));
		startTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, start);
		startTime.setPrefWidth(PREF_WIDTH);

		idleTime = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString,
				entry.map(Entry::getIdleTime).orElse(Duration.ZERO));
		idleTime.setPrefWidth(PREF_WIDTH);

		LocalTime end = entry.map(Entry::getEnd).orElse(timeTransformer.now(false));
		endTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, end);
		endTime.setPrefWidth(PREF_WIDTH);

		bookableTime = new ValueField<>(DurationFormatter::toDuration, DurationFormatter::toString, Duration.ZERO);
		bookableTime.setPrefWidth(PREF_WIDTH);
		bookableTime.setEditable(false);
		bookableTime.setStyle(ViewStyle.READONLY);

		items = new ItemListView(itemDurationRasterMinutes, lastDescriptions, bookableTime::getValue,
				entry.map(Entry::getItems).orElse(Collections.emptyMap()));

		ToggleGroup group = new ToggleGroup();
		startAZ = new RadioButton("Start AZ");
		startAZ.setToggleGroup(group);
		endAZ = new RadioButton("Ende AZ");
		endAZ.setToggleGroup(group);
		bookDay = new RadioButton("Ganzen Tag buchen");
		bookDay.setToggleGroup(group);

		type = new ComboBox<>(FXCollections.observableArrayList(EnumSet.allOf(EntryType.class)));
		type.setPrefWidth(PREF_WIDTH);

		startTime.focusedProperty()
				.addListener((observable, oldFocus, newFocus) -> updateBookableTime(oldFocus, newFocus));
		idleTime.focusedProperty()
				.addListener((observable, oldFocus, newFocus) -> updateBookableTime(oldFocus, newFocus));
		endTime.focusedProperty()
				.addListener((observable, oldFocus, newFocus) -> updateBookableTime(oldFocus, newFocus));

		type.valueProperty().addListener((observable, oldValue, newValue) -> selectRadiobutton(newValue));
		type.valueProperty().addListener((observable, oldValue, newValue) -> updateFields());
		type.valueProperty().addListener((observable, oldValue, newValue) -> enableElements(newValue));
		type.setValue(entry.map(Entry::getType).orElse(getDefaultType(DayOfWeek.from(day))));

		GridPane buttonPane = new GridPane();
		buttonPane.add(startAZ, 0, 0);
		buttonPane.add(endAZ, 0, 1);
		buttonPane.add(bookDay, 0, 2);

		GridPane pane = new GridPane();
		pane.add(new Label("Zeitguthaben:"), 0, 0);
		pane.add(timeAssetsSum, 1, 0, 3, 1);

		pane.add(new Label("Buchungstag:"), 0, 1);
		pane.add(this.day, 1, 1);
		pane.add(new Label("Buchungstyp:"), 2, 1);
		pane.add(type, 3, 1);

		pane.add(new Label("Start:"), 0, 2);
		pane.add(startTime, 1, 2);
		pane.add(new Label("Pause:"), 2, 2);
		pane.add(idleTime, 3, 2);

		pane.add(new Label("Ende:"), 0, 3);
		pane.add(endTime, 1, 3);
		pane.add(new Label("Buchbare Zeit:"), 2, 3);
		pane.add(bookableTime, 3, 3);

		pane.add(new Label("Einträge:"), 0, 4, 4, 1);
		pane.add(items, 0, 5, 4, 1);
		pane.add(buttonPane, 0, 6, 4, 1);

		pane.setHgap(5);
		pane.setVgap(5);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Buchung");

		super.setTitle(Release.TITLE.getValue());
		super.setResultConverter(this::get);
	}

	private static EntryType getDefaultType(DayOfWeek weekday) {
		if (Stream.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).anyMatch(weekday::equals)) {
			return EntryType.WE;
		}else {
			return EntryType.AZ;
		}
	}

	private void setTimeAssetsSumTooltip(LocalDate day) {
		timeAssetsSum.setTooltip(new Tooltip(String.format("Stand: %s", DayTransformer.toText(day))));
	}

	private void updateBookableTime(boolean oldFocus, boolean newFocus) {
		if (oldFocus && !newFocus) {
			updateFields();
		}
	}

	private void updateFields() {
		Map<CalculationType, Duration> calculations = calculationService.calculate(day.getValue(), type.getValue(),
				startTime.getValue(), endTime.getValue(), idleTime.getValue());
		idleTime.setValue(calculations.get(CalculationType.IDLE_TIME));
		bookableTime.setValue(calculations.get(CalculationType.REAL_TIME).minus(idleTime.getValue()));
		timeAssetsSum.setValue(sumBeforeDay.plus(calculations.get(CalculationType.TIME_ASSETS)));
		setTimeAssetsSumTooltip(day.getValue());
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
		bookableTime.setDisable(!az);
		items.setDisable(az && disabledIdleTimeAndEndAZ);
		startAZ.setDisable(!startAZ.isSelected());
		endAZ.setDisable(!endAZ.isSelected());
		bookDay.setDisable(!bookDay.isSelected());
	}

	private Entry get(ButtonType button) {
		if (Optional.ofNullable(button).filter(ButtonType.APPLY::equals).isPresent()) {
			if (startAZ.isSelected()) {
				return bookingService.startAZ(day.getValue(), startTime.getValue());
			} else {
				Map<String, Duration> itemMap = this.items.getItems().stream()
						.filter(item -> !ItemListView.NEW_TRIGGER.equals(item))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				if (endAZ.isSelected()) {
					return bookingService.endAZ(day.getValue(), startTime.getValue(), endTime.getValue(),
							idleTime.getValue(), itemMap);
				} else {
					return bookingService.book(day.getValue(), type.getValue(), itemMap);
				}
			}
		}
		return null;
	}

}
