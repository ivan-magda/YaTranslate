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

package com.ivanmagda.yatranslate.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.adapter.TranslateHistoryAdapter;
import com.ivanmagda.yatranslate.data.TranslateHistoryLoader;
import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.utils.database.TranslateItemDbUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ivanmagda.yatranslate.fragment.BookmarkListFragment.ContentFilter.ALL;

public class BookmarkListFragment extends Fragment
        implements TranslateHistoryLoader.CallbacksListener,
        TranslateHistoryAdapter.TranslateHistoryAdapterOnClickListener {

    /**
     * Defines what kind of history items to present: all or favorites.
     */
    public enum ContentFilter {
        ALL,
        FAVORITE
    }

    /**
     * This ID will be used to identify the Loader responsible for loading our translate history
     */
    private static final int ID_HISTORY_LOADER = 44;
    private static final String ARG_CONTENT_FILTER = "content-filter";

    @BindView(R.id.rv_history) RecyclerView mRecyclerView;

    /**
     * The content filter option.
     */
    private ContentFilter mContentFilter = ALL;

    /**
     * TranslateHistoryLoader is responsible for loading our history data from the history table.
     * Both: all and favorite items.
     */
    private TranslateHistoryLoader mTranslateHistoryLoader;

    /**
     * The TranslateHistoryAdapter is responsible for linking our translate history data with
     * the Views that will end up displaying our history data.
     */
    private TranslateHistoryAdapter mTranslateHistoryAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookmarkListFragment() {
    }

    public static BookmarkListFragment newInstance(final ContentFilter contentFilter) {
        BookmarkListFragment fragment = new BookmarkListFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT_FILTER, contentFilter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mContentFilter = (ContentFilter) getArguments().get(ARG_CONTENT_FILTER);
        }

        mTranslateHistoryLoader = new TranslateHistoryLoader(getContext(), mContentFilter, this);
        getLoaderManager().initLoader(ID_HISTORY_LOADER, null, mTranslateHistoryLoader);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setLayoutManager(layoutManager);

        mTranslateHistoryAdapter = new TranslateHistoryAdapter(null, this);
        mRecyclerView.setAdapter(mTranslateHistoryAdapter);

        return view;
    }

    // TranslateHistoryLoader.CallbacksListener.

    @Override
    public void onHistoryLoadFinished(Cursor cursor) {
        // Call mTranslateHistoryAdapter's swapCursor method and pass in the new Cursor
        mTranslateHistoryAdapter.swapCursor(cursor);
        // If mPosition equals RecyclerView.NO_POSITION, set it to 0
        //if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        // Smooth scroll the RecyclerView to mPosition
        //mRecyclerView.smoothScrollToPosition(mPosition);

        // If the Cursor's size is not equal to 0, call showWeatherDataView
        //if (cursor.getCount() != 0) showWeatherDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     */
    @Override
    public void onHistoryLoaderReset() {
        mTranslateHistoryAdapter.swapCursor(null);
    }

    // TranslateHistoryAdapterOnClickListener.

    @Override
    public void onRowClick(int position, @NonNull TranslateItem selectedItem) {

    }

    @Override
    public void onToggleFavoriteClick(@NonNull TranslateItem selectedItem) {
        TranslateItemDbUtils.toggleFavorite(getContext(), selectedItem);
    }
}
