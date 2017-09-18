package com.webnobis.alltime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public enum Version {

	VERSION;

	private static final String VERSION_FILE = "version.properties";

	private final String version;

	private Version() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(VERSION_FILE)))) {
			version = (in.ready()) ? in.readLine() : "unknown";
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static String getVersion() {
		return VERSION.version;
	}

}
