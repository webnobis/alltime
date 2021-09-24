package com.webnobis.alltime;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Version
 * 
 * @author steffen
 *
 */
public abstract class Version {

	private static final String VERSION_FILE = "version.properties";

	private static final AtomicReference<String> versionRef = new AtomicReference<>();

	private Version() {
	}

	private static String readVersion() {
		try (BufferedInputStream in = new BufferedInputStream(ClassLoader.getSystemResourceAsStream(VERSION_FILE))) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Reads the version from 'version.properties'
	 * 
	 * @return version
	 */
	public static String getVersion() {
		return versionRef.updateAndGet(version -> Optional.ofNullable(version).orElseGet(Version::readVersion));
	}

}
