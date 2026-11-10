package com.konvar.tvhome;

import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Bound next-episode lookups without modifying the user's history. */
final class TraktHistory {
    private TraktHistory() {}

    static List<JSONObject> recent(List<JSONObject> shows, int limit) {
        List<JSONObject> sorted = new ArrayList<>(shows);
        sorted.sort((a, b) -> Long.compare(watchedAt(b), watchedAt(a)));
        return new ArrayList<>(sorted.subList(0, Math.min(Math.max(0, limit), sorted.size())));
    }

    private static long watchedAt(JSONObject show) {
        try {
            return Instant.parse(show.optString("last_watched_at", "")).toEpochMilli();
        } catch (RuntimeException e) {
            return Long.MIN_VALUE;
        }
    }
}
