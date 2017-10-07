package com.webnobis.alltime.model;

public enum EntryType {
	
	AZ("Arbeitszeit"),
	GT("Gleittag"),
	UR("Urlaub"),
	KR("krank"),
	SM("Seminar"),
	SO("Sonstiges"),
	WE("Wochenende");
	
	private final String description;

	private EntryType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
