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

package com.ivanmagda.yatranslate.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.adapter.TranslateLanguagesAdapter;
import com.ivanmagda.yatranslate.api.YandexLangLoader;
import com.ivanmagda.yatranslate.data.TranslateLanguagesDbLoader;
import com.ivanmagda.yatranslate.model.SelectLangListItem;
import com.ivanmagda.yatranslate.model.SelectLangListItemComparator;
import com.ivanmagda.yatranslate.model.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.ArrayUtils;
import com.ivanmagda.yatranslate.utils.MapUtils;
import com.ivanmagda.yatranslate.utils.MapUtils.OnFilterCondition;
import com.ivanmagda.yatranslate.utils.TranslateLangDbUtils;
import com.ivanmagda.yatranslate.utils.TranslateLangUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ivanmagda.yatranslate.Extras.EXTRA_CURRENT_LANGUAGE_ITEM_TRANSFER;
import static com.ivanmagda.yatranslate.Extras.EXTRA_SELECT_LANGUAGE_ACTIVITY_MODE_KEY_TRANSFER;
import static com.ivanmagda.yatranslate.Extras.EXTRA_SELECT_LANGUAGE_RESULT;
import static com.ivanmagda.yatranslate.adapter.TranslateLanguagesAdapter.ListItemClickListener;

public class SelectLanguageActivity extends AppCompatActivity implements ListItemClickListener,
        YandexLangLoader.CallbacksListener, TranslateLanguagesDbLoader.CallbacksListener {

    /**
     * Defines how and what languages will be selected.
     */
    public static final int SELECT_FROM_LANG_MODE = 0;
    public static final int SELECT_TO_LANG_MODE = 1;

    private static final String LOG_TAG = SelectLanguageActivity.class.getSimpleName();

    private static final int TRANSLATE_LANGS_API_LOADER_ID = 201;
    private static final int TRANSLATE_LANGS_DB_LOADER_ID = 301;

    @BindView(R.id.rv_langs) RecyclerView mRecyclerView;

    @BindView(R.id.swipe_container) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.langs_progress_bar) ProgressBar mProgressBar;

    /**
     * RecyclerView adapter.
     */
    TranslateLanguagesAdapter mAdapter;

    /**
     * Helps to control translate language selection flow.
     * <p>
     * When we in the SELECT_FROM_LANG_MODE:
     * We could see list with all of the available source languages by the Yandex Translate API.
     * <p>
     * SELECT_TO_LANG_MODE:
     * We could see only languages that are supported by the source language (from what language
     * we want translate).
     * e.g. en: { "ru", "be", "ca" ...}
     */
    private int mSelectionMode;

    /**
     * Defines translate sequence from => to language.
     * Must be instantiated via intent extras.
     */
    private TranslateLangItem mLangItem;

    /**
     * Holds response with supported languages.
     * Updates onLangsLoadFinished.
     */
    private List<TranslateLangItem> mSupportedLangs;

    /**
     * Yandex supported languages loader.
     */
    YandexLangLoader mLangApiLoader;

    /**
     * Supported languages database loader.
     */
    TranslateLanguagesDbLoader mLangDbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang);
        ButterKnife.bind(this);

        evaluateExtras();
        setup();

        getSupportLoaderManager().initLoader(TRANSLATE_LANGS_DB_LOADER_ID, null, mLangDbLoader);
    }

    private void setup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new TranslateLanguagesAdapter(this);
        mAdapter.setSelectedLangKey(getSelectedLangKey());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(TRANSLATE_LANGS_API_LOADER_ID, null, mLangApiLoader);
            }
        });

        mLangApiLoader = new YandexLangLoader(this, this);
        mLangDbLoader = new TranslateLanguagesDbLoader(this, this);
    }

    @Override
    public void onLangsLoadFinished(List<TranslateLangItem> translateLangItems) {
        mSupportedLangs = translateLangItems;
        TranslateLangDbUtils.persistLangs(this, mSupportedLangs);

        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);

        onFetchSuccess();
    }

    @Override
    public void onFinishLangsQuery(Cursor cursor) {
        mSupportedLangs = TranslateLangDbUtils.buildItemsFromCursor(cursor);

        if (ArrayUtils.isEmpty(mSupportedLangs)) {
            mProgressBar.setVisibility(View.VISIBLE);
            getSupportLoaderManager().initLoader(TRANSLATE_LANGS_API_LOADER_ID, null, mLangApiLoader);
        } else {
            onFetchSuccess();
        }
    }

    @Override
    public void onLangsLoaderReset() {
        mAdapter.updateWithNewData(null);
    }

    @Override
    public void onListItemClick(SelectLangListItem selectedListItem) {
        Intent resultIntent = new Intent();

        String langKey = selectedListItem.getLangKey();
        String langName = selectedListItem.getLangName();

        switch (mSelectionMode) {
            case SELECT_FROM_LANG_MODE:
                mLangItem.setFromLang(langKey);
                mLangItem.setFromLangName(langName);

                if (!isToLangInSupported()) {
                    mLangItem.setToLang(null);
                    mLangItem.setToLangName(null);
                }

                break;
            case SELECT_TO_LANG_MODE:
                mLangItem.setToLang(langKey);
                mLangItem.setToLangName(langName);
                break;
        }

        resultIntent.putExtra(EXTRA_SELECT_LANGUAGE_RESULT, mLangItem);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // Private Helpers.

    private void evaluateExtras() {
        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_CURRENT_LANGUAGE_ITEM_TRANSFER)) {
            mLangItem = intent.getParcelableExtra(EXTRA_CURRENT_LANGUAGE_ITEM_TRANSFER);
        } else {
            throw new RuntimeException("Provide current language item object.");
        }

        if (intent.hasExtra(EXTRA_SELECT_LANGUAGE_ACTIVITY_MODE_KEY_TRANSFER)) {
            mSelectionMode = intent.getIntExtra(EXTRA_SELECT_LANGUAGE_ACTIVITY_MODE_KEY_TRANSFER,
                    SELECT_FROM_LANG_MODE);
        } else {
            throw new RuntimeException(
                    "Provide language selection mode: SELECT_FROM_LANG_MODE or SELECT_TO_LANG_MODE.");
        }
    }

    /**
     * Returns lang id string of the current selected `to` or `from` language.
     *
     * @return Language id string.
     */
    private String getSelectedLangKey() {
        return mSelectionMode == SELECT_FROM_LANG_MODE ? mLangItem.getFromLang() : mLangItem.getToLang();
    }

    private void onFetchSuccess() {
        List<SelectLangListItem> langItems;

        switch (mSelectionMode) {
            case SELECT_FROM_LANG_MODE:
                langItems = buildLangList(TranslateLangUtils.getLangNames(mSupportedLangs));
                break;
            case SELECT_TO_LANG_MODE:
                langItems = buildToLangItems();
                break;
            default:
                throw new RuntimeException("Receive unsupported selection mode.");
        }

        mAdapter.updateWithNewData(langItems);
    }

    /**
     * Build a list of the SelectLangListItem with a languages you could translate to,
     * based on source translate language.
     *
     * @return List of lang items.
     */
    private List<SelectLangListItem> buildToLangItems() {
        final Set<String> supportedLangsSet = getSupportedLangsSet();

        OnFilterCondition<String, String> filterCondition = new OnFilterCondition<String, String>() {
            @Override
            public boolean isMeetCondition(String key, String value) {
                return supportedLangsSet.contains(key);
            }
        };

        Map<String, String> toLangs = MapUtils.filter(
                TranslateLangUtils.getLangNames(mSupportedLangs),
                filterCondition
        );

        return buildLangList(toLangs);
    }

    /**
     * Maps map of lang key and name values into list of the SelectLangListItem items,
     * that accepts TranslateLanguagesAdapter.
     *
     * @param langNamesMap Map of langKey and langName
     * @return List of items to be used by the TranslateLanguagesAdapter.
     */
    private static List<SelectLangListItem> buildLangList(Map<String, String> langNamesMap) {
        List<SelectLangListItem> langList = new ArrayList<>(langNamesMap.size());

        for (Map.Entry<String, String> anEntry : langNamesMap.entrySet()) {
            langList.add(new SelectLangListItem(anEntry.getKey(), anEntry.getValue()));
        }

        Collections.sort(langList, new SelectLangListItemComparator());

        return langList;
    }

    /**
     * Returns supported languages based on current source language.
     *
     * @return List of supported languages to what you could translate to.
     */
    private List<String> getSupportedLangs() {
        Pair<List<String>, HashMap<String, List<String>>> langsMap = TranslateLangUtils
                .buildMap(mSupportedLangs);
        return langsMap.second.get(mLangItem.getFromLang());
    }

    /**
     * Returns set of supported languages.
     *
     * @return Set of supported languages to what you could translate to.
     */
    private Set<String> getSupportedLangsSet() {
        List<String> supportedLangs = getSupportedLangs();
        return new HashSet<>(supportedLangs);
    }

    /**
     * Check is current destination (to) language is accepted by the source language.
     *
     * @return true if supported otherwise false.
     */
    private boolean isToLangInSupported() {
        return getSupportedLangsSet().contains(mLangItem.getToLang());
    }
}
