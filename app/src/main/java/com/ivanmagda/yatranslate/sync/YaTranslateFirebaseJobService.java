package com.ivanmagda.yatranslate.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class YaTranslateFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchSupportedLangsTask;

    /**
     * The entry point to our Job. Implementations offloads work to another thread of
     * execution as soon as possible.
     * <p>
     * This is called by the Job Dispatcher to tell us we should start our job. This
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchSupportedLangsTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Context context = getApplicationContext();
                YaTranslateSyncTask.syncLanguages(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchSupportedLangsTask.execute();

        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchSupportedLangsTask != null) {
            mFetchSupportedLangsTask.cancel(true);
        }

        return true;
    }
}
