package org.ei.opensrp.path.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.mocks.ChildDetailTabbedActivityTestVersion;
import org.ei.opensrp.path.activity.mocks.ChildImmunizationActivityTestVersion;
import org.ei.opensrp.path.activity.mocks.MenuItemTestVersion;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.toolbar.ChildDetailsToolbar;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.util.EasyMap;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import shared.BaseUnitTest;
import shared.customshadows.ImageUtilsShadow;
import shared.customshadows.ImmunizationRowAdapterShadow;
import shared.customshadows.ImmunizationRowCardShadow;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * created by onadev on 07/06/2017.
 */
@PrepareForTest({org.ei.opensrp.Context.class})
@Config(shadows = {ImageUtilsShadow.class, ImmunizationRowAdapterShadow.class, ImmunizationRowCardShadow.class})
public class ChildImmunizationActivityUnitTest extends BaseUnitTest {

    @InjectMocks
    private ChildImmunizationActivityTestVersion activity;

    @Mock
    private CommonPersonObjectClient childDetails;

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private org.ei.opensrp.Context context_;
    private ActivityController<ChildImmunizationActivityTestVersion> controller;
    private Map<String, String> details;

    @Before
    public void setUp() {
        details = new HashMap<>();
        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivityTestVersion.class);
        intent.putExtra("location_name", "Nairobi");
        controller = Robolectric.buildActivity(ChildImmunizationActivityTestVersion.class, intent);
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
    public void shouldDisplayOnOptionsMenuCaseRegistrationData() {
        MenuItemTestVersion menuItem = new MenuItemTestVersion();
        menuItem.setItemId(R.id.registration_data);
        activity.onOptionsItemSelected(menuItem);
        ArrayList<View> outViews = new ArrayList<>();

        //Validate correct form view loaded by validating various fields




    }








    public void onBackPressShouldFinishActivity() {
        activity.onBackPressed();
        assertTrue(activity.isFinishing());

    }



//    @Test
//    public void showWeightDialogShouldRender() {
//
//        activity.showWeightDialog(0);
//        assertNotNull(activity.getFragmentManager().findFragmentByTag(ChildDetailTabbedActivity.DIALOG_TAG));
//
//    }

    @Test
    public void clickingToolBarNavigationButtonClosesTheActivity() {
        ChildDetailsToolbar toolbar = (ChildDetailsToolbar) activity.findViewById(R.id.child_detail_toolbar);
        ArrayList<View> outViews = new ArrayList<>();
        toolbar.findViewsWithText(outViews, "NAVIGATE UP",
                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

        assertFalse(outViews.isEmpty());
        assertTrue(outViews.size() == 1);//only one
        outViews.get(0).performClick();
        assertTrue(activity.isFinishing());


    }


    @Test
    public void shouldRenderStatusFragmentOnStatusViewClick() {

        LinearLayout statusView = (LinearLayout) activity.findViewById(R.id.statusview);
        assertNotNull(statusView);
        statusView.performClick();

        ArrayList<View> outViews = new ArrayList<>();
        View view = activity.getFragmentManager().findFragmentByTag(ChildDetailTabbedActivityTestVersion.DIALOG_TAG).getView();
        assertNotNull(view);//make sure view exists
        view.findViewsWithText(outViews, "Child Status",
                View.FIND_VIEWS_WITH_TEXT);

        assertFalse(outViews.isEmpty());
        assertTrue(outViews.size() == 1);//only one
        assertTrue(outViews.get(0).getVisibility() == View.VISIBLE);
    }


    @Test

    public void onCreateSetsUpSuccessfullyWithSerializedChildDetails() {


        destroyController(); //destroy controller

        //Recreate and start controller with bundles this time

        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivityTestVersion.class);
        intent.putExtra("location_name", "Nairobi");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChildDetailTabbedActivityTestVersion.EXTRA_CHILD_DETAILS, childDetails);
        intent.putExtras(bundle);


        controller = Robolectric.buildActivity(ChildImmunizationActivityTestVersion.class, intent);
        activity = controller.get();
        activity.detailsRepository = getDetailsRepository();
        controller.setup();

        //Certify started successfully by checking if at least one random element rendered
        TextView nameView = (TextView) activity.findViewById(R.id.name);
        assertNotNull(nameView);
    }


    @Test

    public void statusViewShouldUpdateToInactiveIfChildDetailsInactiveParamIsSetToTrue() {

        destroyController(); //destroy controller

        //Recreate and start controller with bundles this time

        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivityTestVersion.class);
        intent.putExtra("location_name", "Nairobi");
        Bundle bundle = new Bundle();
        CommonPersonObjectClient newChildDetails = new CommonPersonObjectClient("1", new HashMap<String, String>(), "test");
        Map<String, String> columnMap = EasyMap.create(ChildDetailTabbedActivityTestVersion.inactive, "true").map();
        newChildDetails.setColumnmaps(columnMap);
        newChildDetails.setDetails(columnMap);
        details = columnMap;//save for later call to getAllDetailsForClient method
        bundle.putSerializable(ChildDetailTabbedActivityTestVersion.EXTRA_CHILD_DETAILS, newChildDetails);
        intent.putExtras(bundle);


        controller = Robolectric.buildActivity(ChildImmunizationActivityTestVersion.class, intent);
        activity = controller.get();

        activity.detailsRepository = getDetailsRepository();
        controller.setup();
        LinearLayout statusView = (LinearLayout) activity.findViewById(R.id.statusview);
        assertNotNull(statusView);

        TextView statusTextView = (TextView) statusView.findViewById(R.id.statusname);
        assertTrue(statusTextView.getVisibility() == View.VISIBLE);
        assertEquals("Inactive", statusTextView.getText().toString());

    }


    @Test
    public void onBackActivityShouldReturnChildSmartRegisterActivityClass() {

        assertNotNull(activity.getApplicationContext());
        assertTrue(activity.onBackActivity() == ChildSmartRegisterActivity.class);


    }


    @Test

    public void statusViewShouldUpdateToActiveifChildStatusParamListIsEmpty() {

        destroyController(); //destroy controller

        //Recreate and start controller with bundles this time

        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivityTestVersion.class);
        intent.putExtra("location_name", "Nairobi");
        Bundle bundle = new Bundle();
        CommonPersonObjectClient newChildDetails = new CommonPersonObjectClient("1", new HashMap<String, String>(), "test");
        newChildDetails.setColumnmaps(Collections.EMPTY_MAP);
        newChildDetails.setDetails(Collections.EMPTY_MAP);
        bundle.putSerializable(ChildDetailTabbedActivityTestVersion.EXTRA_CHILD_DETAILS, newChildDetails);
        intent.putExtras(bundle);


        controller = Robolectric.buildActivity(ChildImmunizationActivityTestVersion.class, intent);
        activity = controller.get();
        activity.detailsRepository = getDetailsRepository();
        controller.setup();

        LinearLayout statusView = (LinearLayout) activity.findViewById(R.id.statusview);
        assertNotNull(statusView);

        TextView statusTextView = (TextView) statusView.findViewById(R.id.statusname);
        assertTrue(statusTextView.getVisibility() == View.VISIBLE);
        assertEquals("Active", statusTextView.getText().toString());


    }

    @Test

    public void statusViewShouldUpdateToLostToFollowUpWhenChildStatusLostToFollowUpParamIsTrue() {

        destroyController(); //destroy controller

        //Recreate and start controller with bundles this time

        Intent intent = new Intent(RuntimeEnvironment.application, ChildDetailTabbedActivityTestVersion.class);
        intent.putExtra("location_name", "Nairobi");
        Bundle bundle = new Bundle();
        CommonPersonObjectClient newChildDetails = new CommonPersonObjectClient("1", new HashMap<String, String>(), "test");
        Map<String, String> columnMap = EasyMap.create(ChildDetailTabbedActivityTestVersion.lostToFollowUp, "true").map();
        newChildDetails.setColumnmaps(columnMap);
        newChildDetails.setDetails(columnMap);
        details = columnMap;//save for later call to getAllDetailsForClient method
        bundle.putSerializable(ChildDetailTabbedActivityTestVersion.EXTRA_CHILD_DETAILS, newChildDetails);
        intent.putExtras(bundle);


        controller = Robolectric.buildActivity(ChildImmunizationActivityTestVersion.class, intent);
        activity = controller.get();

        activity.detailsRepository = getDetailsRepository();
        controller.setup();
        LinearLayout statusView = (LinearLayout) activity.findViewById(R.id.statusview);
        assertNotNull(statusView);

        TextView statusTextView = (TextView) statusView.findViewById(R.id.status);
        assertTrue(statusTextView.getVisibility() == View.VISIBLE);
        assertEquals("Lost to\nFollow-Up", statusTextView.getText().toString());

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
