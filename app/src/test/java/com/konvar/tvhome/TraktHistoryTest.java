package com.konvar.tvhome;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TraktHistoryTest {
    @Test public void selectsRecentShowsRegardlessOfApiOrderWithoutChangingInput() throws Exception {
        JSONObject old = new JSONObject().put("last_watched_at", "2020-01-01T00:00:00Z");
        JSONObject recent = new JSONObject().put("last_watched_at", "2026-11-10T00:00:00.000Z");
        JSONObject middle = new JSONObject().put("last_watched_at", "2025-01-01T00:00:00Z");
        List<JSONObject> input = Arrays.asList(old, recent, middle);
        assertEquals(Arrays.asList(recent, middle), TraktHistory.recent(input, 2));
        assertEquals(Arrays.asList(old, recent, middle), input);
    }

    @Test public void boundsLargeHistoryAndLeavesUnknownDatesLast() throws Exception {
        List<JSONObject> input = new ArrayList<>();
        JSONObject unknown = new JSONObject();
        input.add(unknown);
        for (int i = 0; i < 883; i++) {
            input.add(new JSONObject().put("last_watched_at", "2026-11-10T00:00:00Z"));
        }
        List<JSONObject> result = TraktHistory.recent(input, 40);
        assertEquals(40, result.size());
        assertFalse(result.contains(unknown));
        assertEquals(884, input.size());
        assertTrue(TraktHistory.recent(input, 0).isEmpty());
    }
}
