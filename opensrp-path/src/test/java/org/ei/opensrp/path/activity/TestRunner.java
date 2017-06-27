package org.ei.opensrp.path.activity;

import android.app.Application;

import org.junit.runners.model.InitializationError;
import org.robolectric.DefaultTestLifecycle;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycle;

import java.lang.reflect.Method;

/**
 * Created by coder on 6/15/17.
 */
public class TestRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
    @Override
    protected Class<? extends TestLifecycle> getTestLifecycleClass() {
        return MyTestLifecycle.class;
    }

    public static class MyTestLifecycle extends DefaultTestLifecycle {

        public Application createApplication(final Method method, final org.robolectric.manifest.AndroidManifest appManifest) {
            // run tests under our TestApplication
            return new TestApplication();
        }
    }
}
