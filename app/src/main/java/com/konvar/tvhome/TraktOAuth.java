package com.konvar.tvhome;

import org.json.JSONException;
import org.json.JSONObject;

/** Public TV client authentication. A client secret must not be shipped or sent by this app. */
final class TraktOAuth {
    static final String DEVICE_CODE_URL = "https://auth.trakt.tv/oauth/device/code";
    static final String DEVICE_TOKEN_URL = "https://auth.trakt.tv/oauth/device/token";
    static final String TOKEN_URL = "https://auth.trakt.tv/oauth/token";

    private TraktOAuth() {}

    static JSONObject deviceCode(String clientId) throws JSONException {
        return new JSONObject().put("client_id", clientId);
    }

    static JSONObject deviceToken(String clientId, String deviceCode) throws JSONException {
        return deviceCode(clientId).put("code", deviceCode);
    }

    static JSONObject refreshToken(String clientId, String refreshToken) throws JSONException {
        return deviceCode(clientId)
                .put("refresh_token", refreshToken)
                .put("grant_type", "refresh_token")
                .put("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
    }
}
