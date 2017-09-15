package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.view.entry.EntryDialog;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AlltimeDialog extends Dialog<Void> {

	private static final String STORED = " gespeichert";

	private static final Comparator<LocalDate> dayComparator = (d1, d2) -> d1.compareTo(d2);

	private final EntryService service;

	private final TimeTransformer timeTransformer;

	private final int itemDurationRasterMinutes;

	private final ComboBox<LocalDate> days;

	private final TextField stored;

	public AlltimeDialog(LocalDate now, EntryService service, TimeTransformer timeTransformer, int itemDurationRasterMinutes) {
		super();
		this.service = service;
		this.timeTransformer = timeTransformer;
		this.itemDurationRasterMinutes = itemDurationRasterMinutes;

		days = new ComboBox<>(FXCollections.observableArrayList(getDaysUntilNow(service.getLastDays(), now)));
		days.setConverter(new DayStringConverter());
		days.setValue(now);
		days.setOnAction(this::showEntryDialog);

		stored = new TextField();
		stored.setPrefWidth(220);
		stored.setAlignment(Pos.CENTER);
		stored.setStyle(ViewStyle.READONLY);

		GridPane pane = new GridPane();
		pane.setHgap(5);
		pane.setVgap(5);
		pane.add(new Label("Verfügbare Tage:"), 0, 0);
		pane.add(days, 1, 0);
		pane.add(stored, 0, 1, 2, 1);

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Alltime");

		super.setResultConverter(button -> null);

		showEntryDialog(now);
	}

	private static List<LocalDate> getDaysUntilNow(List<LocalDate> lastDays, LocalDate now) {
		return lastDays.stream().min(dayComparator)
				.map(minDay -> ChronoUnit.DAYS.between(minDay, now))
				.map(count -> LongStream.rangeClosed(0, count)
						.mapToObj(now::minusDays)
						.collect(Collectors.toList()))
				.orElse(Collections.singletonList(now));
	}

	private void showEntryDialog(ActionEvent event) {
		event.consume();

		showEntryDialog(days.getValue());
	}

	private void showEntryDialog(LocalDate selectedDay) {
		System.out.println(selectedDay);
		stored.setVisible(false);
		Optional.ofNullable(selectedDay)
				.ifPresent(day -> {
					Dialog<Entry> entryDialog = new EntryDialog(service, timeTransformer, itemDurationRasterMinutes, day);
					entryDialog.showAndWait()
							.ifPresent(entry -> {
								stored.setText(DayTransformer.toText(day).concat(STORED));
								stored.setVisible(true);
							});
				});
	}

}
