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

package com.ivanmagda.yatranslate.model.core;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Defines Yandex Translate item.
 */
public final class TranslateItem implements Parcelable {

    private final String mTextToTranslate;
    private final String mTranslatedText;

    private final TranslateLangItem mTranslateLangItem;

    public TranslateItem(String textToTranslate, String translatedText, TranslateLangItem langItem) {
        this.mTextToTranslate = textToTranslate;
        this.mTranslatedText = translatedText;
        this.mTranslateLangItem = langItem;
    }

    public TranslateItem(Parcel in) {
        this.mTextToTranslate = in.readString();
        this.mTranslatedText = in.readString();
        this.mTranslateLangItem = in.readParcelable(TranslateLangItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTextToTranslate);
        dest.writeString(mTranslatedText);
        dest.writeParcelable(mTranslateLangItem, flags);
    }

    public static final Creator<TranslateItem> CREATOR = new Creator<TranslateItem>() {
        @Override
        public TranslateItem createFromParcel(Parcel source) {
            return new TranslateItem(source);
        }

        @Override
        public TranslateItem[] newArray(int size) {
            return new TranslateItem[size];
        }
    };

    public String getTextToTranslate() {
        return mTextToTranslate;
    }

    public String getTranslatedText() {
        return mTranslatedText;
    }

    public TranslateLangItem getTranslateLangItem() {
        return mTranslateLangItem;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TranslateItem)) return false;

        TranslateItem translateItem = (TranslateItem) obj;
        TranslateLangItem translateLang = translateItem.getTranslateLangItem();

        return translateItem.getTextToTranslate().equals(mTextToTranslate) &&
                translateItem.getTranslatedText().equals(mTranslatedText) &&
                translateLang.getFromLang().equals(mTranslateLangItem.getFromLang()) &&
                translateLang.getToLang().equals(mTranslateLangItem.getToLang());
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(mTextToTranslate, mTranslatedText,
                    mTranslateLangItem.getFromLang(), mTranslateLangItem.getToLang());
        } else {
            int result = 17;

            result = 31 * result + mTextToTranslate.hashCode();
            result = 31 * result + mTranslatedText.hashCode();
            result = 31 * result + mTranslateLangItem.getFromLang().hashCode();
            result = 31 * result + mTranslateLangItem.getToLang().hashCode();

            return result;
        }
    }

    @Override
    public String toString() {
        return "TranslateItem{" +
                "text to translate='" + mTextToTranslate + '\'' +
                ", translated text='" + mTranslatedText + '\'' +
                ", source language='" + mTranslateLangItem.getFromLang() + '\'' +
                ", destination language='" + mTranslateLangItem.getToLang() + '\'' +
                '}';
    }
}
