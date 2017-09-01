package com.webnobis.alltime.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.webnobis.alltime.config.Config;
import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.persistence.EntryStore;
import com.webnobis.alltime.persistence.EntryToLineSerializer;
import com.webnobis.alltime.persistence.FileStore;
import com.webnobis.alltime.persistence.LineToDayDeserializer;
import com.webnobis.alltime.persistence.LineToEntryDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumSerializer;
import com.webnobis.alltime.view.BookingDialog;
import com.webnobis.alltime.view.DayTransformer;
import com.webnobis.alltime.view.TimeTransformer;

import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class Alltime extends Application {

	private static final String CONFIG_FILE = "config.properties";

	private static final Supplier<LocalDateTime> now = LocalDateTime::now;

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
		return new FileStore(config.getFileStoreRootPath(), () -> now.get().toLocalDate(), config.getMaxCountOfDays(),
				LineToDayDeserializer::toDay, LineToEntryDeserializer::toEntry, EntryToLineSerializer::toLine,
				TimeAssetsSumDeserializer::toTimeAssetsSum, TimeAssetsSumSerializer::toLine);
	}

	private static EntryService createService(Config config, EntryStore store) {
		return new EntryService(config.getExpectedTimes(), new IdleTimeHandler(config.getIdleTimes()), store);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		/*
		 * Scene bookingScene = new Scene(null); Stage bookingStage = new
		 * Stage(); bookingStage.setScene(bookingScene);
		 * bookingStage.centerOnScreen();
		 */

		Dialog<Entry> bookingDialog = new BookingDialog(service, new DayTransformer(() -> now.get().toLocalDate()), new TimeTransformer(() -> now.get().toLocalTime(), 0, 0, 5));

	}

}
