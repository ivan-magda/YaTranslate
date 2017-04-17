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
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.adapter.TranslateHistoryAdapter;
import com.ivanmagda.yatranslate.data.TranslateHistoryLoader;
import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.utilities.database.TranslateItemDbUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ivanmagda.yatranslate.fragment.BookmarkListFragment.ContentFilter.ALL;

public class BookmarkListFragment extends Fragment implements TranslateHistoryLoader.CallbacksListener,
        TranslateHistoryAdapter.TranslateHistoryAdapterOnClickListener, SearchView.OnQueryTextListener {

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(TranslateItem selectedItem);
    }

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
    @BindView(R.id.tv_empty) TextView mEmptyTextView;

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
     * List interaction listener.
     */
    private OnListFragmentInteractionListener mListener;

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
        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.bookmark, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setQueryHint(getString(R.string.hint_search_history));
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, menuItem, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, menuItem, true);
                queryForText(null);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                clearHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + OnListFragmentInteractionListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // TranslateHistoryLoader.CallbacksListener.

    @Override
    public void onHistoryLoadFinished(Cursor cursor) {
        // Call mTranslateHistoryAdapter's swapCursor method and pass in the new Cursor
        mTranslateHistoryAdapter.swapCursor(cursor);

        mEmptyTextView.setVisibility(cursor.getCount() == 0 ? View.VISIBLE : View.INVISIBLE);
        if (TextUtils.isEmpty(mTranslateHistoryLoader.getQuery())) {
            mEmptyTextView.setText((mContentFilter == ContentFilter.ALL
                    ? R.string.tv_empty_history
                    : R.string.tv_empty_favorite)
            );
        } else {
            mEmptyTextView.setText(R.string.tv_empty_history_search);
        }
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
        mListener.onListFragmentInteraction(selectedItem);
    }

    @Override
    public void onToggleFavoriteClick(@NonNull TranslateItem selectedItem) {
        TranslateItemDbUtils.toggleFavorite(getContext(), selectedItem);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        queryForText(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        queryForText(newText);
        return false;
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
    }

    private void clearHistory() {
        if (mTranslateHistoryAdapter.getItemCount() == 0) return;

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.clear_history_title)
                .setMessage(R.string.msg_clear_history)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TranslateItemDbUtils.clearHistory(getContext());
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_delete_holo_light)
                .show();
    }

    private void queryForText(String queryString) {
        mTranslateHistoryLoader.setQuery(queryString);
        getLoaderManager().restartLoader(ID_HISTORY_LOADER, null, mTranslateHistoryLoader);
    }
}
