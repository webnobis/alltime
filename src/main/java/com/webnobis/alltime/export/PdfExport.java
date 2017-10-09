package com.webnobis.alltime.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.webnobis.alltime.model.Entry;
import com.webnobis.alltime.service.FindService;

public class PdfExport implements EntryExport {

	private static final String MONTH_FORMAT = "yyyyMM";

	private static final String FILE_EXT = ".pdf";

	private final Path root;

	private final Supplier<LocalDate> now;

	private final FindService findService;
	
	private final List<String> header;
	
	private final List<String> footer;

	public PdfExport(Path root, Supplier<LocalDate> now, FindService findService, List<String> header, List<String> footer) {
		this.root = root;
		this.now = now;
		this.findService = findService;
		this.header = header;
		this.footer = footer;

		if (!Files.exists(root)) {
			try {
				Files.createDirectories(root);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	@Override
	public List<Entry> exportRange(LocalDate fromDay, LocalDate untilDay) {
		Path pdfFile = root.resolve("test.pdf");
		
		PDDocument doc = new PDDocument(MemoryUsageSetting.setupMixed(Runtime.getRuntime().totalMemory() / 2));
		try {
			doc.save(pdfFile.toFile());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}
	
	private void addHeader(PDPage page) {
		//page.set
	}

}
