package org.ei.opensrp.view;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

import static org.ei.opensrp.util.Log.logVerbose;

public class LockingBackgroundTask {
    private ProgressIndicator indicator;
    private static final ReentrantLock lock = new ReentrantLock();

    public LockingBackgroundTask(ProgressIndicator progressIndicator) {
        this.indicator = progressIndicator;
    }

    public <T> void doActionInBackground(final BackgroundAction<T> backgroundAction) {
        Log.v(getClass().getName(), "Have started action in bg for LockingBackgroundTask "+backgroundAction);
        new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... params) {
                Log.v(getClass().getName(), "Params are "+params);

                if (!lock.tryLock()) {
                    logVerbose("Going away. Something else is holding the lock.");
                    cancel(true);
                    return null;
                }
                try {
                    Log.v(getClass().getName(), "Going to publich progress");

                    publishProgress();
                    return backgroundAction.actionToDoInBackgroundThread();
                } finally {
                    lock.unlock();
                }
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                indicator.setVisible();
            }

            @Override
            protected void onPostExecute(T result) {
                backgroundAction.postExecuteInUIThread(result);
                indicator.setInvisible();
            }
        }.execute((Void) null);
    }
}
