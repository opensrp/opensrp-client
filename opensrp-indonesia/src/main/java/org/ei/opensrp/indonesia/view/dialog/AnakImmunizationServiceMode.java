package org.ei.opensrp.indonesia.view.dialog;

import android.view.View;

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
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.BAYI_IMUNISASI;

/**
 * Created by Dimas Ciputra on 4/27/15.
 */
public class AnakImmunizationServiceMode extends BidanServiceModeOption {

    public AnakImmunizationServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_service_mode_immunization);
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 100;
            }

            @Override
            public int[] weights() {
                return new int[]{26, 12, 12, 12, 12, 12, 12};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_imunisasi_hb07, R.string.header_imunisasi_bcg_polio1,
                        R.string.header_imunisasi_dpt_hb1_polio2, R.string.header_imunisasi_dpt_hb2_polio3, R.string.header_imunisasi_dpt_hb3_polio4,
                        R.string.header_imunisasi_campak};
            }
        };
    }

    @Override
    public void setupListView(AnakClient client, NativeAnakRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {
        viewHolder.getServiceModeImmunizationView().setVisibility(View.VISIBLE);
        //viewHolder.getServiceModeImmunizationView().setOnClickListener(clientSectionClickListener);
        viewHolder.getServiceModeImmunizationView().setTag(client.entityId());
        setupHbView(client, viewHolder);
        setupBcgView(client, viewHolder);
        setupDpt1View(client, viewHolder);
        setupDpt2View(client, viewHolder);
        setupDpt3View(client, viewHolder);
        setupCampakView(client, viewHolder);
    }

    private void setupHbView(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtHb07PendingView().setText("Hb");
        viewHolder.getTxtHb07Done().setText("Hb");

        if(client.isHbSevenDone()) {
            viewHolder.getLayoutHb07On().setVisibility(View.VISIBLE);
            viewHolder.getTxtHb07DoneOn().setText(client.getHb07());
            viewHolder.getTxtHb07PendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutHb07On().setVisibility(View.INVISIBLE);
            viewHolder.getTxtHb07PendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtHb07PendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    private void setupBcgView(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtBcgPendingView().setText("Bcg/Pol1");
        viewHolder.getTxtBcgDone().setText("Bcg/Pol1");

        if(client.isBcgDone()) {
            viewHolder.getLayoutBcgOn().setVisibility(View.VISIBLE);
            viewHolder.getTxtBcgDoneOn().setText(client.getBcgPol1());
            viewHolder.getTxtBcgPendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutBcgOn().setVisibility(View.INVISIBLE);
            viewHolder.getTxtBcgPendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtBcgPendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    private void setupDpt1View(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtDpt1PendingView().setText("Dpt/Hb1/Pol2");
        viewHolder.getTxtDpt1Done().setText("Dpt/Hb1/Pol2");

        if(client.isDpt1Done()) {
            viewHolder.getLayoutDpt1On().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt1DoneOn().setText(client.getDptHb1Pol2());
            viewHolder.getTxtDpt1PendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutDpt1On().setVisibility(View.INVISIBLE);
            viewHolder.getTxtDpt1PendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt1PendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    private void setupDpt2View(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtDpt2PendingView().setText("Dpt/Hb2/Pol3");
        viewHolder.getTxtDpt2Done().setText("Dpt/Hb2/Pol3");

        if(client.isDpt2Done()) {
            viewHolder.getLayoutDpt2On().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt2DoneOn().setText(client.getDptHb2Pol3());
            viewHolder.getTxtDpt2PendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutDpt2On().setVisibility(View.INVISIBLE);
            viewHolder.getTxtDpt2PendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt2PendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    private void setupDpt3View(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtDpt3PendingView().setText("Dpt/Hb3/Pol4");
        viewHolder.getTxtDpt3Done().setText("Dpt/Hb3/Pol4");

        if(client.isDpt3Done()) {
            viewHolder.getLayoutDpt3On().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt3DoneOn().setText(client.getDptHb3Pol4());
            viewHolder.getTxtDpt3PendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutDpt3On().setVisibility(View.INVISIBLE);
            viewHolder.getTxtDpt3PendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtDpt3PendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
    }

    private void setupCampakView(AnakClient client, NativeAnakRegisterViewHolder viewHolder) {
        viewHolder.getTxtCampakPendingView().setText("Campak");
        viewHolder.getTxtCampakDone().setText("Campak");

        if(client.isCampakDone()) {
            viewHolder.getLayoutCampakOn().setVisibility(View.VISIBLE);
            viewHolder.getTxtCampakDoneOn().setText(client.getCampak());
            viewHolder.getTxtCampakPendingView().setVisibility(View.INVISIBLE);
        } else {
            viewHolder.getLayoutCampakOn().setVisibility(View.INVISIBLE);
            viewHolder.getTxtCampakPendingView().setVisibility(View.VISIBLE);
            viewHolder.getTxtCampakPendingView().setOnClickListener(launchChildImmunizationForm(client));
        }
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

    private OnClickFormLauncher launchChildImmunizationForm(AnakClient client) {
        return provider().newFormLauncher(BAYI_IMUNISASI, client.entityId(), null);
    }

}
