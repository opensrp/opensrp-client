package com.example.opensrp_anc.anc;

import android.widget.TableLayout;
import android.widget.TextView;

import com.example.opensrp_anc.R;

import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.core.utils.Utils;

import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.getValue;

/**
 * Created by Owais on 3/31/2017.
 */
public class ANCDetailFragment extends DetailFragment {
    @Override
    protected int layoutResId() {
        return R.layout.fragment_anc_detail;
    }

    @Override
    protected String pageTitle() {
        return "ANC Details";
    }

    @Override
    protected String titleBarId() {
        return client.getColumnmaps().get("existing_program_client_id");
    }

    @Override
    protected void generateView() {
        // getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ((TextView) currentView.findViewById(org.ei.opensrp.core.R.id.details_id_label)).setText(getValue(client.getColumnmaps(), "program_client_id", true));

        // setProfiePic(currentView.getContext(), (ImageView) currentView.findViewById(R.id.anc_profilepic), "pkanc",  client.entityId(), null);

        TableLayout dt1 = (TableLayout) currentView.findViewById(R.id.anc_detail_info_table1);
        dt1.removeAllViews();

        addRow(getActivity(), dt1, "Patient ID", getValue(client.getColumnmaps(), "program_client_id", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt1, "Patient's Name", getValue(client.getColumnmaps(), "existing_first_name", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt1, "Husband's Name", getValue(client.getColumnmaps(), "existing_husband_name", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt1, "Father's Name", getValue(client.getColumnmaps(), "existing_father_name", true), Utils.Size.MEDIUM);

        addRow(getActivity(), dt1, "Date of Birth", getValue(client.getColumnmaps(), "existing_birth_date", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt1, "Age", getValue(client.getColumnmaps(), "existing_age", true), Utils.Size.MEDIUM);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.anc_detail_info_table2);
        dt2.removeAllViews();

        addRow(getActivity(), dt2, "Mobile Number", getValue(client.getColumnmaps(), "contact_phone_number", true), Utils.Size.MEDIUM);
        String Province = getValue(client.getColumnmaps(), "province", true);
        String City = getValue(client.getColumnmaps(), "city_village", true);
        String Town = getValue(client.getColumnmaps(), "town", true);
        String UC = getValue(client.getColumnmaps(), "union_council", true).replace("Uc", "UC");
        String Address = getValue(client.getColumnmaps(), "address1", true);
        if(!City.contains("$") || !Province.contains("$")){
        addRow(getActivity(), dt2, "Address",
                Address  + ", \n" +
                        UC + ", " +
                        Town + ",\n" +
                        City + ", " +
                        Province , Utils.Size.MEDIUM);}
        addRow(getActivity(), dt2, "Ethnicity", getValue(client.getColumnmaps(), "existing_ethnicity", true), Utils.Size.MEDIUM);

        TableLayout dt3 = (TableLayout) currentView.findViewById(R.id.anc_detail_info_table3);
        dt3.removeAllViews();

        addRow(getActivity(), dt3, "EDD", getValue(client.getColumnmaps(), "final_edd", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt3, "LMP", getValue(client.getColumnmaps(), "final_lmp", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt3, "GA", getValue(client.getColumnmaps(), "final_ga", true), Utils.Size.MEDIUM);

        /*addRow(getActivity(), dt3, "Last Pregnancy", getValue(client.getColumnmaps(), " ", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt3, "Miscarriages/Abortions", getValue(client.getColumnmaps(), " ", true), Utils.Size.MEDIUM);*/


        TableLayout dt4 = (TableLayout) currentView.findViewById(R.id.anc_detail_info_table4);
        dt4.removeAllViews();

        addRow(getActivity(), dt4, "TT 1", getValue(client.getColumnmaps(), "e_tt1", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt4, "TT 2", getValue(client.getColumnmaps(), "e_tt2", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt4, "TT 3", getValue(client.getColumnmaps(), "e_tt3", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt4, "TT 4", getValue(client.getColumnmaps(), "e_tt4", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt4, "TT 5", getValue(client.getColumnmaps(), "e_tt5", true), Utils.Size.MEDIUM);


       /* TableLayout dt5 = (TableLayout) currentView.findViewById(R.id.anc_detail_info_table5);
        dt5.removeAllViews();*/

        ((TextView) currentView.findViewById(R.id.anc_comments)).setText(getValue(client.getColumnmaps(), "comments", true));
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.anc_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        return R.drawable.pk_woman_avtar;
    }

    @Override
    protected String bindType() {
        return "pkanc";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }
}
