package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import com.webnobis.alltime.config.Release;
import com.webnobis.alltime.export.EntryExport;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.CalculationService;
import com.webnobis.alltime.service.DayTransformer;
import com.webnobis.alltime.service.FindService;
import com.webnobis.alltime.service.TimeTransformer;
import com.webnobis.alltime.view.entry.EntryDialog;
import com.webnobis.alltime.view.entry.EntryRangeDialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AlltimeDialog extends Dialog<Void> {

	private static final int PREF_WIDTH = 120;

	private static final String DAYS_LABEL = "Verfügbare Tage:";

	private static final String FROM_DAYS_LABEL = "Zeitraum von:";

	private static final String STORED = "%s (%s) gespeichert";

	private static final String RANGE_STORED = "%dx %s (%s-%s) gespeichert";

	private static final String MONTH_EXPORTED = "Monat %s exportiert. Anzahl der Einträge: %d";

	private static final Comparator<LocalDate> dayComparator = (d1, d2) -> d1.compareTo(d2);

	private final LocalDate now;

	private final FindService findService;

	private final CalculationService calculationService;

	private final BookingService bookingService;

	private final EntryExport entryExport;

	private final TimeTransformer timeTransformer;

	private final int itemDurationRasterMinutes;

	private final int maxCountOfRangeBookingDays;

	private final Label fromDaysLabel;

	private final ComboBox<LocalDate> fromDays;

	private final CheckBox onlyUnfinishedDays;

	private final Label untilDaysLabel;

	private final ComboBox<LocalDate> untilDays;

	private final CheckBox bookRange;

	private final TextField storedText;

	public AlltimeDialog(LocalDate now, FindService findService, CalculationService calculationService,
			BookingService bookingService, EntryExport entryExport, TimeTransformer timeTransformer,
			int itemDurationRasterMinutes, int maxCountOfRangeBookingDays) {
		super();
		this.now = now;
		this.findService = findService;
		this.calculationService = calculationService;
		this.bookingService = bookingService;
		this.entryExport = entryExport;
		this.timeTransformer = timeTransformer;
		this.itemDurationRasterMinutes = itemDurationRasterMinutes;
		this.maxCountOfRangeBookingDays = maxCountOfRangeBookingDays;

		onlyUnfinishedDays = new CheckBox("nur unvollständige Tage");
		onlyUnfinishedDays.setOnAction(this::updateDays);

		fromDaysLabel = new Label(DAYS_LABEL);
		fromDaysLabel.setPrefWidth(PREF_WIDTH);

		ObservableList<LocalDate> daysUntilNow = getDaysUntilNow();
		fromDays = new ComboBox<>(daysUntilNow);
		fromDays.setConverter(new DayStringConverter());
		fromDays.setValue(now);
		fromDays.setOnAction(this::showEntryDialog);

		untilDaysLabel = new Label("bis:");
		untilDaysLabel.setPrefWidth(PREF_WIDTH);
		untilDaysLabel.setDisable(true);

		untilDays = new ComboBox<>(daysUntilNow);
		untilDays.setConverter(new DayStringConverter());
		untilDays.setOnAction(this::showEntryRangeDialog);
		untilDays.setDisable(true);

		bookRange = new CheckBox("ganzen Zeitraum buchen");
		bookRange.setOnAction(this::switchDialog);

		storedText = new TextField();
		storedText.setPrefWidth(300);
		storedText.setAlignment(Pos.CENTER);
		storedText.setStyle(ViewStyle.READONLY);

		GridPane pane = new GridPane();
		pane.setHgap(10);
		pane.setVgap(5);
		pane.add(fromDaysLabel, 0, 0);
		pane.add(fromDays, 1, 0);
		pane.add(onlyUnfinishedDays, 2, 0);
		pane.add(untilDaysLabel, 0, 1);
		pane.add(untilDays, 1, 1);
		pane.add(bookRange, 2, 1);
		pane.add(storedText, 0, 2, 3, 1);

		DialogPane dialogPane = super.getDialogPane();
		ExportMonthButton exportMonthButton = new ExportMonthButton(dialogPane, this::exportMonth);
		YearMonth month = YearMonth.from(now);
		exportMonthButton.addButton(month.minusMonths(1));
		exportMonthButton.addButton(month);
		dialogPane.getButtonTypes().add(ButtonType.CLOSE);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText(Release.HEADER.getValue());
		Stage stage = (Stage) dialogPane.getScene().getWindow();
		stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream("time.gif")));

		super.setTitle(Release.TITLE.getValue());
		super.setResultConverter(button -> null);

		showEntryDialog(now);
	}

	private ObservableList<LocalDate> getDaysUntilNow() {
		return FXCollections.observableArrayList(findService.getLastDays().stream().min(dayComparator)
				.map(minDay -> ChronoUnit.DAYS.between(minDay, now))
				.map(count -> LongStream.rangeClosed(0, count).mapToObj(now::minusDays)
						.filter(day -> !onlyUnfinishedDays.isSelected() || !isFinish(day)).toList())
				.orElse(Collections.singletonList(now)));
	}

	private ObservableList<LocalDate> getDaysAfterFirstDay(LocalDate firstDay) {
		return FXCollections.observableArrayList(LongStream.rangeClosed(1, maxCountOfRangeBookingDays)
				.mapToObj(firstDay::plusDays).toList());
	}

	private boolean isFinish(LocalDate day) {
		return Optional.ofNullable(findService.getEntry(day))
				.map(entry -> !EntryType.AZ.equals(entry.getType()) || entry.getEnd() != null).orElse(false);
	}

	private void updateDays(ActionEvent event) {
		fromDays.setItems(getDaysUntilNow());
	}

	private void updateUntilDays() {
		untilDays.setItems(getDaysAfterFirstDay(fromDays.getValue()));
	}

	private void showEntryDialog(ActionEvent event) {
		event.consume();

		if (bookRange.isSelected()) {
			updateUntilDays();
		} else {
			showEntryDialog(fromDays.getValue());
		}
	}

	private void showEntryDialog(LocalDate selectedDay) {
		storedText.setVisible(false);
		Optional.ofNullable(selectedDay).ifPresent(day -> {
			Dialog<Entry> entryDialog = new EntryDialog(calculationService, bookingService, timeTransformer,
					itemDurationRasterMinutes, day, findService.getTimeAssetsSumBefore(day),
					findService.getLastDescriptions(), Optional.ofNullable(findService.getEntry(day)));
			entryDialog.showAndWait().ifPresent(entry -> {
				storedText.setText(String.format(STORED, entry.getType().name(), DayTransformer.toText(day)));
				storedText.setVisible(true);
			});
		});
	}

	private void switchDialog(ActionEvent event) {
		event.consume();

		fromDaysLabel.setText((bookRange.isSelected()) ? FROM_DAYS_LABEL : DAYS_LABEL);
		untilDaysLabel.setDisable(!bookRange.isSelected());
		untilDays.setDisable(!bookRange.isSelected());

		if (bookRange.isSelected()) {
			updateUntilDays();
		}
	}

	private void showEntryRangeDialog(ActionEvent event) {
		event.consume();

		showEntryRangeDialog(fromDays.getValue(), untilDays.getValue());
	}

	private void showEntryRangeDialog(LocalDate selectedFromDay, LocalDate selectedUntilDay) {
		storedText.setVisible(false);
		Optional.ofNullable(selectedFromDay).ifPresent(
				fromDay -> Optional.ofNullable(selectedUntilDay).filter(fromDay::isBefore).ifPresent(untilDay -> {
					Dialog<List<Entry>> entryRangeDialog = new EntryRangeDialog(bookingService,
							itemDurationRasterMinutes, fromDay, untilDay, findService.getTimeAssetsSumBefore(fromDay),
							findService.getLastDescriptions(), Optional.ofNullable(findService.getEntry(fromDay)));
					entryRangeDialog.showAndWait().ifPresent(entries -> {
						storedText.setText(String.format(RANGE_STORED, entries.size(), entries.get(0).getType().name(),
								DayTransformer.toText(fromDay), DayTransformer.toText(untilDay)));
						storedText.setVisible(true);
					});
				}));
	}

	private void exportMonth(YearMonth month, String monthText) {
		List<Entry> entries = entryExport.exportMonth(month);
		storedText.setText(String.format(MONTH_EXPORTED, monthText, entries.size()));
		storedText.setVisible(true);
	}

}
