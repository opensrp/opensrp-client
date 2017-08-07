package org.ei.opensrp.path.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.mocks.ChildImmunizationActivityTestVersion;
import org.ei.opensrp.path.activity.mocks.WomanImmunizationActivityTestVersion;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.DetailsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import shared.BaseUnitTest;
import shared.customshadows.ImageUtilsShadow;
import shared.customshadows.ImmunizationRowAdapterShadow;
import shared.customshadows.ImmunizationRowCardShadow;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * created by onadev on 07/06/2017.
 */
@PrepareForTest({org.ei.opensrp.Context.class})
@Config(shadows = {ImageUtilsShadow.class, ImmunizationRowAdapterShadow.class, ImmunizationRowCardShadow.class})
public class WomanImmunizationActivityUnitTest extends BaseUnitTest {

    @InjectMocks
    private WomanImmunizationActivityTestVersion activity;

    @Mock
    private CommonPersonObjectClient childDetails;

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private org.ei.opensrp.Context context_;
    private ActivityController<WomanImmunizationActivityTestVersion> controller;
    private Map<String, String> details;

    @Before
    public void setUp() {
        details = new HashMap<>();
        childDetails = new CommonPersonObjectClient("1234",details,"name");
        Intent intent = new Intent(RuntimeEnvironment.application, WomanImmunizationActivityTestVersion.class);
//        intent.putExtra("location_name", "Nairobi");
        controller = Robolectric.buildActivity(WomanImmunizationActivityTestVersion.class, intent);
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
        destroyController();
        details = null;
        activity = null;
        controller = null;
        context_ = null;
        detailsRepository = null;
        childDetails = null;

    }


    @Test
    public void shouldRenderAvatarImageView() {

        ImageView logoImageView = (ImageView) activity.findViewById(R.id.profile_image_iv);
        assertNotNull(logoImageView);

    }

    @Test
    public void shouldRenderChildNameTextView() {

        TextView nameView = (TextView) activity.findViewById(R.id.name_tv);
        assertNotNull(nameView);

    }


    @Test
    public void shouldRenderZierIDTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.child_id_tv);
        assertNotNull(textView);

    }


    @Test
    public void shouldRenderAgeForClientTextView() {

        TextView textView = (TextView) activity.findViewById(R.id.age_tv);
        assertNotNull(textView);

    }


    @Test
    public void onBackPressShouldFinishActivity() {
        activity.onBackPressed();
        assertTrue(activity.isFinishing());

    }





    @Test
    public void showWeightDialogShouldRender() {
        View recordWeight = activity.findViewById(R.id.record_weight);
        activity.showWeightDialog(recordWeight);
        assertNotNull(activity.getFragmentManager().findFragmentByTag(WomanImmunizationActivityTestVersion.DIALOG_TAG));

    }

    @Test
    public void clickingToolBarNavigationButtonClosesTheActivity() {
        LocationSwitcherToolbar toolbar = (LocationSwitcherToolbar) activity.findViewById(R.id.location_switching_toolbar);
        ArrayList<View> outViews = new ArrayList<>();
        toolbar.findViewsWithText(outViews, "NAVIGATE UP",
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

        assertFalse(outViews.isEmpty());
        assertTrue(outViews.size() == 1);//only one
        outViews.get(0).performClick();
        assertTrue(activity.isFinishing());


    }


    @Test

    public void onCreateSetsUpSuccessfullyWithSerializedChildDetails() {


        destroyController(); //destroy controller

        //Recreate and start controller with bundles this time

        Intent intent = new Intent(RuntimeEnvironment.application, WomanImmunizationActivityTestVersion.class);
//        intent.putExtra("location_name", "Nairobi");
        Bundle bundle = new Bundle();
        bundle.putSerializable(WomanImmunizationActivityTestVersion.EXTRA_CHILD_DETAILS, childDetails);
        intent.putExtras(bundle);


        controller = Robolectric.buildActivity(WomanImmunizationActivityTestVersion.class, intent);
        activity = controller.get();
        activity.detailsRepository = getDetailsRepository();
        controller.setup();

        //Certify started successfully by checking if at least one random element rendered
        TextView nameView = (TextView) activity.findViewById(R.id.name_tv);
        assertNotNull(nameView);
    }



    @Test
    public void onBackActivityShouldReturnChildSmartRegisterActivityClass() {

        assertNotNull(activity.getApplicationContext());
        assertTrue(activity.onBackActivity() == ChildSmartRegisterActivity.class);


    }



    private DetailsRepository getDetailsRepository() {


        return new DetailsRepositoryLocal();
    }

    class DetailsRepositoryLocal extends DetailsRepository {


        @Override
        public Map<String, String> getAllDetailsForClient(String baseEntityId) {
            return details;
        }
    }

    private void destroyController() {
        try {
            activity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
        }

        System.gc();
    }


}
