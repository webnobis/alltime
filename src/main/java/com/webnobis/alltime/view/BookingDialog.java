package com.webnobis.alltime.view;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.service.EntryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class BookingDialog extends Dialog<Entry> {
	
	private final EntryService service;
	
	private final ComboBox<EntryType> typeBox;

	public BookingDialog(EntryService service) {
		super();
		this.service = service;
		
		typeBox = new ComboBox<>(FXCollections.observableArrayList(EntryType.values()));
		GridPane pane = new GridPane();
		pane.add(typeBox, 0,0);
		
		super.getDialogPane().setContent(pane);
		super.show();
	}


}
