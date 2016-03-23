package org.ei.opensrp.indonesia.view.dialog;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.common.base.Strings;

import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.AnakClient;
import org.ei.opensrp.indonesia.view.contract.KIANCClient;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.indonesia.view.viewHolder.NativeAnakRegisterViewHolder;
import org.ei.opensrp.indonesia.view.viewHolder.NativeKIANCRegisterViewHolder;
import org.ei.opensrp.indonesia.view.viewHolder.NativeKIRegisterViewHolder;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ANCSmartRegisterClient;
import org.ei.opensrp.view.contract.ChildSmartRegisterClient;
import org.ei.opensrp.view.contract.FPSmartRegisterClient;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static android.view.View.VISIBLE;
import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class AnakOverviewServiceMode extends BidanServiceModeOption {

    public AnakOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_service_mode_overview);
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 6;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{26, 10, 16, 15, 23, 8};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id_no, R.string.header_dok_persalinan,
                        R.string.header_last_service, R.string.header_birth_status, R.string.header_edit};
            }
        };
    }

    @Override
    public void setupListView(AnakClient client,
                              NativeAnakRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {
        viewHolder.getServiceModeOverviewView().setVisibility(VISIBLE);

        setupDokPersalinanView(client, viewHolder);
        setupLastServiceView(client, viewHolder);
        setupBirthStatus(client, viewHolder);
        setupEditView(client, viewHolder, clientSectionClickListener);
    }

    @Override
    public void setupListView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    private void setupDokPersalinanView(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtDobView().setText(client.dateOfBirth());
        viewHolder.getBirthPlace().setText(client.birthPlace());
        viewHolder.getTxtBirthWeight().setText(client.birthWeight());
        viewHolder.getTxtBirthCondition().setText(client.getBirthCondition());
    }

    private void setupLastServiceView(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        // ServiceProvidedDTO lastService = client.lastServiceProvided();
        String hbGiven = client.getHbGiven();
        String service = client.getServiceAtBirth();

        if(!Strings.isNullOrEmpty(service)) {
            String[] serviceAtBirth = service.split("\\s+");

            for(int i = 0; i < serviceAtBirth.length; i++) {
                switch (serviceAtBirth[i]) {
                    case "first_breast_feeding" :
                        viewHolder.getImdTrueIcon().setVisibility(View.VISIBLE);
                        viewHolder.getImdFalseIcon().setVisibility(View.GONE);
                        break;
                    case "vit-k_injection" :
                        viewHolder.getVitKTrueIcon().setVisibility(View.VISIBLE);
                        viewHolder.getVitKFalseIcon().setVisibility(View.GONE);
                        break;
                    case "salep_mata" :
                        viewHolder.getSlepMataTrueIcon().setVisibility(View.VISIBLE);
                        viewHolder.getSlepMataFalseIcon().setVisibility(View.GONE);
                }
            }
        }

        if(hbGiven.equalsIgnoreCase("ya")) {
            viewHolder.getImmuniHBTrueIcon().setVisibility(View.VISIBLE);
            viewHolder.getImmuniHBFalseIcon().setVisibility(View.GONE);
        }
    }

    private void setupBirthStatus(AnakClient client,
                                 NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getClientAnakBirthStatusView().bindData(client);
    }

    private void setupEditView(AnakClient client,
                               NativeAnakRegisterViewHolder viewHolder, View.OnClickListener onClickListener) {
        Drawable iconPencilDrawable = Context.getInstance().applicationContext().getResources().getDrawable(R.drawable.ic_pencil);
        viewHolder.getEditButton().setImageDrawable(iconPencilDrawable);
        viewHolder.getEditButton().setOnClickListener(onClickListener);
        viewHolder.getEditButton().setTag(client);
    }
}
