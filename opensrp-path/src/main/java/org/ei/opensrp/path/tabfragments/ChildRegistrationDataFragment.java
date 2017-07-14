package org.ei.opensrp.path.tabfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildDetailTabbedActivity;
import org.ei.opensrp.path.viewComponents.WidgetFactory;
import org.ei.opensrp.repository.DetailsRepository;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import util.DateUtils;
import util.JsonFormUtils;
import util.Utils;


public class ChildRegistrationDataFragment extends Fragment {
    public CommonPersonObjectClient childDetails;
    public Map<String, String> detailsMap;
    private LayoutInflater inflater;
    private ViewGroup container;
    private LinearLayout layout;

    public ChildRegistrationDataFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = this.getArguments();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (this.getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(ChildDetailTabbedActivity.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = (CommonPersonObjectClient) serializable;
            }
        }
        View fragmentview = inflater.inflate(R.layout.child_registration_data_fragment, container, false);
        LinearLayout layout = (LinearLayout) fragmentview.findViewById(R.id.rowholder);
        this.inflater = inflater;
        this.container = container;
        this.layout = layout;

//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));
//        layout.addView(createTableRow(inflater,container,"Catchment Area","Linda"));


        // Inflate the layout for this fragment
        return fragmentview;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void loadData() {
        if (layout != null && container != null && inflater != null) {
            if (layout.getChildCount() > 0) {
                layout.removeAllViews();
            }

            DetailsRepository detailsRepository = ((ChildDetailTabbedActivity) getActivity()).getDetailsRepository();
            childDetails = childDetails != null ? childDetails : ((ChildDetailTabbedActivity) getActivity()).getChildDetails();
            detailsMap = detailsRepository.getAllDetailsForClient(childDetails.entityId());

//        detailsMap = childDetails.getColumnmaps();
            WidgetFactory wd = new WidgetFactory();

            layout.addView(wd.createTableRow(inflater, container, "Child's home health facility", JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "Home_Facility", false)))));
            layout.addView(wd.createTableRow(inflater, container, "Child's ZEIR ID", Utils.getValue(childDetails.getColumnmaps(), "zeir_id", false)));
            layout.addView(wd.createTableRow(inflater, container, "Child's register card number", Utils.getValue(detailsMap, "Child_Register_Card_Number", false)));
            layout.addView(wd.createTableRow(inflater, container, "Child's birth certificate number", Utils.getValue(detailsMap, "Child_Birth_Certificate", false)));
            layout.addView(wd.createTableRow(inflater, container, "First name", Utils.getValue(childDetails.getColumnmaps(), "first_name", true)));
            layout.addView(wd.createTableRow(inflater, container, "Last name", Utils.getValue(childDetails.getColumnmaps(), "last_name", true)));
            layout.addView(wd.createTableRow(inflater, container, "Sex", Utils.getValue(childDetails.getColumnmaps(), "gender", true)));
            boolean containsDOB = Utils.getValue(childDetails.getColumnmaps(), "dob", true).isEmpty();
            String childsDateOfBirth = !containsDOB ? ChildDetailTabbedActivity.DATE_FORMAT.format(new DateTime(Utils.getValue(childDetails.getColumnmaps(), "dob", true)).toDate()) : "";
            layout.addView(wd.createTableRow(inflater, container, "Child's DOB", childsDateOfBirth));


            String formattedAge = "";
            String dobString = Utils.getValue(childDetails.getColumnmaps(), "dob", false);
            if (!TextUtils.isEmpty(dobString)) {
                DateTime dateTime = new DateTime(dobString);
                Date dob = dateTime.toDate();
                long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

                if (timeDiff >= 0) {
                    formattedAge = DateUtils.getDuration(timeDiff);
                }
            }


            layout.addView(wd.createTableRow(inflater, container, "Age", formattedAge));

            String dateString = Utils.getValue(detailsMap, "First_Health_Facility_Contact", false);
            if (!TextUtils.isEmpty(dateString)) {
                Date date = JsonFormUtils.formatDate(dateString, false);
                if (date != null) {
                    dateString = ChildDetailTabbedActivity.DATE_FORMAT.format(date);
                }
            }
            layout.addView(wd.createTableRow(inflater, container, "Date first seen", dateString));
            layout.addView(wd.createTableRow(inflater, container, "Birth weight", Utils.kgStringSuffix(Utils.getValue(detailsMap, "Birth_Weight", true))));

            layout.addView(wd.createTableRow(inflater, container, "Mother/guardian first name", (Utils.getValue(childDetails.getColumnmaps(), "mother_first_name", true).isEmpty() ? Utils.getValue(childDetails.getDetails(), "mother_first_name", true) : Utils.getValue(childDetails.getColumnmaps(), "mother_first_name", true))));
            layout.addView(wd.createTableRow(inflater, container, "Mother/guardian last name", (Utils.getValue(childDetails.getColumnmaps(), "mother_last_name", true).isEmpty() ? Utils.getValue(childDetails.getDetails(), "mother_last_name", true) : Utils.getValue(childDetails.getColumnmaps(), "mother_last_name", true))));
            String motherDob = Utils.getValue(childDetails, "mother_dob", true);

            try {
                DateTime dateTime = new DateTime(motherDob);
                Date mother_dob = dateTime.toDate();
                motherDob = ChildDetailTabbedActivity.DATE_FORMAT.format(mother_dob);
            } catch (Exception e) {

            }

            // If default mother dob ... set it as blank
            if (motherDob != null && motherDob.equals(JsonFormUtils.MOTHER_DEFAULT_DOB)) {
                motherDob = "";
            }

            layout.addView(wd.createTableRow(inflater, container, "Mother/guardian DOB", motherDob));

            layout.addView(wd.createTableRow(inflater, container, "Mother/guardian NRC number", Utils.getValue(childDetails, "mother_nrc_number", true)));
            layout.addView(wd.createTableRow(inflater, container, "Mother/guardian phone number", Utils.getValue(detailsMap, "Mother_Guardian_Number", true)));
            layout.addView(wd.createTableRow(inflater, container, "Father/guardian full name", Utils.getValue(detailsMap, "Father_Guardian_Name", true)));
            layout.addView(wd.createTableRow(inflater, container, "Father/guardian NRC number", Utils.getValue(detailsMap, "Father_NRC_Number", true)));

            String placeofnearth_Choice = Utils.getValue(detailsMap, "Place_Birth", true);
            if (placeofnearth_Choice.equalsIgnoreCase("1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeofnearth_Choice = "Health facility";
            }
            if (placeofnearth_Choice.equalsIgnoreCase("1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeofnearth_Choice = "Home";
            }
            layout.addView(wd.createTableRow(inflater, container, "Place of birth", placeofnearth_Choice));
            layout.addView(wd.createTableRow(inflater, container, "Health facility the child was born in", JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "Birth_Facility_Name", false)))));
            if (JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(
                    Context.getInstance(), Utils.getValue(detailsMap, "Birth_Facility_Name",
                            false))).equalsIgnoreCase("other")) {
                layout.addView(wd.createTableRow(inflater, container, "Other birth facility", Utils.getValue(detailsMap, "Birth_Facility_Name_Other", true)));
            }
            layout.addView(wd.createTableRow(inflater, container, "Child's residential area", JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "address3", false)))));
            if (JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(
                    Context.getInstance(),
                    Utils.getValue(detailsMap, "address3", true))).equalsIgnoreCase("other")) {
                layout.addView(wd.createTableRow(inflater, container, "Other residential area", Utils.getValue(detailsMap, "address5", true)));
            }
            layout.addView(wd.createTableRow(inflater, container, "Home address", Utils.getValue(detailsMap, "address2", true)));

            layout.addView(wd.createTableRow(inflater, container, "Landmark", Utils.getValue(detailsMap, "address1", true)));
            layout.addView(wd.createTableRow(inflater, container, "CHW name", Utils.getValue(detailsMap, "CHW_Name", true)));
            layout.addView(wd.createTableRow(inflater, container, "CHW phone number", Utils.getValue(detailsMap, "CHW_Phone_Number", true)));
            layout.addView(wd.createTableRow(inflater, container, "HIV exposure", Utils.getValue(childDetails.getColumnmaps(), "pmtct_status", true)));
        }
    }
}
