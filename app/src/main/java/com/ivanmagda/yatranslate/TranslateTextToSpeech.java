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

package com.ivanmagda.yatranslate;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ivanmagda.yatranslate.model.core.TranslateItem;
import com.ivanmagda.yatranslate.model.core.TranslateLangItem;

import java.util.Locale;

public final class TranslateTextToSpeech implements TextToSpeech.OnInitListener {

    private static final String LOG_TAG = TranslateTextToSpeech.class.getSimpleName();

    /**
     * The TextToSpeech engine.
     */
    private TextToSpeech mTextToSpeech;

    private boolean mIsOk = false;
    private boolean mIsInitialized = false;

    /**
     * Create an TranslateTextToSpeech instance.
     *
     * @param context The Context.
     */
    public TranslateTextToSpeech(@NonNull final Context context) {
        mTextToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        mIsInitialized = true;
        mIsOk = status != TextToSpeech.ERROR;
    }

    public void shutdown() {
        mTextToSpeech.stop();
        mTextToSpeech.shutdown();
    }

    public boolean isOk() {
        return mIsInitialized && mIsOk;
    }

    public void setLang(@NonNull final TranslateLangItem langItem) {
        int result = mTextToSpeech.setLanguage(new Locale(langItem.getToLang()));
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(LOG_TAG, "This language is not supported: " + langItem);
            mIsOk = false;
        } else {
            mIsOk = true;
        }
    }

    public void speak(@NonNull final TranslateItem translateItem) {
        if (!isOk()) return;

        final String REQUEST_ID = "speech-translated-text";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextToSpeech.speak(translateItem.getTranslatedText(), TextToSpeech.QUEUE_FLUSH, null,
                    REQUEST_ID);
        } else {
            mTextToSpeech.speak(translateItem.getTranslatedText(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
