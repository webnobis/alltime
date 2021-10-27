package com.webnobis.alltime;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import com.webnobis.alltime.config.Config;
import com.webnobis.alltime.config.Release;
import com.webnobis.alltime.export.EntryExport;
import com.webnobis.alltime.export.PdfExport;
import com.webnobis.alltime.persistence.EntryStore;
import com.webnobis.alltime.persistence.EntryToLineSerializer;
import com.webnobis.alltime.persistence.FileStore;
import com.webnobis.alltime.persistence.LineToDayDeserializer;
import com.webnobis.alltime.persistence.LineToEntryDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumDeserializer;
import com.webnobis.alltime.persistence.TimeAssetsSumSerializer;
import com.webnobis.alltime.service.BookingService;
import com.webnobis.alltime.service.CalculationService;
import com.webnobis.alltime.service.EntryService;
import com.webnobis.alltime.service.FindService;
import com.webnobis.alltime.service.IdleTimeHandler;
import com.webnobis.alltime.service.TimeTransformer;
import com.webnobis.alltime.view.AlltimeDialog;

import javafx.application.Application;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;

public class Alltime extends Application {

	private static final Supplier<LocalDateTime> now = LocalDateTime::now;

	FindService findService;

	CalculationService calculationService;

	BookingService bookingService;

	EntryExport entryExport;

	TimeTransformer timeTransformer;

	int itemDurationRasterMinutes;

	int maxCountOfRangeBookingDays;

	public static void main(String[] args) {
		Application.launch(Alltime.class, args);
	}

	@Override
	public void init() throws Exception {
		super.init();

		Config config = new Config(ClassLoader.getSystemResourceAsStream(Release.CONFIG.getValue()));
		EntryService service = createService(config, createStore(config));
		findService = service;
		calculationService = service;
		bookingService = service;
		entryExport = new PdfExport(config.getFileExportRootPath(), findService);
		timeTransformer = new TimeTransformer(() -> now.get().toLocalTime(), config.getTimeStartOffsetMinutes(),
				config.getTimeEndOffsetMinutes(), config.getTimeRasterMinutes());
		itemDurationRasterMinutes = config.getItemDurationRasterMinutes();
		maxCountOfRangeBookingDays = config.getItemDurationRasterMinutes();
	}

	private static EntryStore createStore(Config config) {
		return new FileStore(config.getFileStoreRootPath(), () -> now.get().toLocalDate(), config.getMaxCountOfDays(),
				config.getMaxCountOfDescriptions(), LineToDayDeserializer::toDay, LineToEntryDeserializer::toEntry,
				EntryToLineSerializer::toLine, TimeAssetsSumDeserializer::toTimeAssetsSum,
				TimeAssetsSumSerializer::toLine);
	}

	private static EntryService createService(Config config, EntryStore store) {
		return new EntryService(config.getExpectedTimes(), new IdleTimeHandler(config.getIdleTimes()), store);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Dialog<Void> dialog = new AlltimeDialog(now.get().toLocalDate(), findService, calculationService,
				bookingService, entryExport, timeTransformer, itemDurationRasterMinutes, maxCountOfRangeBookingDays);
		dialog.showAndWait();
	}

}
