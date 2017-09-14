package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.view.entry.EntryDialog;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class AlltimeDialog extends Dialog<Void> {

	private final EntryService service;

	private final TimeTransformer timeTransformer;

	private final int itemDurationRasterMinutes;

	private final ComboBox<LocalDate> days;

	public AlltimeDialog(LocalDate now, EntryService service, TimeTransformer timeTransformer, int itemDurationRasterMinutes) {
		super();
		this.service = service;
		this.timeTransformer = timeTransformer;
		this.itemDurationRasterMinutes = itemDurationRasterMinutes;

		days = new ComboBox<>(FXCollections.observableArrayList(getDaysWithNow(service.getLastDays(), now)));
		days.setConverter(new DayStringConverter());
		days.setValue(now);
		days.setOnAction(this::showEntryDialog);

		GridPane pane = new GridPane();
		pane.setHgap(5);
		pane.setVgap(5);
		pane.add(new Label("Verfügbare Tage:"), 0, 0);
		pane.add(days, 1, 0);

		showEntryDialog();

		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(pane);
		dialogPane.setHeaderText("Alltime");

		super.setResultConverter(button -> null);
	}

	private static List<LocalDate> getDaysWithNow(List<LocalDate> lastDays, LocalDate now) {
		if (lastDays.contains(now)) {
			return lastDays;
		}
		return Stream.concat(Stream.of(now), lastDays.stream()).collect(Collectors.toList());
	}

	private void showEntryDialog() {
		Optional.ofNullable(days.getValue())
				.ifPresent(day -> {
					Dialog<Entry> entryDialog = new EntryDialog(service, timeTransformer, itemDurationRasterMinutes, day);
					entryDialog.showAndWait().ifPresent(System.out::println);

					days.setValue(null);
				});
	}

	private void showEntryDialog(ActionEvent event) {
		showEntryDialog();
		event.consume();
	}

}
