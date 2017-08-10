package com.webnobis.alltime.config;

import java.util.NoSuchElementException;
import java.util.Optional;

public enum IdleLimitKey {

	IDLE_LIMIT_KEY("idle.time.limit.");

	private final String key;

	private IdleLimitKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getDurationPart(String key) {
		return Optional.ofNullable(key)
				.filter(s -> s.startsWith(this.key))
				.map(s -> s.substring(this.key.length()))
				.orElseThrow(() -> new NoSuchElementException("no idle time limit key: " + key));
	}

}
