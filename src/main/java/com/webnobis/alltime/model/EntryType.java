package com.webnobis.alltime.model;

public enum EntryType {
	
	AZ("Arbeitszeit"),
	WE("Wochenende"),
    UR("Urlaub"),
	KR("krank"),
	GT("Gleittag"),
	FT("Feiertag"),
	SM("Seminar"),
	SO("Sonstiges");
	
	private final String description;

	private EntryType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
