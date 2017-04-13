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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry;
import static com.ivanmagda.yatranslate.data.TranslateContract.LanguageEntry;

/**
 * Manages a local database for translate data.
 */
public final class TranslateDbHelper extends SQLiteOpenHelper {

    /**
     * If database schema changed, we must increment the database version.
     */
    private static final int DATABASE_VERSION = 2;

    /**
     * Name of the database file.
     */
    private static final String DATABASE_NAME = "translate.db";

    private static final String SQL_CREATE_LANGUAGES_TABLE = "CREATE TABLE " +
            LanguageEntry.TABLE_NAME + " (" +
            LanguageEntry._ID + " INTEGER PRIMARY KEY," +
            LanguageEntry.COLUMN_TRANSLATE_FROM_KEY + " TEXT NOT NULL, " +
            LanguageEntry.COLUMN_TRANSLATE_TO_KEY + " TEXT NOT NULL, " +
            LanguageEntry.COLUMN_TRANSLATE_FROM_NAME + " TEXT NOT NULL, " +
            LanguageEntry.COLUMN_TRANSLATE_TO_NAME + " TEXT NOT NULL " +
            " );";

    private static final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " +
            HistoryEntry.TABLE_NAME + " (" +
            HistoryEntry._ID + " INTEGER PRIMARY KEY," +
            HistoryEntry.COLUMN_TEXT_TO_TRANSLATE + " TEXT NOT NULL, " +
            HistoryEntry.COLUMN_TEXT_TRANSLATED + " TEXT NOT NULL, " +
            HistoryEntry.COLUMN_LANG_TRANSLATE_FROM + " TEXT NOT NULL, " +
            HistoryEntry.COLUMN_LANG_TRANSLATE_TO + " TEXT NOT NULL, " +
            HistoryEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
            HistoryEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP " +
            " );";

    private static final String SQL_DROP_LANGUAGES_TABLE =
            "DROP TABLE IF EXISTS " + LanguageEntry.TABLE_NAME;
    private static final String SQL_DROP_HISTORY_TABLE =
            "DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME;

    /**
     * Constructs a new instance of {@link SQLiteOpenHelper}.
     *
     * @param context of the app
     */
    public TranslateDbHelper(@NonNull final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_LANGUAGES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    /**
     * This database is for a cache of the online data and user history,
     * so its upgrade policy is simply to discard the data of the languages table
     * and call through to onCreate to recreate the table.
     * While we in a development we could simply drop tables, but for a production we need to
     * write migrations for history table.
     * <p>
     * This only fires we change the version number database (in our case, DATABASE_VERSION).
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_LANGUAGES_TABLE);
        sqLiteDatabase.execSQL(SQL_DROP_HISTORY_TABLE);
        onCreate(sqLiteDatabase);
    }
}
