package com.webnobis.alltime;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VersionTest {

	private static final String VERSION = "0 8 15";

	@Test
	public void testGetVersion() {
		assertEquals(VERSION, Version.getVersion());
	}

}
