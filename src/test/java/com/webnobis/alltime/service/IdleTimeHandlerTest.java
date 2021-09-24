package com.webnobis.alltime.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IdleTimeHandlerTest {

	private static final LocalDate DAY = LocalDate.of(1, 2, 3);

	private static final LocalTime START = LocalTime.of(0, 0);

	private static final Duration IDLE_TIME_LIMIT_1 = Duration.ofHours(6);

	private static final Duration IDLE_TIME_1 = Duration.ofMinutes(30);

	private static final Duration IDLE_TIME_LIMIT_2 = Duration.ofHours(9);

	private static final Duration IDLE_TIME_2 = Duration.ofMinutes(15);

	private static final Duration IDLE_TIME_LIMIT_3 = Duration.ofHours(10);

	private static final Duration IDLE_TIME_3 = Duration.ofMinutes(5);

	private static Map<Duration, Duration> idleTimes;

	private IdleTimeHandler handler;

	@BeforeAll
	static void setUpClass() throws Exception {
		idleTimes = new HashMap<>();
		idleTimes.put(IDLE_TIME_LIMIT_1, IDLE_TIME_1);
		idleTimes.put(IDLE_TIME_LIMIT_2, IDLE_TIME_2);
		idleTimes.put(IDLE_TIME_LIMIT_3, IDLE_TIME_3);
	}

	@BeforeEach
	void setUp() throws Exception {
		handler = new IdleTimeHandler(idleTimes);
	}

	@Test
	void testNoIdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_1.minusHours(1);
		assertEquals(Duration.ZERO, handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testLimit1IdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_2.minusHours(1);
		assertEquals(IDLE_TIME_1, handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testLimit1And2IdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_3.minusMinutes(1);
		assertEquals(IDLE_TIME_1.plus(IDLE_TIME_2),
				handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testLimit1And2And3IdleTime() {
		Duration realTime = IDLE_TIME_LIMIT_3.plusMinutes(45);
		assertEquals(IDLE_TIME_1.plus(IDLE_TIME_2).plus(IDLE_TIME_3),
				handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testPartOfLimit1IdleTime() {
		Duration expectedIdleTime = IDLE_TIME_1.minusMinutes(20);
		Duration realTime = IDLE_TIME_LIMIT_1.plus(expectedIdleTime);
		assertEquals(expectedIdleTime, handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testPartOfLimit3IdleTime() {
		Duration offset = Duration.ofMinutes(1);
		Duration expectedIdleTime = IDLE_TIME_1.plus(IDLE_TIME_2).plus(offset);
		Duration realTime = IDLE_TIME_LIMIT_3.plus(offset);
		assertEquals(expectedIdleTime, handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), null));
	}

	@Test
	void testSetIdleTimeLonger() {
		Duration realTime = IDLE_TIME_LIMIT_3.plusHours(2);
		Duration expectedIdleTime = Duration.ofDays(100);
		assertEquals(expectedIdleTime,
				handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), expectedIdleTime));
	}

	@Test
	void testSetIdleTimeShorter() {
		Duration realTime = IDLE_TIME_LIMIT_3.plusHours(2);
		Duration expectedIdleTime = IDLE_TIME_1.plus(IDLE_TIME_2).plus(IDLE_TIME_3);
		assertEquals(expectedIdleTime, handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()),
				expectedIdleTime.minusMinutes(1)));
		assertEquals(expectedIdleTime,
				handler.getIdleTime(DAY, START, START.plusMinutes(realTime.toMinutes()), Duration.ofHours(-1)));
	}

}
