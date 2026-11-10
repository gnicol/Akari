package com.konvar.tvhome;

/** Shared monotonic cooldown across Trakt workers, so one 429 pauses subsequent API requests. */
final class TraktBackoff {
    private long retryAt;

    synchronized long remaining(long now) {
        return Math.max(0, retryAt - now);
    }

    synchronized void limited(String retryAfter, long now) {
        long delay = 60_000;
        try {
            double seconds = Double.parseDouble(retryAfter);
            if (Double.isFinite(seconds) && seconds > 0) {
                delay = (long) Math.ceil(seconds * 1000);
            }
        } catch (RuntimeException ignored) {
        }
        long until = delay > Long.MAX_VALUE - now ? Long.MAX_VALUE : now + delay;
        retryAt = Math.max(retryAt, until);
    }
}
