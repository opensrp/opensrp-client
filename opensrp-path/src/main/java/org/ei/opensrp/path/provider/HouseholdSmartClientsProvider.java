package org.ei.opensrp.path.provider;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.HouseholdSmartRegisterActivity;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.fragment.HouseholdMemberAddFragment;
import org.ei.opensrp.path.fragment.HouseholdSmartRegisterFragment;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.view.LocationPickerView;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.DateUtils;
import util.ImageUtils;
import util.JsonFormUtils;
import util.VaccinateActionUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.ei.opensrp.path.fragment.HouseholdMemberAddFragment.startForm;
import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;
import static util.VaccinatorUtils.generateScheduleList;
import static util.VaccinatorUtils.nextVaccineDue;
import static util.VaccinatorUtils.receivedVaccines;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class HouseholdSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    VaccineRepository vaccineRepository;
    WeightRepository weightRepository;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private static final String VACCINES_FILE = "vaccines.json";
    String locationId = "";
    private HouseholdSmartRegisterFragment mBaseFragment;
    private static final int REQUEST_CODE_GET_JSON = 3432;

    public HouseholdSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                         AlertService alertService, VaccineRepository vaccineRepository, WeightRepository weightRepository,HouseholdSmartRegisterFragment mBaseFragment) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.vaccineRepository = vaccineRepository;
        this.weightRepository = weightRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mBaseFragment = mBaseFragment;

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
    }

    @Override
    public void getView(SmartRegisterClient client, final View convertView) {
        convertView.setLayoutParams(clientViewLayoutParams);
        final CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        fillValue((TextView) convertView.findViewById(R.id.householdheadname), getValue(pc.getColumnmaps(), "first_name", false));

        fillValue((TextView) convertView.findViewById(R.id.id), getValue(pc.getColumnmaps(), "HHID", false));
        fillValue((TextView) convertView.findViewById(R.id.registrationdate), getValue(pc.getColumnmaps(), "Date_Of_Reg", false));
//        fillValue((TextView) convertView.findViewById(R.id.address), getValue(pc.getColumnmaps(), "address1", false));
//        fillValue((TextView) convertView.findViewById(R.id.householdprimarytext), getValue(pc.getColumnmaps(), "block", false));

        ////////////////////////performance tweak address///////////////////////////////////////
        String address4 = "";
        try {
            JSONArray hie_facilities = new JSONArray(getValue(pc.getColumnmaps(), "hie_facilities", false));
            String fulladdress = hie_facilities.getString(hie_facilities.length()-1);
            fillValue((TextView) convertView.findViewById(R.id.householdprimarytext), fulladdress.split(":")[fulladdress.split(":").length-2]);
            fillValue((TextView) convertView.findViewById(R.id.housholdsecondarytext), fulladdress.split(":")[fulladdress.split(":").length-3]);
            fillValue((TextView) convertView.findViewById(R.id.address), fulladdress.split(":")[fulladdress.split(":").length-4]);
            address4 =  fulladdress;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /////////////////////commentout for performance tweak/////////////////////////
//        DetailsRepository detailsRepository;
//        detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
//        final Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
//        fillValue((TextView) convertView.findViewById(R.id.householdprimarytext), getValue(details, "address3", false).split(":")[getValue(details, "address3", false).split(":").length-1]);
//        fillValue((TextView) convertView.findViewById(R.id.housholdsecondarytext), getValue(details, "address2", false).split(":")[getValue(details, "address2", false).split(":").length-1]);
//        fillValue((TextView) convertView.findViewById(R.id.address), getValue(details, "address1", false).split(":")[getValue(details, "address1", false).split(":").length-1]);

        Button addmember = (Button)convertView.findViewById(R.id.add_member);
        LocationPickerView locationPickerView = ((HouseholdSmartRegisterFragment) mBaseFragment).getLocationPickerView();

        try {
//            locationId = JsonFormUtils.getOpenMrsLocationId(context(),getValue(details, "address4", false) );
            locationId = JsonFormUtils.getOpenMrsLocationId(context(),address4);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        convertView.findViewById(R.id.child_profile_info_layout).setTag(client);
        convertView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        ImageView profileImageIV = (ImageView) convertView.findViewById(R.id.profilepic);
        if (pc.entityId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, pc.entityId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.entityId(), OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, R.drawable.houshold_register_placeholder, R.drawable.houshold_register_placeholder));

        }

        final String finalAddress = address4;
        addmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = ((HouseholdSmartRegisterActivity)context).getFragmentManager().beginTransaction();
                android.app.Fragment prev = ((HouseholdSmartRegisterActivity)context).getFragmentManager().findFragmentByTag(HouseholdMemberAddFragment.DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                try {
                    locationId = JsonFormUtils.getOpenMrsLocationId(context(), finalAddress);
//                    locationId = JsonFormUtils.getOpenMrsLocationId(context(),getValue(details, "address4", false) );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HouseholdMemberAddFragment addmemberFragment = HouseholdMemberAddFragment.newInstance(context,locationId,pc.entityId(),context());
                    addmemberFragment.show(ft, HouseholdMemberAddFragment.DIALOG_TAG);
//

            }
        });

        ////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_household_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    public enum State {
        DUE,
        OVERDUE,
        UPCOMING_NEXT_7_DAYS,
        UPCOMING,
        INACTIVE,
        LOST_TO_FOLLOW_UP,
        EXPIRED,
        WAITING,
        NO_ALERT,
        FULLY_IMMUNIZED
    }
    protected org.ei.opensrp.Context context() {
        return org.ei.opensrp.Context.getInstance().updateApplicationContext(context);
    }
}