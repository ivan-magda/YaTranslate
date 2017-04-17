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

package com.ivanmagda.yatranslate.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.helper.GenericAsyncTaskLoader;
import com.ivanmagda.yatranslate.model.core.TranslateLangItem;

import java.util.List;

public final class YandexLangLoader
        implements LoaderManager.LoaderCallbacks<List<TranslateLangItem>> {

    public interface CallbacksListener {
        void onLangsLoadFinished(List<TranslateLangItem> translateLangItems);

        void onLangsLoaderReset();
    }

    private final Context mContext;
    private final CallbacksListener mCallbacksListener;

    public YandexLangLoader(@NonNull final Context context,
                            @NonNull final CallbacksListener callbacksListener) {
        this.mContext = context;
        this.mCallbacksListener = callbacksListener;
    }

    @Override
    public Loader<List<TranslateLangItem>> onCreateLoader(int id, Bundle args) {
        return new GenericAsyncTaskLoader<>(
                mContext,
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
        mCallbacksListener.onLangsLoadFinished(data);
    }

    @Override
    public void onLoaderReset(Loader<List<TranslateLangItem>> loader) {
        mCallbacksListener.onLangsLoaderReset();
    }
}
