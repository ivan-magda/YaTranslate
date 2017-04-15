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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.ivanmagda.yatranslate.fragment.BookmarkListFragment;

import static com.ivanmagda.yatranslate.data.TranslateContract.HistoryEntry;

public final class TranslateHistoryLoader implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface CallbacksListener {
        void onHistoryLoadFinished(Cursor cursor);

        void onHistoryLoaderReset();
    }

    private Context mContext;
    private CallbacksListener mCallbacksListener;
    private BookmarkListFragment.ContentFilter mContentFilter;

    public TranslateHistoryLoader(@NonNull final Context context,
                                  @NonNull final BookmarkListFragment.ContentFilter contentFilter,
                                  @NonNull final CallbacksListener callbacksListener) {
        this.mContext = context;
        this.mContentFilter = contentFilter;
        this.mCallbacksListener = callbacksListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /* URI for all rows of translate history data in our history table */
        Uri historyQueryUri = HistoryEntry.CONTENT_URI;
        /* Sort order: Descending by creation date */
        String sortOrder = HistoryEntry.COLUMN_CREATED_AT + " DESC";
        /*A SELECTION that declares which rows we'd like to return. */
        String selection = null;

        if (mContentFilter == BookmarkListFragment.ContentFilter.FAVORITE) {
            selection = HistoryEntry.COLUMN_FAVORITE + " == 1";
        }

        return new CursorLoader(
                mContext,
                historyQueryUri,
                null,
                selection,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCallbacksListener.onHistoryLoadFinished(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCallbacksListener.onHistoryLoaderReset();
    }
}
