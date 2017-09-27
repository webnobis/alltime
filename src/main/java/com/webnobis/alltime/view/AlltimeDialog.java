package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import com.webnobis.alltime.Alltime;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.CalculationService;
import com.webnobis.alltime.service.FindService;
import com.webnobis.alltime.view.entry.EntryDialog;

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
import javafx.scene.layout.GridPane;

public class AlltimeDialog extends Dialog<Void> {

    private static final String STORED = "%s (%s) gespeichert";

    private static final Comparator<LocalDate> dayComparator = (d1, d2) -> d1.compareTo(d2);

    private final LocalDate now;

    private final FindService findService;

    private final CalculationService calculationService;

    private final BookingService bookingService;

    private final TimeTransformer timeTransformer;

    private final int itemDurationRasterMinutes;

    private final ComboBox<LocalDate> days;

    private final CheckBox onlyUnfinishedDays;

    private final TextField stored;

    public AlltimeDialog(LocalDate now, FindService findService, CalculationService calculationService, BookingService bookingService, TimeTransformer timeTransformer, int itemDurationRasterMinutes) {
        super();
        this.now = now;
        this.findService = findService;
        this.calculationService = calculationService;
        this.bookingService = bookingService;
        this.timeTransformer = timeTransformer;
        this.itemDurationRasterMinutes = itemDurationRasterMinutes;

        onlyUnfinishedDays = new CheckBox("nur unvollständige Tage");
        onlyUnfinishedDays.setOnAction(this::updateDays);

        days = new ComboBox<>(getDaysUntilNow());
        days.setConverter(new DayStringConverter());
        days.setValue(now);
        days.setOnAction(this::showEntryDialog);

        stored = new TextField();
        stored.setPrefWidth(300);
        stored.setAlignment(Pos.CENTER);
        stored.setStyle(ViewStyle.READONLY);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(5);
        pane.add(new Label("Verfügbare Tage:"), 0, 0);
        pane.add(days, 1, 0);
        pane.add(onlyUnfinishedDays, 2, 0);
        pane.add(stored, 0, 1, 3, 1);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
        dialogPane.setContent(pane);
        dialogPane.setHeaderText("Alltime");

        super.setTitle(Alltime.TITLE);
        super.setResultConverter(button -> null);

        showEntryDialog(now);
    }

    private ObservableList<LocalDate> getDaysUntilNow() {
        return FXCollections.observableArrayList(findService.getLastDays().stream().min(dayComparator)
                .map(minDay -> ChronoUnit.DAYS.between(minDay, now))
                .map(count -> LongStream.rangeClosed(0, count)
                        .mapToObj(now::minusDays)
                        .filter(day -> !onlyUnfinishedDays.isSelected() || !isFinish(day))
                        .collect(Collectors.toList()))
                .orElse(Collections.singletonList(now)));
    }

    private boolean isFinish(LocalDate day) {
        return Optional.ofNullable(findService.getEntry(day))
                .map(entry -> !EntryType.AZ.equals(entry.getType()) || entry.getEnd() != null)
                .orElse(false);
    }

    private void updateDays(ActionEvent event) {
        days.setItems(getDaysUntilNow());
    }

    private void showEntryDialog(ActionEvent event) {
        event.consume();

        showEntryDialog(days.getValue());
    }

    private void showEntryDialog(LocalDate selectedDay) {
        stored.setVisible(false);
        Optional.ofNullable(selectedDay)
                .ifPresent(day -> {
                    Dialog<Entry> entryDialog = new EntryDialog(calculationService, bookingService, timeTransformer, itemDurationRasterMinutes,
                            day, findService.getTimeAssetsSumBefore(day), findService.getLastDescriptions(), Optional.ofNullable(findService.getEntry(day)));
                    entryDialog.showAndWait()
                            .ifPresent(entry -> {
                                stored.setText(String.format(STORED, entry.getClass().getSimpleName(), DayTransformer.toText(day)));
                                stored.setVisible(true);
                            });
                });
    }

}
