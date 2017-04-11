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
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader;
import com.ivanmagda.yatranslate.Extras;
import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.api.YandexTranslateApi;
import com.ivanmagda.yatranslate.data.SelectLangListItem;
import com.ivanmagda.yatranslate.data.SelectLangListItemComparator;
import com.ivanmagda.yatranslate.data.adapter.SelectLangAdapter;
import com.ivanmagda.yatranslate.data.model.TranslateLangItem;
import com.ivanmagda.yatranslate.utils.TranslateLangItemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLanguageActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<TranslateLangItem>>,
        SelectLangAdapter.ListItemClickListener {

    private static final String LOG_TAG = SelectLanguageActivity.class.getSimpleName();

    /**
     * Identifies a particular Loader being used in this component.
     */
    private static final int TRANSLATE_LANGS_LOADER_ID = 201;

    @BindView(R.id.rv_langs)
    RecyclerView mRecyclerView;
    SelectLangAdapter mAdapter;

    private String mSelectedLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_SELECTED_LANGUAGE_KEY_TRANSFER)) {
            mSelectedLang = intent.getStringExtra(Extras.EXTRA_SELECTED_LANGUAGE_KEY_TRANSFER);
        }

        setup();

        getSupportLoaderManager().initLoader(TRANSLATE_LANGS_LOADER_ID, null, this);
    }

    private void setup() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new SelectLangAdapter(this);
        mAdapter.setSelectedLangKey(mSelectedLang);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<List<TranslateLangItem>> onCreateLoader(int id, Bundle args) {
        return new GenericAsyncTaskLoader<>(
                this,
                YandexTranslateApi.getSupportedLanguages(),
                new GenericAsyncTaskLoader.OnStartLoadingCondition() {
                    @Override
                    public boolean isMeetConditions(Resource<?> resource) {
                        return true;
                    }
                }
        );
    }

    @Override
    public void onLoadFinished(Loader<List<TranslateLangItem>> loader, List<TranslateLangItem> data) {
        //Pair<List<String>, HashMap<String, List<String>>> langsMap = TranslateLangItemUtils.buildMap(data);
        List<SelectLangListItem> langList = buildLangList(TranslateLangItemUtils.getLangNames(data));

        mAdapter.updateWithNewData(langList);
    }

    @Override
    public void onLoaderReset(Loader<List<TranslateLangItem>> loader) {
        mAdapter.updateWithNewData(null);
    }

    @Override
    public void onListItemClick(SelectLangListItem selectedListItem) {
        Log.d(LOG_TAG, "onListItemClick: " + selectedListItem);
    }

    // Private Helpers.

    private static List<SelectLangListItem> buildLangList(HashMap<String, String> langNamesMap) {
        List<SelectLangListItem> langList = new ArrayList<>(langNamesMap.size());

        for (Map.Entry<String, String> anEntry : langNamesMap.entrySet()) {
            langList.add(new SelectLangListItem(anEntry.getKey(), anEntry.getValue()));
        }

        Collections.sort(langList, new SelectLangListItemComparator());

        return langList;
    }
}
