package org.ei.opensrp.path.activity;


import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Photo;
import org.ei.opensrp.repository.DetailsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.Map;

import util.ImageUtils;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Created by martin on 07/06/2017.
 */

@Config(constants = BuildConfig.class, application = VaccinatorApplicationTestVersion.class, shadows = {CustomFontTextViewShadow.class})
@PrepareForTest({org.ei.opensrp.Context.class, ImageUtils.class})
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
    public void shouldRenderRegistrationDataTabTitle() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Age",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderUnderFiveHistoryTabTitle() {


    }

    @Test
    public void shouldRenderChildsHomeHealthFacilityRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s home health facility",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderChildsZeirIdRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s ZEIR ID",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderChildsRegisterCardNumberRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s register card number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderChildBirthCertificateNumberRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s birth certificate number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderFirstNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "First Name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderLastNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Last Name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderSexRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Sex",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderChildsDobRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s DOB",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderAgeRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Age",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderDateFirstSeenRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Date first seen",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderBirthWeightRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Birth Weight",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianFirstNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Mother/guardian first name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianLastNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Mother/guardian last name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianDOBRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Mother/guardian DOB",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianNRCNumberRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Mother/guardian NRC number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianPhoneNumberRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Mother/guardian phone number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianFullNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Father/guardian full name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderFatherGuardianNRCnumberRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Father/guardian NRC number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderPlaceOfBirtRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Place of birth",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderMotherGuardianHealthFacilityRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Health facility the child was born in",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderChildsResidentialAreaRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Child\'s residential area",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderHomeAddressRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Home Address",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderLandmarkRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "Landmark",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderCHWNameRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "CHW name",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderCHWphoneNumber() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "CHW phone number",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    public void shouldRenderHIVexposureRow() {
        final ArrayList<View> outViews = new ArrayList<>();
        activity.getViewPagerAdapter().getItem(0).getView().findViewById(R.id.rowholder).findViewsWithText(outViews, "HIV exposure",
                View.FIND_VIEWS_WITH_TEXT);
        assertTrue(!outViews.isEmpty());

    }

    @Test
    @Ignore
    public void shouldDisplayOnOptionsMenu() {
        //MenuItem menuItem = shadowOf(activity).getOptionsMenu().findItem(R.id.registration_data);
        //activity.onOptionsItemSelected(menuItem);
    }


    @Test
    public void getChildDetailsMethodShouldNotReturnNull() {

        assertNotNull(activity.getChildDetails());

    }

    @Test
    public void getViewPagerAdapterShouldNotReturnNull() {

        assertNotNull(activity.getViewPagerAdapter());

    }

    @Test
    public void getViewPagerAdapterShouldHaveTwoFragments() {

        assertNotNull(activity.getViewPagerAdapter().getItem(0));
        assertNotNull(activity.getViewPagerAdapter().getItem(1));

    }


    @Test
    public void getDetailsRepositoryShouldNotReturnNull() {

        assertNotNull(activity.getDetailsRepository());

    }

    @Test
    public void onReturnSelectItemOptionsShouldReturnTrue() {
        assertTrue(activity.onPrepareOptionsMenu(null));

    }

    @Test
    public void onBackPressShouldFinishActivity() {
        activity.onBackPressed();
        assertTrue(activity.isFinishing());

    }

    @Test
    public void getVaccinatorApplicationInstanceShouldNotReturnNull() {

        assertNotNull(activity.getVaccinatorApplicationInstance());

    }

    @Test
    public void showWeightDialogShouldRender() {

        PowerMockito.mockStatic(ImageUtils.class);

        PowerMockito.doReturn(new Photo()).when(ImageUtils.profilePhotoByClient(any(CommonPersonObjectClient.class)));
        activity.showWeightDialog(0);
        assertNotNull(activity.getFragmentManager());

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
