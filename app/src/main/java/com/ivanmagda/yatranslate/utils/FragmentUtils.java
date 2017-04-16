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

package com.ivanmagda.yatranslate.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class FragmentUtils {

    private static final float DEFAULT_ACTION_BAR_ELEVATION = 12.0f;

    private FragmentUtils() {
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);

        View currentFocusView = activity.getCurrentFocus();
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
        }
    }

    public static void restoreDefaultElevationForActionBar(@NonNull final FragmentActivity activity) {
        setElevationForActionBar(activity, DEFAULT_ACTION_BAR_ELEVATION);
    }

    public static void setElevationForActionBar(@NonNull final FragmentActivity activity,
                                                final float elevation) {
        ActionBar actionBar = getActionBar(activity);
        if (actionBar != null) {
            actionBar.setElevation(elevation);
        }
    }

    public static void setTitle(@NonNull final FragmentActivity fragmentActivity,
                                @NonNull final String title) {
        ActionBar actionBar = getActionBar(fragmentActivity);
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    public static void setTitle(@NonNull final FragmentActivity fragmentActivity,
                                final int stringResourceId) {
        ActionBar actionBar = getActionBar(fragmentActivity);
        if (actionBar != null) {
            actionBar.setTitle(stringResourceId);
        }
    }

    private static ActionBar getActionBar(@NonNull final FragmentActivity fragmentActivity) {
        if (fragmentActivity instanceof AppCompatActivity) {
            return ((AppCompatActivity) fragmentActivity).getSupportActionBar();
        }

        return null;
    }
}
