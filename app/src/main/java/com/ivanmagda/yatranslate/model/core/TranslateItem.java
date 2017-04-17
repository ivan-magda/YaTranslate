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

    public static final int ID_NOT_FOUND = -1;

    private long mId = ID_NOT_FOUND;
    private boolean mIsFavorite = false;

    private String mTextToTranslate;
    private String mTranslatedText;

    private TranslateLangItem mTranslateLangItem;

    public TranslateItem(long id, boolean isFavorite, String textToTranslate,
                         String translatedText, TranslateLangItem translateLangItem) {
        this.mId = id;
        this.mIsFavorite = isFavorite;
        this.mTextToTranslate = textToTranslate;
        this.mTranslatedText = translatedText;
        this.mTranslateLangItem = translateLangItem;
    }

    public TranslateItem(String textToTranslate, String translatedText, TranslateLangItem langItem) {
        this.mTextToTranslate = textToTranslate;
        this.mTranslatedText = translatedText;
        this.mTranslateLangItem = langItem;
    }

    public TranslateItem(Parcel in) {
        this.mId = in.readLong();
        this.mIsFavorite = in.readInt() != 0;
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
        dest.writeLong(mId);
        dest.writeInt(mIsFavorite ? 1 : 0);
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

    public long getId() {
        return mId;
    }

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public String getTextToTranslate() {
        return mTextToTranslate;
    }

    public String getTranslatedText() {
        return mTranslatedText;
    }

    public TranslateLangItem getTranslateLangItem() {
        return mTranslateLangItem;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.mIsFavorite = isFavorite;
    }

    public void setTextToTranslate(String textToTranslate) {
        this.mTextToTranslate = textToTranslate;
    }

    public void setTranslatedText(String translatedText) {
        this.mTranslatedText = translatedText;
    }

    public void setTranslateLangItem(TranslateLangItem translateLangItem) {
        this.mTranslateLangItem = translateLangItem;
    }

    public void toggleFavorite() {
        this.mIsFavorite = !mIsFavorite;
    }

    @Override
    public String toString() {
        return "TranslateItem{" +
                "id=" + mId +
                ", isFavorite=" + mIsFavorite +
                ", textToTranslate='" + mTextToTranslate + '\'' +
                ", translatedText='" + mTranslatedText + '\'' +
                ", translateLangItem=" + mTranslateLangItem +
                '}';
    }
}
