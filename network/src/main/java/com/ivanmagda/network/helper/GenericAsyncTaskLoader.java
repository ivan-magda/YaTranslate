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

package com.ivanmagda.network.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.ivanmagda.network.core.Resource;
import com.ivanmagda.network.core.Webservice;

public final class GenericAsyncTaskLoader<T> extends AsyncTaskLoader<T> {

    /**
     * Helper interface, that controls whether to start loading.
     */
    public interface OnStartLoadingCondition {
        boolean isMeetConditions(Resource<?> resource);
    }

    private final Resource<T> mResource;
    private final OnStartLoadingCondition mLoadingCondition;

    public GenericAsyncTaskLoader(@NonNull final Context context,
                                  @NonNull final Resource<T> resource,
                                  @NonNull final OnStartLoadingCondition loadingCondition) {
        super(context);
        this.mResource = resource;
        this.mLoadingCondition = loadingCondition;
    }

    @Override
    protected void onStartLoading() {
        if (mLoadingCondition.isMeetConditions(mResource)) {
            forceLoad();
        }
    }

    @Override
    public T loadInBackground() {
        return Webservice.load(mResource);
    }
}
