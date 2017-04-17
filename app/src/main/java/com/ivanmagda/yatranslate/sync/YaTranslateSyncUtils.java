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

package com.ivanmagda.yatranslate.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.ivanmagda.yatranslate.data.TranslateContract.LanguageEntry;

import java.util.concurrent.TimeUnit;

public final class YaTranslateSyncUtils {

    /**
     * Interval at which to sync with the languages.
     */
    private static final int SYNC_INTERVAL_HOURS = 24 * 7;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    /**
     * A boolean flag that mainly used as a safeguard to prevent calling the synchronize method
     * more than once.
     */
    private static boolean sInitialized;

    /**
     * A sync tag to identify sync job.
     */
    private static final String YA_TRANSLATE_SYNC_TAG = "ya-translate-sync";

    /**
     * Schedules a repeating sync of YaTranslate's languages data using FirebaseJobDispatcher.
     *
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher.
     */
    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync YaTranslate */
        Job syncJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync YaTranslate's data */
                .setService(YaTranslateFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(YA_TRANSLATE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * Set weather data to stay up to date.
                 */
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncJob);
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods.
     */
    synchronized public static void initialize(@NonNull final Context context) {
        /*
          Only perform initialization once per app lifetime. If initialization has already been
          performed, we have nothing to do in this method.
         */
        if (sInitialized) return;
        sInitialized = true;

        /*
          This method call triggers YaTranslate to create its task to synchronize supported
          languages data periodically.
         */
        scheduleFirebaseJobDispatcherSync(context);

        /*
          Check to see if our ContentProvider has data to display in our select languages
          list. However, performing a query on the main thread is a bad idea as this may
          cause UI to lag.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = LanguageEntry.CONTENT_URI;
                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        checkForEmpty.start();
    }

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    private static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, YaTranslateIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
