package com.webnobis.alltime.export;

public enum PdfTableColumn {
	
	DAY("Datum", 30),
	WEEKDAY("Tag", 10),
	TYPE("Typ", 10),
	START_TIME("Start", 20),
	END_TIME("Ende", 20),
	REAL_TIME("Gesamt", 25),
	EXPECTED_TIME("Erwartet", 25),
	IDLE_TIME("Pause", 25),
	TIME_ASSETS("Guthaben", 30),
	ITEMS("Buchungen", 70);
	
	private final String header;
	
	private final float widthWeight;

	private PdfTableColumn(String header, int widthWeight) {
		this.header = header;
		this.widthWeight = widthWeight;
	}

	public String getHeader() {
		return header;
	}

	public float getWidthWeight() {
		return widthWeight;
	}

}
