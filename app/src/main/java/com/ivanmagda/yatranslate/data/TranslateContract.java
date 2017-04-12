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

package com.ivanmagda.yatranslate.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the translate database.
 */
public final class TranslateContract {

    /**
     * The name for the entire content provider.
     * A convenient string to use for the content authority is the package name for the app,
     * which is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.ivanmagda.yatranslate";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible URI's)
     * For instance, content://com.ivanmagda.yatranslate/languages/ is a valid path for
     * looking at languages data.
     * content://com.ivanmagda.yatranslate/givemedata/ will fail.
     */
    public static final String PATH_LANGUAGES = "languages";
    public static final String PATH_HISTORY = "history";

    /* Inner class that defines the table contents of the languages table */
    public static final class LanguageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LANGUAGES).build();

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of languages.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single language.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LANGUAGES;

        /**
         * Table name.
         */
        public static final String TABLE_NAME = "languages";

        /**
         * The source language key string is what language we will translate from.
         * <p>
         * TYPE: TEXT
         */
        public static final String COLUMN_TRANSLATE_FROM_KEY = "translate_from_key";

        /**
         * The destination language key string is what language we will translate to.
         * <p>
         * TYPE: TEXT
         */
        public static final String COLUMN_TRANSLATE_TO_KEY = "translate_to_key";

        /**
         * The source language name string.
         * <p>
         * TYPE: TEXT
         */
        public static final String COLUMN_TRANSLATE_FROM_NAME = "translate_from_name";

        /**
         * The destination language name string.
         * <p>
         * TYPE: TEXT
         */
        public static final String COLUMN_TRANSLATE_TO_NAME = "translate_to_name";

        public static Uri buildLanguageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
