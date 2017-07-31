package com.webnobis.alltime.model;

public enum EntryType {
	
	AZ("Arbeitszeit", true),
	GT("Gleittag", true),
	UR("Urlaub", false),
	KR("krank", false),
	SM("Seminar", false),
	SO("Sondertag", false),
	WE("Wochenende", false);
	
	private final String description;
	
	private final boolean toCalculate;

	private EntryType(String description, boolean toCalculate) {
		this.description = description;
		this.toCalculate = toCalculate;
	}

	public String getDescription() {
		return description;
	}

	public boolean isToCalculate() {
		return toCalculate;
	}
	
}
