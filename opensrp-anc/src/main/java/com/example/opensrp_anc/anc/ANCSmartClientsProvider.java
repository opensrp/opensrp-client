package com.example.opensrp_anc.anc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.example.opensrp_anc.R;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.service.AlertService;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.core.utils.Utils.fillValue;

/**
 * Created by Owais on 3/31/2017.
 */

public class ANCSmartClientsProvider implements RegisterClientsProvider<CommonPersonObjectClient> {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    public ANCSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                   AlertService alertService) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.core.R.dimen.list_item_height));    }

    @Override
    public View getView(CommonPersonObjectClient client, View parentView, ViewGroup viewGroup) {
        parentView.setLayoutParams(clientViewLayoutParams);
        fillValue((TextView) parentView.findViewById(R.id.patient_id), client.getColumnmaps(), "existing_program_client_id", false);
        fillValue((TextView) parentView.findViewById(R.id.patient_name), client.getColumnmaps(), "existing_first_name", false);
        fillValue((TextView) parentView.findViewById(R.id.patient_husbandname), client.getColumnmaps(), "existing_husband_name", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_birthdate), client.getColumnmaps(), "existing_birth_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_age), client.getColumnmaps(), "existing_age", false);
        //fillValue((TextView) parentView.findViewById(R.id.anc_contact), client.getColumnmaps(), "contact_phone_number", false);
       /* fillValue((TextView) parentView.findViewById(R.id.anc_visit_1), client.getColumnmaps(), "anc_visit1_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_visit_2), client.getColumnmaps(), "anc_visit2_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_visit_3), client.getColumnmaps(), "anc_visit3_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_visit_4), client.getColumnmaps(), "anc_visit4_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_other_visit), client.getColumnmaps(), "anc_other_visit_date", false);*/
        fillValue((TextView) parentView.findViewById(R.id.anc_edd), client.getColumnmaps(), "final_edd", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_ga), client.getColumnmaps(), "final_ga", false);
       // fillValue((TextView) parentView.findViewById(R.id.anc_comments), client.getColumnmaps(), "comments", false);

        parentView.findViewById(R.id.child_profile_info_layout).setTag(client);
        parentView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        parentView.findViewById(R.id.open_anc_form).setTag(client);
        parentView.findViewById(R.id.open_anc_form).setOnClickListener(onClickListener);

        return parentView;
    }

    @Override
    public List<CommonPersonObjectClient> getClients() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public View inflateLayoutForAdapter() {
        return inflater().inflate(R.layout.smart_register_anc_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
