package com.webnobis.alltime.config;

public enum Key {
	
	MAX_COUNT_OF_DAYS("max.count.of.days"),
	FILE_STORE_ROOT_PATH("file.store.root.path");

	private final String key;

	private Key(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}