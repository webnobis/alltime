package com.webnobis.alltime.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public enum Release {

	VERSION, AUTHOR, TITLE, HEADER, CONFIG;

	private static final String RELEASE_FILE = "release.properties";

	private static final AtomicReference<Properties> releaseRef = new AtomicReference<>();

	private static Properties readRelease() {
		Properties properties = new Properties();
		try (InputStream in = ClassLoader.getSystemResourceAsStream(RELEASE_FILE)) {
			properties.load(in);
			return properties;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Reads the version from 'version.properties'
	 * 
	 * @return version
	 */
	public String getValue() {
		return releaseRef.updateAndGet(version -> Optional.ofNullable(version).orElseGet(Release::readRelease))
				.getProperty(name().toLowerCase());
	}

}
