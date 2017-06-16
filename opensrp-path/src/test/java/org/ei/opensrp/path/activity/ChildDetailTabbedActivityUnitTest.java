package org.ei.opensrp.path.activity;


import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.repository.DetailsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by martin on 07/06/2017.
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = VaccinatorApplicationTestVersion.class, shadows = {CustomFontTextViewShadow.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({org.ei.opensrp.Context.class})
public class ChildDetailTabbedActivityUnitTest extends BaseUnitTest {


    // @Rule
    // public PowerMockRule rule = new PowerMockRule();

    @InjectMocks
    ChildDetailTabbedActivity activity;


    @Mock
    CommonPersonObjectClient childDetails;

    @Mock
    DetailsRepository detailsRepository;

    @Mock
    org.ei.opensrp.Context context_;

    ActivityController<ChildDetailTabbedActivity> controller;

    @Before
    public void setUp() {


        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivity.class);
        intent.putExtra("location_name", "Nairobi");

        controller = Robolectric.buildActivity(ChildDetailTabbedActivity.class, intent);
        activity = controller.get();

        initMocks(this);

        PowerMockito.mockStatic(VaccinatorApplication.class);

        PowerMockito.mockStatic(org.ei.opensrp.Context.class);


        Whitebox.setInternalState(org.ei.opensrp.Context.class, "context", context_);

        activity.detailsRepository = getDetailsRepository();
        controller.setup();
    }


    @After
    public void tearDown() {
        controller
                .pause()
                .stop()
                .destroy();
    }


    @Test
    public void shouldRenderAvatarImageView() {

        ImageView logoImageView = (ImageView) activity.findViewById(R.id.profile_image_iv);
        assertNotNull(logoImageView);

    }


    @Test
    public void shouldRenderEditIconImageView() {

        ImageView logoImageView = (ImageView) activity.findViewById(R.id.imageView2);
        assertNotNull(logoImageView);

    }

    @Test
    public void shouldRenderChildNameTextView() {

        TextView nameView = (TextView) activity.findViewById(R.id.name);
        assertNotNull(nameView);

    }


    @Test
    public void shouldRenderZierIDTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.idforclient);
        assertNotNull(textView);

    }


    @Test
    public void shouldRenderStatusImageView() {

        ImageView logoImageView = (ImageView) activity.findViewById(R.id.statusimage);
        assertNotNull(logoImageView);

    }

    @Test
    public void shouldRenderAgeForClientTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.ageforclient);
        assertNotNull(textView);

    }


    @Test
    public void shouldRenderStatusNameTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.statusname);
        assertNotNull(textView);

    }


    @Test
    public void shouldRenderStatusTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.status);
        assertNotNull(textView);

    }


    @Test
    @Ignore
    public void shouldRenderRegistrationDataTabTitle() {
       // activity.getViewPagerAdapter();
        Button button = (Button) activity.findViewById(R.id.login_loginButton);
        button.performClick();


    }

    @Test
    @Ignore
    public void shouldRenderUnderFiveHistoryTabTitle() {
        Button button = (Button) activity.findViewById(R.id.login_loginButton);
        button.performClick();


    }

    private DetailsRepository getDetailsRepository() {


        return new DetailsRepositoryLocal();
    }

    class DetailsRepositoryLocal extends DetailsRepository {
        @Override
        public Map<String, String> getAllDetailsForClient(String baseEntityId) {
            return Collections.emptyMap();
        }
    }


}
