package org.ei.opensrp.path.activity;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


/**
 * Created by martin on 07/06/2017.
 */
public class LoginActivityUnitTest extends BaseUnitTest {
    LoginActivity activity;


    @Before
    public void setUp() {

        activity = Robolectric.setupActivity(LoginActivity.class);
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void usernameTextFieldShouldContainCorrectTextAfterInput() {

        EditText usernameText = (EditText) activity.findViewById(R.id.login_userNameText);
        assertNotNull(usernameText);

        usernameText.setText("onadevuser");
        assertEquals("onadevuser", usernameText.getText().toString());

    }


    @Test
    @Ignore
    public void passwordTextFieldShouldContainCorrectTextAfterInput() {
        EditText passwordText = (EditText) activity.findViewById(R.id.login_passwordText);
        assertNotNull(passwordText);

        passwordText.setText("onadevuserpass");
        assertEquals("onadevuserpass", passwordText.getText().toString());

    }

    @Test
    @Ignore
    public void theApplicationLogoShouldRenderCorrectlyOnLoad() {

        LinearLayout logoImageViewCanvas = (LinearLayout) activity.findViewById(R.id.logoCanvasLL);
        assertNotNull(logoImageViewCanvas);


        ImageView logoImageView = (ImageView) activity.findViewById(R.id.logoImage);
        assertNotNull(logoImageView);
    }




}
