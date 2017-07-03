package org.ei.opensrp.path.activity;


import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.repository.DetailsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Created by martin on 07/06/2017.
 */
@Config(constants = BuildConfig.class, sdk = 21,application = TestApplication.class)
@RunWith(RobolectricTestRunner.class)
public class ChildDetailTabbedActivityUnitTest2 {
    private ChildDetailTabbedActivity activity;
    @Mock
    private DetailsRepository detailsRepository;
    @Mock
    VaccineRepository vaccineRepository;
@Mock
    CommonPersonObjectClient childDetails;
    // @Rule


    @Before
    public void setUp() throws Exception {
        initMocks(this);

        activity = Robolectric.buildActivity(ChildDetailTabbedActivity.class)
                .get();

        List<Vaccine> vaccineList = new ArrayList<>();

       when(vaccineRepository.findByEntityId(anyString())).thenReturn(vaccineList);
       when(childDetails.entityId()).thenReturn("1234");
       // activity.setVaccineRepository(vaccineRepository);
       // activity.setCommonPersonObjectClient(childDetails);



    }

    public void tearDown() {
       /* controller
                .pause()
                .stop()
                .destroy();*/
    }
    @Test
    public void testOnCreateOptionsMenu(){
        PopupMenu p = new PopupMenu(activity, null);
        Menu menu = p.getMenu();
        activity.onCreateOptionsMenu(menu);
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
    public void shouldShowErrorPopupIfNoInputAfterButtonClick() {
        Button button = (Button) activity.findViewById(R.id.login_loginButton);
        button.performClick();


    }


}
