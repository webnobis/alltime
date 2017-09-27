package com.webnobis.alltime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AlltimeTest {

    private Alltime alltime;

    @Before
    public void setUp() {
        alltime = new Alltime();
    }

    @Test
    public void testInit() throws Exception {
        assertNull(alltime.findService);
        assertNull(alltime.calculationService);
        assertNull(alltime.bookingService);
        assertNull(alltime.timeTransformer);
        assertFalse(alltime.itemDurationRasterMinutes > 0);

        alltime.init();

        assertNotNull(alltime.findService);
        assertNotNull(alltime.calculationService);
        assertNotNull(alltime.bookingService);
        assertNotNull(alltime.timeTransformer);
        assertTrue(alltime.itemDurationRasterMinutes > 0);
    }

}
