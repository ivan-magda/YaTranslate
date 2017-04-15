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
package com.ivanmagda.yatranslate.viewmodel;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.ivanmagda.yatranslate.R;
import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.model.core.TranslateLangItem;

public final class TranslateItemViewModel {

    private final TranslateItem mTranslateItem;
    private final Context mContext;

    public TranslateItemViewModel(@NonNull final TranslateItem translateItem,
                                  @NonNull final Context context) {
        this.mTranslateItem = translateItem;
        this.mContext = context;
    }

    // Public Methods.

    public int getFavoriteColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getFavoriteColorMarshmallow();
        } else {
            return mTranslateItem.isFavorite()
                    ? mContext.getResources().getColor(R.color.colorAccent)
                    : mContext.getResources().getColor(R.color.black);
        }
    }

    public String getFormattedLangKeys() {
        TranslateLangItem langItem = mTranslateItem.getTranslateLangItem();
        return langItem.getFromLang() + " - " + langItem.getToLang();
    }

    // Private Methods.

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getFavoriteColorMarshmallow() {
        return mTranslateItem.isFavorite()
                ? mContext.getColor(R.color.colorAccent)
                : mContext.getColor(R.color.black);
    }
}
