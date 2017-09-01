package com.webnobis.alltime.view;

import java.time.LocalDate;
import java.time.LocalTime;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.EntryService;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class BookingDialog extends Dialog<Entry> {
	
	private final EntryService service;
	
	private final DayTransformer dayTransformer;
	
	private final TimeTransformer timeTransformer;
	
	private final ValueField<LocalDate> day;
	
	private final ValueField<LocalTime> startTime;
	
	private final ValueField<LocalTime> endTime;
	
	private final ComboBox<EntryType> typeBox;
	
	private final Button startAZ;
	
	private final Button endAZ;
	
	private final Button bookDay;

	public BookingDialog(EntryService service, DayTransformer dayTransformer, TimeTransformer timeTransformer) {
		super();
		this.service = service;
		this.dayTransformer = dayTransformer;
		this.timeTransformer = timeTransformer;
		
		day = new ValueField<>(DayTransformer::toDay, DayTransformer::toText, dayTransformer::nowToText);
		startTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, () -> timeTransformer.nowToText(true));
		endTime = new ValueField<>(TimeTransformer::toTime, TimeTransformer::toText, () -> timeTransformer.nowToText(false));
		typeBox = new ComboBox<>(FXCollections.observableArrayList(EntryType.values()));
		
		startAZ = new Button("Start AZ");
		startAZ.setOnAction(this::startAZ);

		endAZ = new Button("Ende AZ");
		bookDay = new Button("Tag buchen");

		GridPane pane = new GridPane();
		pane.add(day, 0,0);
		pane.add(startTime, 0,1);
		pane.add(endTime, 1,1);
		pane.add(typeBox, 0,2);
		pane.add(startAZ, 0,3);
		pane.add(endAZ, 1,3);
		pane.add(bookDay, 2,3);
		
		super.getDialogPane().setContent(pane);
		//super.onShowingProperty().addListener(observable -> observable.addListener(e -> e.));
		super.show();
	}

	private void startAZ(ActionEvent event) {
		super.setResult(service.startAZ(day.getValue(), startTime.getValue()));
		
		System.out.println(super.getResult());
	}

}
