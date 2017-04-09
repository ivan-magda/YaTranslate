/**
 * Copyright (c) 2017 Ivan Magda
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ivanmagda.yatranslate.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.core.Resource.Parse;
import com.ivanmagda.network.helper.MethodParameters;
import com.ivanmagda.network.helper.UrlBuilder;

import java.net.URL;

public final class YandexTranslateApi {

    private static final String LOG_TAG = YandexTranslateApi.class.getSimpleName();

    // TODO: Replace Yandex Translate API Key with your own.
    // REPLACE_WITH_YOUR_OWN_API_KEY
    private static final String API_KEY = "trnsl.1.1.20170325T155033Z.495075cb2e19b514.b7d60e847a917c8a823856985920026ec5c3ede6";
    private static final String API_SCHEME = "https";
    private static final String API_HOST = "translate.yandex.net";
    private static final String API_PATH = "api/v1.5/tr.json";

    private static final String API_KEY_PARAM = "key";
    private static final String UI_LANGUAGE_KEY_PARAM = "ui";
    private static final String TEXT_KEY_PARAM = "text";
    private static final String TRANSLATE_DIRECTION_KEY_PARAM = "lang";

    private static final String SUPPORTED_LANGUAGES_PATH = "getLangs";
    private static final String TRANSLATE_PATH = "translate";

    /**
     * @return The supported languages Resource.
     */
    public static Resource<String> getSupportedLanguages() {
        MethodParameters parameters = getDefaultMethodParameters();
        parameters.put(UI_LANGUAGE_KEY_PARAM, "en");

        URL url = buildUrl(SUPPORTED_LANGUAGES_PATH, parameters);

        return new Resource<>(url, new Parse<String>() {
            @Override
            public String parse(@Nullable String response) {
                return response;
            }
        });
    }

    public static Resource<String> getTranslation(@NonNull final String text,
                                                  @NonNull final String fromLang,
                                                  @NonNull final String toLang) {
        MethodParameters parameters = getDefaultMethodParameters();
        parameters.put(TEXT_KEY_PARAM, text);
        parameters.put(TRANSLATE_DIRECTION_KEY_PARAM, fromLang + "-" + toLang);

        URL url = buildUrl(TRANSLATE_PATH, parameters);

        return new Resource<>(url, new Parse<String>() {
            @Override
            public String parse(@Nullable String response) {
                return response;
            }
        });
    }

    /**
     * @return The default http method parameters.
     */
    private static MethodParameters getDefaultMethodParameters() {
        MethodParameters parameters = new MethodParameters(5);
        parameters.put(API_KEY_PARAM, API_KEY);
        return parameters;
    }

    /**
     * Builds the URL used to talk to the Yandex Translate API.
     *
     * @param path             The Yandex Translate endpoint path.
     * @param methodParameters The method parameters that will be applied for the URL.
     * @return The URL to use to query the translate server.
     */
    private static URL buildUrl(@Nullable final String path,
                                @NonNull final MethodParameters methodParameters) {
        return UrlBuilder.buildUrl(API_SCHEME, API_HOST, API_PATH, path, methodParameters);
    }
}
