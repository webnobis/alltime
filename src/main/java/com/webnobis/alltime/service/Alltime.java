package com.webnobis.alltime.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import com.webnobis.alltime.config.Config;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.model.EntryType;
import com.webnobis.alltime.persistence.EntryStore;
import com.webnobis.alltime.persistence.FileEntryStore;
import com.webnobis.alltime.view.BookingDialog;

import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class Alltime extends Application {
	
	private static final String CONFIG_FILE = "config.properties";
	
	private EntryService service;

	public static void main(String[] args) {
		Application.launch(Alltime.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();
		
		Path configFile = Paths.get(this.getClass().getClassLoader().getResource(CONFIG_FILE).getPath());
		Config config = new Config(configFile);
		service = createService(config, createStore(config));
	}
	
	private static EntryStore createStore(Config config) {
		return new FileEntryStore(config.getFileStoreRootPath(), s -> new Entry(){

			@Override
			public LocalDate getDay() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EntryType getType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public LocalTime getStart() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Duration getTimeAssets() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Duration> getItems() {
				// TODO Auto-generated method stub
				return null;
			}}, e -> "");
	}

	private static EntryService createService(Config config, EntryStore store) {
		return new EntryService(() -> LocalTime.now(), 
				config.getMaxCountOfDays(), 
				config.getExpectedTimes(), 
				config.getIdleTimes(), 
				store);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
/*		Scene bookingScene = new Scene(null);
		Stage bookingStage = new Stage();
		bookingStage.setScene(bookingScene);
		bookingStage.centerOnScreen();
	*/	
		
		Dialog<Entry> bookingDialog = new BookingDialog(service);
		
	}

}
