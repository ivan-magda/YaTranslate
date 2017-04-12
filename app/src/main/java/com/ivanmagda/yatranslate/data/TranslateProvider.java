package com.ivanmagda.yatranslate.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.ivanmagda.yatranslate.data.TranslateContract.LanguageEntry;

/**
 * {@link ContentProvider} for YaTranslate app.
 */
public class TranslateProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the languages table.
     */
    private static final int LANGUAGES = 100;

    /**
     * URI matcher code for the content URI for a single language in the languages table.
     */
    private static final int LANGUAGE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TranslateContract.CONTENT_AUTHORITY, TranslateContract.PATH_LANGUAGES,
                LANGUAGES);
        sUriMatcher.addURI(TranslateContract.CONTENT_AUTHORITY,
                TranslateContract.PATH_LANGUAGES + "/#", LANGUAGE_ID);
    }

    /**
     * Database helper object.
     */
    private TranslateDbHelper mTranslateDbHelper;

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCreate() {
        mTranslateDbHelper = new TranslateDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mTranslateDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case LANGUAGES:
                cursor = database.query(LanguageEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case LANGUAGE_ID:
                selection = LanguageEntry._ID + "=?";
                selectionArgs = new String[]{idStringFrom(uri)};

                cursor = database.query(LanguageEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LANGUAGES:
                return LanguageEntry.CONTENT_LIST_TYPE;
            case LANGUAGE_ID:
                return LanguageEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case LANGUAGES:
                SQLiteDatabase database = mTranslateDbHelper.getWritableDatabase();
                long id = database.insert(LanguageEntry.TABLE_NAME, null, values);

                if (id > 0) {
                    // Notify all listeners that the data has changed for the product content URI.
                    notifyChangeWithUri(uri);

                    // Return the new URI with the ID (of the newly inserted row) appended at the end.
                    return ContentUris.withAppendedId(uri, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mTranslateDbHelper.getWritableDatabase();

        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case LANGUAGES:
                rowsDeleted = database.delete(LanguageEntry.TABLE_NAME, null, null);
                break;
            case LANGUAGE_ID:
                // Delete a single row given by the ID in the URI.
                selection = LanguageEntry._ID + "=?";
                selectionArgs = new String[]{idStringFrom(uri)};
                rowsDeleted = database.delete(LanguageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed.
        if (rowsDeleted != 0) {
            notifyChangeWithUri(uri);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case LANGUAGE_ID:
                selection = LanguageEntry._ID + "=?";
                selectionArgs = new String[]{idStringFrom(uri)};

                SQLiteDatabase database = mTranslateDbHelper.getWritableDatabase();

                int rowsUpdated = database.update(LanguageEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                if (rowsUpdated > 0) {
                    notifyChangeWithUri(uri);
                }

                return rowsUpdated;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case LANGUAGES:
                final SQLiteDatabase database = mTranslateDbHelper.getWritableDatabase();
                database.beginTransaction();

                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = database.insert(LanguageEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                notifyChangeWithUri(uri);

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private String idStringFrom(Uri uri) {
        return String.valueOf(ContentUris.parseId(uri));
    }

    private void notifyChangeWithUri(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }
}
