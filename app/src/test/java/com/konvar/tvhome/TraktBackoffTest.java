package com.konvar.tvhome;

import org.junit.Test;
import static org.junit.Assert.*;

public class TraktBackoffTest {
    @Test public void honorsLongDelayAndDoesNotShortenExistingCooldown() {
        TraktBackoff limiter = new TraktBackoff();
        limiter.limited("300", 1000);
        assertEquals(300_000, limiter.remaining(1000));
        limiter.limited("1", 2000);
        assertEquals(299_000, limiter.remaining(2000));
        assertEquals(0, limiter.remaining(301_000));
    }

    @Test public void malformedHeaderUsesSafeDefaultAndFractionRoundsUp() {
        TraktBackoff limiter = new TraktBackoff();
        limiter.limited(null, 0);
        assertEquals(60_000, limiter.remaining(0));
        limiter.limited("NaN", 100_000);
        assertEquals(60_000, limiter.remaining(100_000));
        limiter.limited("0.1251", 200_000);
        assertEquals(126, limiter.remaining(200_000));
    }
}
