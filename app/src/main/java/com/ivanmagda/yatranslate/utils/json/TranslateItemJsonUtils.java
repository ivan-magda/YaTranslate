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

package com.ivanmagda.yatranslate.utils.json;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ivanmagda.yatranslate.data.TranslateItem;
import com.ivanmagda.yatranslate.data.TranslateLangItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle Yandex Translate translate query JSON data.
 */
public final class TranslateItemJsonUtils {

    /* Response keys. */
    private static final String RESPONSE_CODE_KEY = "code";
    private static final String LANG_RESPONSE_KEY = "lang";
    private static final String TEXT_RESPONSE_KEY = "text";

    private TranslateItemJsonUtils() {
    }

    public static List<TranslateItem> buildItemsFromResponseAndSourceText(
            @NonNull final String textToTranslate,
            @Nullable String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(response);

            int responseCode = json.getInt(RESPONSE_CODE_KEY);
            if (responseCode >= HttpURLConnection.HTTP_OK || responseCode <= 299) {
                TranslateLangItem langItem = new TranslateLangItem(json.getString(LANG_RESPONSE_KEY));
                JSONArray translatedWords = json.getJSONArray(TEXT_RESPONSE_KEY);

                return parseTranslatedWords(textToTranslate, langItem, translatedWords);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<TranslateItem> parseTranslatedWords(
            @NonNull final String textToTranslate,
            @NonNull final TranslateLangItem langItem,
            @Nullable final JSONArray translatedWords) throws JSONException {
        if (translatedWords == null || translatedWords.length() == 0) return null;

        List<TranslateItem> translatedItems = new ArrayList<>(translatedWords.length());
        for (int i = 0; i < translatedWords.length(); i++) {
            String translateText = translatedWords.getString(i);
            translatedItems.add(new TranslateItem(textToTranslate, translateText, langItem));
        }

        return translatedItems;
    }

}
