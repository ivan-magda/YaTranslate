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

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ivanmagda.yatranslate.model.core.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.TranslateLangUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle Yandex Translate supported languages JSON data.
 */
public final class TranslateLangItemJsonUtils {

    /* Response keys. */
    private static final String DIRS_RESPONSE_KEY = "dirs";
    private static final String LANGS_RESPONSE_KEY = "langs";

    private TranslateLangItemJsonUtils() {
    }

    public static List<TranslateLangItem> buildFromResponse(@Nullable String response) {
        if (TextUtils.isEmpty(response)) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(response);
            JSONArray dirs = json.getJSONArray(DIRS_RESPONSE_KEY);
            JSONObject langs = json.getJSONObject(LANGS_RESPONSE_KEY);

            if (dirs == null || langs == null) return null;

            return parse(dirs, langs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<TranslateLangItem> parse(JSONArray dirs, JSONObject langs) throws JSONException {
        ArrayList<TranslateLangItem> parsedItems = new ArrayList<>(dirs.length());

        for (int i = 0; i < dirs.length(); i++) {
            String langDir = dirs.getString(i);

            String fromLang = TranslateLangUtils.getFromLangName(langDir);
            String toLang = TranslateLangUtils.getToLangName(langDir);
            String fromName = langs.getString(fromLang);
            String toName = langs.getString(toLang);

            parsedItems.add(new TranslateLangItem(fromLang, toLang, fromName, toName));
        }

        return parsedItems;
    }

}

