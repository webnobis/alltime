package com.webnobis.alltime.export;

public enum PdfTableColumn {
	
	DAY("Datum", 3),
	WEEKDAY("Tag", 1),
	TYPE("Typ", 1),
	START_TIME("Start", 2),
	END_TIME("Ende", 2),
	REAL_TIME("Gesamt", 2),
	EXPECTED_TIME("Erwartet", 2),
	IDLE_TIME("Pause", 2),
	TIME_ASSETS("Guthaben", 3),
	ITEMS("Buchungen", 5);
	
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
