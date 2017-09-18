package com.webnobis.alltime.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.webnobis.alltime.config.Config;
import com.webnobis.alltime.persistence.EntryStore;
import com.webnobis.alltime.persistence.EntryToLineSerializer;
import com.webnobis.alltime.persistence.FileStore;
import com.webnobis.alltime.persistence.LineToDayDeserializer;
import com.webnobis.alltime.persistence.LineToEntryDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumSerializer;
import com.webnobis.alltime.view.AlltimeDialog;
import com.webnobis.alltime.view.TimeTransformer;

import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class Alltime extends Application {
	
	public static final String TITLE = Alltime.class.getSimpleName().concat(" V2.00 (Steffen Nobis)");

	private static final String CONFIG_FILE = "config.properties";

	private static final Supplier<LocalDateTime> now = LocalDateTime::now;

	private FindService findService;

	private BookingService bookingService;

	public static void main(String[] args) {
		Application.launch(Alltime.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();

		Path configFile = Paths.get(this.getClass().getClassLoader().getResource(CONFIG_FILE).getPath());
		Config config = new Config(configFile);
		EntryService service = createService(config, createStore(config));
		findService = service;
		bookingService = service;
	}

	private static EntryStore createStore(Config config) {
		return new FileStore(config.getFileStoreRootPath(), () -> now.get().toLocalDate(), config.getMaxCountOfDays(), config.getMaxCountOfDescriptions(),
				LineToDayDeserializer::toDay, LineToEntryDeserializer::toEntry, EntryToLineSerializer::toLine,
				TimeAssetsSumDeserializer::toTimeAssetsSum, TimeAssetsSumSerializer::toLine);
	}

	private static EntryService createService(Config config, EntryStore store) {
		return new EntryService(config.getExpectedTimes(), new IdleTimeHandler(config.getIdleTimes()), store);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Dialog<Void> dialog = new AlltimeDialog(now.get().toLocalDate(), findService, bookingService, new TimeTransformer(() -> now.get().toLocalTime(), 5, 10, 5), 15);
		dialog.showAndWait();
	}

}
