package com.webnobis.alltime.view;

import java.time.LocalDate;

import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.view.entry.EntryPane;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class AlltimeDialog extends Dialog<Void> {
	
	private final EntryService service;
	
	private final TimeTransformer timeTransformer;
	
	private final int minutesRaster;
	
	public AlltimeDialog(EntryService service, TimeTransformer timeTransformer, int minutesRaster) {
		super();
		this.service = service;
		this.timeTransformer = timeTransformer;
		this.minutesRaster = minutesRaster;
		
		DialogPane dialogPane = super.getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(new EntryPane(service, timeTransformer, minutesRaster, LocalDate.now()));
		dialogPane.setHeaderText("Buchungen");
	}

}
