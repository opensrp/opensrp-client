package com.example.opensrp_anc.anc;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.example.opensrp_anc.R;

import org.ei.opensrp.commonregistry.CommonPersonObject;
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
        fillValue((TextView) parentView.findViewById(R.id.patient_id), client.getColumnmaps(), "program_client_id", false);
        fillValue((TextView) parentView.findViewById(R.id.patient_name), client.getColumnmaps(), "existing_first_name", false);
        fillValue((TextView) parentView.findViewById(R.id.patient_husbandname), client.getColumnmaps(), "existing_husband_name", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_birthdate), client.getColumnmaps(), "existing_birth_date", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_age), client.getColumnmaps(), "existing_age", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_visit_number), client.getColumnmaps(), " ", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_edd), client.getColumnmaps(), "final_edd", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_ga), client.getColumnmaps(), "final_ga", false);
        fillValue((TextView) parentView.findViewById(R.id.anc_pregnancy_risk), client.getColumnmaps(), " ", false);

        parentView.findViewById(R.id.child_profile_info_layout).setTag(client);
        parentView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        parentView.findViewById(R.id.open_test_form1).setTag(client);
        parentView.findViewById(R.id.open_test_form1).setOnClickListener(onClickListener);

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
