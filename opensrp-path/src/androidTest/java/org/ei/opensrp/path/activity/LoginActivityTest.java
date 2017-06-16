package org.ei.opensrp.path.activity;

import android.support.test.rule.ActivityTestRule;

import org.ei.opensrp.path.R;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;

public class LoginActivityTest {


    @Rule
    public ActivityTestRule<LoginActivity> rule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void shouldShowErrorPopupIfNoInputAfterButtonClick() {


        onView(withId(R.id.login_loginButton)).perform(click());
        onView(withText(R.string.login_failed_dialog_title))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));
    }


}