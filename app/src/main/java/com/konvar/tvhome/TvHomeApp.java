package com.konvar.tvhome;

import android.app.Application;

/** Gives the key store its context before any screen or background job reads a key. */
public class TvHomeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!getSharedPreferences("trial", MODE_PRIVATE).getBoolean("initialized", false)) {
            RowPrefs.setStartRow(this, "Your apps");
            Player.set(this, "com.stremio.one");
            Trailers.choose(this, "org.smarttube.stable");
            getSharedPreferences("trial", MODE_PRIVATE).edit().putBoolean("initialized", true).apply();
        }
        Keys.app = this;
        Keys.tidy();
        Trakt.watchedIds = RowCache.loadIds(this, "watched");
        Trakt.loadSavedRatings(this);
    }
}
