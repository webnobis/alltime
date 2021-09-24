package com.webnobis.alltime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AlltimeTest {

	private Alltime alltime;

	@BeforeEach
	void setUp() {
		alltime = new Alltime();
	}

	@Test
	void testInit() throws Exception {
		assertNull(alltime.findService);
		assertNull(alltime.calculationService);
		assertNull(alltime.bookingService);
		assertNull(alltime.entryExport);
		assertNull(alltime.timeTransformer);
		assertFalse(alltime.itemDurationRasterMinutes > 0);
		assertFalse(alltime.maxCountOfRangeBookingDays > 0);

		alltime.init();

		assertNotNull(alltime.findService);
		assertNotNull(alltime.calculationService);
		assertNotNull(alltime.bookingService);
		assertNotNull(alltime.entryExport);
		assertNotNull(alltime.timeTransformer);
		assertTrue(alltime.itemDurationRasterMinutes > 0);
		assertTrue(alltime.maxCountOfRangeBookingDays > 0);
	}

}
