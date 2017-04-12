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

package com.ivanmagda.yatranslate.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ivanmagda.yatranslate.utils.ArrayUtils;

import java.util.List;

/**
 * Wrapper around TranslateFragment instance variables.
 */
public final class TranslateFragmentState implements Parcelable {

    private String mTextToTranslate;
    private List<TranslateItem> mTranslateResults;
    private TranslateLangItem mTranslateLangs;

    public TranslateFragmentState(String textToTranslate,
                                  List<TranslateItem> translateResults,
                                  TranslateLangItem translateLangs) {
        this.mTextToTranslate = textToTranslate;
        this.mTranslateResults = translateResults;
        this.mTranslateLangs = translateLangs;
    }

    public TranslateFragmentState(Parcel in) {
        this.mTextToTranslate = in.readString();
        in.readTypedList(mTranslateResults, TranslateItem.CREATOR);
        this.mTranslateLangs = in.readParcelable(TranslateLangItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTextToTranslate);
        dest.writeTypedList(mTranslateResults);
        dest.writeParcelable(mTranslateLangs, flags);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new TranslateFragmentState(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public String getTextToTranslate() {
        return mTextToTranslate;
    }

    public void setTextToTranslate(String textToTranslate) {
        this.mTextToTranslate = textToTranslate;
    }

    public List<TranslateItem> getTranslateResults() {
        return mTranslateResults;
    }

    public void setTranslateResults(List<TranslateItem> translateResults) {
        if (ArrayUtils.isEmpty(translateResults)) {
            mTranslateResults.clear();
        } else {
            mTranslateResults = translateResults;
        }
    }

    public TranslateLangItem getTranslateLangs() {
        return mTranslateLangs;
    }

    public void setTranslateLangs(TranslateLangItem translateLangs) {
        this.mTranslateLangs = translateLangs;
    }
}
