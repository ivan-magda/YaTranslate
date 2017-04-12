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
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ivanmagda.yatranslate.utils.TranslateLangItemUtils;

public final class TranslateLangItem implements Parcelable {

    private String mFromLang;
    private String mToLang;

    private String mFromLangName;
    private String mToLangName;

    public static TranslateLangItem defaultItem = new TranslateLangItem("en", "ru", "English", "Russian");

    public TranslateLangItem(@NonNull final String lang,
                             @NonNull final String fromLangName,
                             @NonNull final String toLangName) {
        this.mFromLang = TranslateLangItemUtils.getFromLangName(lang);
        this.mToLang = TranslateLangItemUtils.getToLangName(lang);
        this.mFromLangName = fromLangName;
        this.mToLangName = toLangName;
    }

    public TranslateLangItem(@NonNull final String fromLang, @NonNull final String toLang,
                             @NonNull final String fromLangName, @NonNull final String toLangName) {
        this.mFromLang = fromLang;
        this.mToLang = toLang;
        this.mFromLangName = fromLangName;
        this.mToLangName = toLangName;
    }

    public TranslateLangItem(Parcel in) {
        this.mFromLang = in.readString();
        this.mToLang = in.readString();
        this.mFromLangName = in.readString();
        this.mToLangName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFromLang);
        dest.writeString(mToLang);
        dest.writeString(mFromLangName);
        dest.writeString(mToLangName);
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

    public String getFromLangName() {
        return mFromLangName;
    }

    public String getToLangName() {
        return mToLangName;
    }

    public void setFromLang(String fromLang) {
        this.mFromLang = fromLang;
    }

    public void setToLang(String toLang) {
        this.mToLang = toLang;
    }

    public void setFromLangName(String fromLangName) {
        this.mFromLangName = fromLangName;
    }

    public void setToLangName(String toLangName) {
        this.mToLangName = toLangName;
    }

    public boolean isValid() {
        return !(TextUtils.isEmpty(mFromLang) || TextUtils.isEmpty(mToLang));
    }

    public void swap() {
        swapLangKeys();
        swapLangNames();
    }

    // Private Helpers.

    private void swapLangKeys() {
        String temp = mFromLang;
        mFromLang = mToLang;
        mToLang = temp;
    }

    private void swapLangNames() {
        String temp = mFromLangName;
        mFromLangName = mToLangName;
        mToLangName = temp;
    }
}
