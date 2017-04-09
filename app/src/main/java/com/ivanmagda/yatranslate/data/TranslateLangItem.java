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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public final class TranslateLangItem implements Parcelable {

    private static final int FROM_LANG_TRANSLATE_INDEX = 0;
    private static final int TO_LANG_TRANSLATE_INDEX = 1;

    private final String mFromLang;
    private final String mToLang;

    public TranslateLangItem(@NonNull final String lang) {
        String[] langs = splitLang(lang);
        this.mFromLang = langs[FROM_LANG_TRANSLATE_INDEX];
        this.mToLang = langs[TO_LANG_TRANSLATE_INDEX];
    }

    public TranslateLangItem(@NonNull final String fromLang, @NonNull final String toLang) {
        this.mFromLang = fromLang;
        this.mToLang = toLang;
    }

    public TranslateLangItem(Parcel in) {
        this.mFromLang = in.readString();
        this.mToLang = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFromLang);
        dest.writeString(mToLang);
    }

    public static final Creator CREATOR = new Creator<TranslateLangItem>() {
        @Override
        public TranslateLangItem createFromParcel(Parcel source) {
            return new TranslateLangItem(source);
        }

        @Override
        public TranslateLangItem[] newArray(int size) {
            return new TranslateLangItem[size];
        }
    };

    public String getFromLang() {
        return mFromLang;
    }

    public String getToLang() {
        return mToLang;
    }

    public String getLangString() {
        return mFromLang + "-" + mToLang;
    }

    private static String[] splitLang(@NonNull final String lang) {
        return lang.split("-");
    }

}
