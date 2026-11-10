package com.konvar.tvhome;

import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class TraktOAuthTest {
    @Test public void deviceCodeUsesOnlyPublicClientId() throws Exception {
        JSONObject body = TraktOAuth.deviceCode("public-test-id");
        assertEquals("public-test-id", body.getString("client_id"));
        assertEquals(1, body.length());
    }

    @Test public void deviceExchangeHasCodeAndNoSecret() throws Exception {
        JSONObject body = TraktOAuth.deviceToken("public-test-id", "device-test-code");
        assertEquals("public-test-id", body.getString("client_id"));
        assertEquals("device-test-code", body.getString("code"));
        assertFalse(body.has("client_secret"));
        assertEquals(2, body.length());
    }

    @Test public void renewalUsesRefreshGrantWithoutSecret() throws Exception {
        JSONObject body = TraktOAuth.refreshToken("public-test-id", "test-refresh-token");
        assertEquals("public-test-id", body.getString("client_id"));
        assertEquals("test-refresh-token", body.getString("refresh_token"));
        assertEquals("refresh_token", body.getString("grant_type"));
        assertEquals("urn:ietf:wg:oauth:2.0:oob", body.getString("redirect_uri"));
        assertFalse(body.has("client_secret"));
    }

    @Test public void credentialsGoOnlyToHttpsTraktAuthHost() throws Exception {
        for (String url : new String[]{TraktOAuth.DEVICE_CODE_URL, TraktOAuth.DEVICE_TOKEN_URL,
                TraktOAuth.TOKEN_URL}) {
            URI uri = new URI(url);
            assertEquals("https", uri.getScheme());
            assertEquals("auth.trakt.tv", uri.getHost());
            assertNull(uri.getUserInfo());
            assertNull(uri.getQuery());
        }
        assertEquals("/oauth/device/code", new URI(TraktOAuth.DEVICE_CODE_URL).getPath());
        assertEquals("/oauth/device/token", new URI(TraktOAuth.DEVICE_TOKEN_URL).getPath());
        assertEquals("/oauth/token", new URI(TraktOAuth.TOKEN_URL).getPath());
    }
}
