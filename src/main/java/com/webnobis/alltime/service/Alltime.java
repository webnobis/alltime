package com.webnobis.alltime.service;

import com.webnobis.alltime.view.BookingDialog;

import javafx.application.Application;
import javafx.stage.Stage;

public class Alltime extends Application {

	public static void main(String[] args) {
		Application.launch(Alltime.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new BookingDialog(null);
		
	}

}
