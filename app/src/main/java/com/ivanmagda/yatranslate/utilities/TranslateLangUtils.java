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

package com.ivanmagda.yatranslate.utilities;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.ivanmagda.yatranslate.model.core.TranslateLangItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class TranslateLangUtils {

    private static final int FROM_LANG_TRANSLATE_INDEX = 0;
    private static final int TO_LANG_TRANSLATE_INDEX = 1;

    public static String getFromLangName(@NonNull final String lang) {
        return splitLang(lang)[FROM_LANG_TRANSLATE_INDEX];
    }

    public static String getToLangName(@NonNull final String lang) {
        return splitLang(lang)[TO_LANG_TRANSLATE_INDEX];
    }

    private static String[] splitLang(@NonNull final String lang) {
        return lang.split("-");
    }

    public static HashMap<String, String> getLangNames(@NonNull final List<TranslateLangItem> langItems) {
        HashMap<String, String> langNames = new HashMap<>(langItems.size());

        for (TranslateLangItem anItem : langItems) {
            langNames.put(anItem.getFromLang(), anItem.getFromLangName());
            langNames.put(anItem.getToLang(), anItem.getToLangName());
        }

        return langNames;
    }

    public static Pair<List<String>, HashMap<String, List<String>>> buildMap(
            @NonNull final List<TranslateLangItem> langItems) {
        HashSet<String> fromLangsSet = new HashSet<>(langItems.size());
        HashMap<String, HashSet<String>> toLangsMap = new HashMap<>(langItems.size());

        for (TranslateLangItem anItem : langItems) {
            String fromLang = anItem.getFromLang();
            String toLang = anItem.getToLang();

            fromLangsSet.add(anItem.getFromLang());

            if (toLangsMap.get(fromLang) == null) {
                toLangsMap.put(fromLang, new HashSet<String>(5));
            }

            toLangsMap.get(fromLang).add(toLang);
        }

        List<String> fromLangs = new ArrayList<>(fromLangsSet);
        ListSortUtils.caseInsensitiveSort(fromLangs);

        HashMap<String, List<String>> toLangs = new HashMap<>(toLangsMap.size());
        for (String key : toLangsMap.keySet()) {
            HashSet<String> value = toLangsMap.get(key);

            List<String> langsArray = new ArrayList<>(value);
            ListSortUtils.caseInsensitiveSort(langsArray);
            toLangs.put(key, langsArray);
        }

        return new Pair<>(fromLangs, toLangs);
    }
}
