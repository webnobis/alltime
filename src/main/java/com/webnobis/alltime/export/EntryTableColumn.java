package com.webnobis.alltime.export;

public enum EntryTableColumn {
	
	DAY("Datum", 25),
	WEEKDAY("Tag", 10),
	TYPE("Typ", 10),
	START_TIME("Start", 20),
	END_TIME("Ende", 20),
	REAL_TIME("Gesamt", 25),
	EXPECTED_TIME("Erwartet", 25),
	IDLE_TIME("Pause", 25),
	TIME_ASSETS("Guthaben", 25),
	ITEMS("Buchungen", 90);
	
	private final String header;
	
	private final float widthWeight;

	private EntryTableColumn(String header, int widthWeight) {
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
