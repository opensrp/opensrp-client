package org.ei.opensrp.path.tabfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildDetailTabbedActivity;
import org.ei.opensrp.path.viewComponents.WidgetFactory;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import util.DateUtils;
import util.JsonFormUtils;
import util.PathConstants;
import util.Utils;


public class ChildRegistrationDataFragment extends Fragment {
//    public Map<String, String> detailsMap;
    private LayoutInflater inflater;
    private ViewGroup container;
    private LinearLayout layout;
    public CommonPersonObjectClient childDetails;
    private View fragmentView;

    public static ChildRegistrationDataFragment newInstance(Bundle bundle) {
        Bundle args = bundle;
        ChildRegistrationDataFragment fragment = new ChildRegistrationDataFragment();
        if (args == null) {
            args = new Bundle();
        }
        fragment.setArguments(args);
        return fragment;
    }

    public ChildRegistrationDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.getArguments() != null) {
            Serializable serializable = getArguments().getSerializable(ChildDetailTabbedActivity.EXTRA_CHILD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                childDetails = (CommonPersonObjectClient) serializable;
            }
        }
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.child_registration_data_fragment_tweak, container, false);
        return fragmentView;
    }



    public void loadData(Map<String, String> detailsMap) {
        if (fragmentView != null) {

            CustomFontTextView tvChildsHomeHealthFacility = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_home_health_facility);
            CustomFontTextView tvChildsRegisterCardNumber = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_register_card_number);
            CustomFontTextView tvChildsBirthCertificateNumber = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_birth_certificate_number);
            CustomFontTextView tvChildsFirstName = (CustomFontTextView) fragmentView.findViewById(R.id.value_first_name);
            CustomFontTextView tvChildsSex = (CustomFontTextView) fragmentView.findViewById(R.id.value_sex);
            CustomFontTextView tvChildsDOB = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_dob);
            CustomFontTextView tvChildsAge = (CustomFontTextView) fragmentView.findViewById(R.id.value_age);
            CustomFontTextView tvChildsBirthWeight = (CustomFontTextView) fragmentView.findViewById(R.id.value_birth_weight);
            CustomFontTextView tvMotherFirstName = (CustomFontTextView) fragmentView.findViewById(R.id.value_mother_guardian_first_name);
            CustomFontTextView tvMotherPhoneNumber = (CustomFontTextView) fragmentView.findViewById(R.id.value_mother_guardian_phone_number);
            CustomFontTextView tvChildsPlaceOfBirth = (CustomFontTextView) fragmentView.findViewById(R.id.value_place_of_birth);
            CustomFontTextView tvChildsBirthHealthFacility = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_birth_health_facility);
            CustomFontTextView tvChildsOtherBirthFacility = (CustomFontTextView) fragmentView.findViewById(R.id.value_other_birth_facility);
            CustomFontTextView tvChildsResidentialArea = (CustomFontTextView) fragmentView.findViewById(R.id.value_childs_residential_area);

            TableRow tableRowChildsOtherBirthFacility = (TableRow) fragmentView.findViewById(R.id.tableRow_childRegDataFragment_childsOtherBirthFacility);

            Map<String, String> childDetailsColumnMaps = detailsMap;

            tvChildsHomeHealthFacility.setText(JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "Home_Facility", false))));
            tvChildsRegisterCardNumber.setText(Utils.getValue(detailsMap, "Child_Register_Card_Number", false));
            tvChildsBirthCertificateNumber.setText(Utils.getValue(detailsMap, "Child_Birth_Certificate", false));
            tvChildsFirstName.setText(Utils.getValue(childDetailsColumnMaps, "first_name", true));
            tvChildsSex.setText(Utils.getValue(childDetailsColumnMaps, "gender", true));
            boolean containsDOB = Utils.getValue(childDetails.getColumnmaps(), "dob", true).isEmpty();
            String childsDateOfBirth = !containsDOB ? ChildDetailTabbedActivity.DATE_FORMAT.format(new DateTime(Utils.getValue(childDetails.getColumnmaps(), "dob", true)).toDate()) : "";
            tvChildsDOB.setText(childsDateOfBirth);
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

            tvChildsAge.setText(formattedAge);

            tvChildsBirthWeight.setText(Utils.kgStringSuffix(Utils.getValue(detailsMap, "Birth_Weight", true)));
            tvMotherFirstName.setText(Utils.getValue(childDetailsColumnMaps, "mother_first_name", true).isEmpty() ? Utils.getValue(childDetails.getDetails(), "mother_first_name", true) : Utils.getValue(childDetailsColumnMaps, "mother_first_name", true));


            tvMotherPhoneNumber.setText(Utils.getValue(detailsMap, "Mother_Guardian_Number", true));

            String placeOfBirthChoice = Utils.getValue(detailsMap, "Place_Birth", true);
            if (placeOfBirthChoice.equalsIgnoreCase("1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeOfBirthChoice = "Health facility";
            }

            if (placeOfBirthChoice.equalsIgnoreCase("1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeOfBirthChoice = "Home";
            }

            tvChildsPlaceOfBirth.setText(placeOfBirthChoice);
            String childsBirthHealthFacility = Utils.getValue(detailsMap, "Birth_Facility_Name", false);
            tvChildsBirthHealthFacility.setText(JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "Birth_Facility_Name", false))));

            if (JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(
                    Context.getInstance(), Utils.getValue(detailsMap, "Birth_Facility_Name",
                            false))).equalsIgnoreCase("other")) {
                tableRowChildsOtherBirthFacility.setVisibility(View.VISIBLE);
                tvChildsOtherBirthFacility.setText(Utils.getValue(detailsMap, "Birth_Facility_Name_Other", true));
            }


            tvChildsResidentialArea.setText(JsonFormUtils.getOpenMrsReadableName(JsonFormUtils.getOpenMrsLocationName(Context.getInstance(), Utils.getValue(detailsMap, "address3", false))));



        }
    }
}
