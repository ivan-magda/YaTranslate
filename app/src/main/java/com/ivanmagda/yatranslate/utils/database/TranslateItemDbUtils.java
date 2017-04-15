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
package com.ivanmagda.yatranslate.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.model.core.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry;
import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry.COLUMN_LANG_TRANSLATE_FROM;
import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry.COLUMN_LANG_TRANSLATE_TO;
import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry.COLUMN_TEXT_TO_TRANSLATE;
import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry.COLUMN_TEXT_TRANSLATED;
import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry.buildUriWithText;

public final class TranslateItemDbUtils {

    private TranslateItemDbUtils() {
    }

    // Public.

    public static void addToHistory(@NonNull final Context context,
                                    @NonNull final List<TranslateItem> translateItem) {
        for (TranslateItem anTranslateItem: translateItem) {
            addToHistory(context, anTranslateItem);
        }
    }

    public static void addToHistory(@NonNull final Context context,
                                    @NonNull final TranslateItem translateItem) {
        if (!isExist(context, translateItem)) {
            context.getContentResolver()
                    .insert(HistoryEntry.CONTENT_URI, toContentValues(translateItem));
        }
    }

    public static void toggleFavorite(@NonNull final Context context,
                                      @NonNull final TranslateItem translateItem) {
        Uri uri = HistoryEntry.buildUriWithId(translateItem.getId());

        translateItem.toggleFavorite();
        ContentValues contentValues = toContentValues(translateItem);

        context.getContentResolver().update(uri, contentValues, null, null);
    }

    /**
     * Build TranslateItem from the current position of the Cursor.
     * @param cursor The cursor from which data will be extracted.
     * @return Created TranslateItem from the cursor value.
     */
    @SuppressWarnings("ConstantConditions")
    public static @Nullable TranslateItem buildFromCursor(@NonNull final Cursor cursor) {
        if (cursor.getCount() == 0) {
            return null;
        }

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryEntry._ID));
        boolean isFavorite = cursor
                .getInt(cursor.getColumnIndexOrThrow(HistoryEntry.COLUMN_FAVORITE)) != 0;
        String textToTranslate = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_TEXT_TO_TRANSLATE));
        String translatedText = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_TEXT_TRANSLATED));
        String fromLang = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_LANG_TRANSLATE_FROM));
        String toLang = cursor.getString(
                cursor.getColumnIndexOrThrow(COLUMN_LANG_TRANSLATE_TO));
        TranslateLangItem langItem = new TranslateLangItem(fromLang, toLang, null, null);

        return new TranslateItem(id, isFavorite, textToTranslate, translatedText, langItem);
    }

    // Private.

    private static List<TranslateItem> buildTranslateItems(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }

        List<TranslateItem> items = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
            TranslateItem translateItem = buildFromCursor(cursor);
            if (translateItem != null) {
                items.add(translateItem);
            }
        }

        return items;
    }

    private static ContentValues toContentValues(@NonNull final TranslateItem item) {
        ContentValues contentValues = new ContentValues(5);

        contentValues.put(HistoryEntry.COLUMN_TEXT_TO_TRANSLATE, item.getTextToTranslate());
        contentValues.put(HistoryEntry.COLUMN_TEXT_TRANSLATED, item.getTranslatedText());
        contentValues.put(HistoryEntry.COLUMN_LANG_TRANSLATE_FROM,
                item.getTranslateLangItem().getFromLang());
        contentValues.put(HistoryEntry.COLUMN_LANG_TRANSLATE_TO,
                item.getTranslateLangItem().getToLang());
        contentValues.put(HistoryEntry.COLUMN_FAVORITE, item.isFavorite() ? 1 : 0);

        if (item.getId() != -1) {
            contentValues.put(HistoryEntry._ID, item.getId());
        }

        return contentValues;
    }

    private static boolean isExist(@NonNull final Context context,
                                   @NonNull final TranslateItem translateItem) {
        Uri searchUri = buildUriWithText(translateItem.getTextToTranslate());
        Cursor cursor = context.getContentResolver().query(searchUri, null, null, null, null);

        List<TranslateItem> list = buildTranslateItems(cursor);
        if (ArrayUtils.isEmpty(list)) return false;

        for (TranslateItem anItem : list) {
            if (anItem.equals(translateItem)) return true;
        }

        cursor.close();

        return false;
    }

}
