package com.webnobis.alltime.export;

public enum TimeAssetsSumTableRow {

	SUM_BEFORE("Bisheriges Guthaben (Stand %s):"), 
	DIFFERENCE("Gesamtguthaben des Reports:"), 
	SUM_NEW("Neues Guthaben (Stand %s):");

	public static final float[] WEIDTH_WEIGHTS = { 120, 30 };

	private final String description;

	private TimeAssetsSumTableRow(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
