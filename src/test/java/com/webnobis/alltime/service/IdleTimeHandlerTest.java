package com.webnobis.alltime.service;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IdleTimeHandlerTest {

	private static final Duration IDLE_TIME_LIMIT_1 = Duration.ofHours(6);

	private static final Duration IDLE_TIME_1 = Duration.ofMinutes(30);

	private static final Duration IDLE_TIME_LIMIT_2 = Duration.ofHours(9);

	private static final Duration IDLE_TIME_2 = Duration.ofMinutes(15);

	private static final Duration IDLE_TIME_LIMIT_3 = Duration.ofHours(10);

	private static final Duration IDLE_TIME_3 = Duration.ofMinutes(5);

	private static Map<Duration, Duration> idleTimes;

	private IdleTimeHandler handler;

	@BeforeClass
	public static void setUpClass() throws Exception {
		idleTimes = new HashMap<>();
		idleTimes.put(IDLE_TIME_LIMIT_1, IDLE_TIME_1);
		idleTimes.put(IDLE_TIME_LIMIT_2, IDLE_TIME_2);
		idleTimes.put(IDLE_TIME_LIMIT_3, IDLE_TIME_3);
	}

	@Before
	public void setUp() throws Exception {
		handler = new IdleTimeHandler(idleTimes);
	}

	@Test
	public void testNoIdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_1.minusHours(1);
		assertEquals(Duration.ZERO, handler.getIdleTime(realTime));
	}

	@Test
	public void testLimit1IdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_2.minusHours(1);
		assertEquals(IDLE_TIME_1, handler.getIdleTime(realTime));
	}

	@Test
	public void testLimit1And2IdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_3.minusMinutes(1);
		assertEquals(IDLE_TIME_1.plus(IDLE_TIME_2), handler.getIdleTime(realTime));
	}

	@Test
	public void testPartOfLimit1IdleTime() {
		Duration expectedIdleTime = IDLE_TIME_1.minusMinutes(20);
		Duration realTime = IDLE_TIME_LIMIT_1.plus(expectedIdleTime);
		assertEquals(expectedIdleTime, handler.getIdleTime(realTime));
	}

	@Test
	public void testPartOfLimit3IdleTime() {
		Duration offset = Duration.ofMinutes(1);
		Duration expectedIdleTime = IDLE_TIME_1.plus(IDLE_TIME_2).plus(offset);
		Duration realTime = IDLE_TIME_LIMIT_3.plus(offset);
		assertEquals(expectedIdleTime, handler.getIdleTime(realTime));
	}

}
