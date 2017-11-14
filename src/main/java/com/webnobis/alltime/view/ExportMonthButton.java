package com.webnobis.alltime.view;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.BiConsumer;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class ExportMonthButton {

	private static final String MONTH_FORMAT = "MMM";

	private static final String EXPORT_BUTTON_LABEL = "Export %s";

	private final DialogPane dialogPane;

	private final BiConsumer<YearMonth, String> actionConsumer;

	public ExportMonthButton(DialogPane dialogPane, BiConsumer<YearMonth, String> actionConsumer) {
		this.dialogPane = dialogPane;
		this.actionConsumer = actionConsumer;
	}

	public void addButton(YearMonth month) {
		String monthText = Objects.requireNonNull(month, "month is null").format(DateTimeFormatter.ofPattern(MONTH_FORMAT));
		ButtonType buttonType = new ButtonType(String.format(EXPORT_BUTTON_LABEL, monthText));
		dialogPane.getButtonTypes().add(buttonType);
		Node button = dialogPane.lookupButton(buttonType);
		button.addEventFilter(ActionEvent.ACTION, event -> export(event, month, monthText)); // avoid dialog close on consume
	}

	private void export(ActionEvent event, YearMonth month, String monthText) {
		event.consume();
		actionConsumer.accept(month, monthText);
	}

}
