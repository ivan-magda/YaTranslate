package com.ivanmagda.network.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

public final class UrlBuilder {

    private static final String LOG_TAG = UrlBuilder.class.getSimpleName();

    private UrlBuilder() {
    }

    /**
     * Builds the URL used to talk to the some API.
     *
     * @param scheme           The URL scheme.
     * @param host             The URL host.
     * @param baseApiPath      The basic API URL path.
     * @param apiRequestPath   API request endpoint path.
     * @param methodParameters The method parameters that will be applied for the URL.
     * @return The URL to use to query the web-server.
     */
    public static URL buildUrl(@NonNull final String scheme,
                               @NonNull final String host,
                               @NonNull final String baseApiPath,
                               @Nullable final String apiRequestPath,
                               @Nullable final MethodParameters methodParameters) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(scheme)
                .authority(host)
                .appendPath(baseApiPath);

        if (!TextUtils.isEmpty(apiRequestPath)) {
            builder.appendPath(apiRequestPath);
        }

        if (methodParameters != null && !methodParameters.isEmpty()) {
            for (Map.Entry<String, String> parameter : methodParameters.entrySet()) {
                builder.appendQueryParameter(parameter.getKey(), parameter.getValue());
            }
        }

        URL url = urlFromBuilder(builder);
        Log.d(LOG_TAG, "URL: " + url);

        return url;
    }

    private static URL urlFromBuilder(Uri.Builder builder) {
        URL url = null;
        try {
            String urlString = builder.build().toString();
            url = new URL(URLDecoder.decode(urlString, "UTF-8"));
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
