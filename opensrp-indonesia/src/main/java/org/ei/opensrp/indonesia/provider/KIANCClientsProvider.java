package org.ei.opensrp.indonesia.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.KIANCClient;
import org.ei.opensrp.indonesia.view.controller.KIANCRegisterController;
import org.ei.opensrp.indonesia.view.viewHolder.NativeKIANCRegisterViewHolder;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.ECProfilePhotoLoader;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.ei.opensrp.view.viewHolder.ProfilePhotoLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIANCClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final SecuredActivity activity;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;
    private final Context context;

    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected KIANCRegisterController controller;

    private Drawable iconPencilDrawable;

    public KIANCClientsProvider(
            SecuredActivity activity, View.OnClickListener onClickListener, KIANCRegisterController controller) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(activity.getResources(),
                activity.getResources().getDrawable(R.drawable.woman_placeholder));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) activity.getResources().getDimension(R.dimen.list_item_height));

    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;
        NativeKIANCRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_ki_anc_client, null);
            viewHolder = new NativeKIANCRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeKIANCRegisterViewHolder) itemView.getTag();
        }

        KIANCClient kartuIbuClient = (KIANCClient) client;
        setupClientProfileView(kartuIbuClient, viewHolder);
        setupIdDetailsView(kartuIbuClient, viewHolder);
        setupANCStatusView(kartuIbuClient, viewHolder);
        setupPemeriksaanView(kartuIbuClient, viewHolder);
        setupResikoView(kartuIbuClient, viewHolder);
        setupEditView(kartuIbuClient, viewHolder);
        setupStatusView(kartuIbuClient, viewHolder);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupStatusView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        // get Alert DTO
        AlertDTO ancVisitAlert = client.getANCAlert();

        if(!Strings.isNullOrEmpty(ancVisitAlert.name())) {
            viewHolder.getStatusText().setText(ancVisitAlert.name());
            viewHolder.getDateStatusText().setText(ancVisitAlert.date());
            viewHolder.getLabelDateStatus().setText("Start");
            viewHolder.getAlertStatusText().setText(ancVisitAlert.status().toUpperCase());
            setStatusLayoutColor(ancVisitAlert.alertStatus().backgroundColorResourceId(), ancVisitAlert.alertStatus().fontColor(), viewHolder);
        } else {
            viewHolder.getStatusText().setText("ANC");
            viewHolder.getDateStatusText().setText(client.visitDate());
            viewHolder.getLabelDateStatus().setText("Date");
            viewHolder.getAlertStatusText().setText("-");
            setStatusLayoutColor(R.color.status_bar_text_almost_white, Color.parseColor("#000000"), viewHolder);
        }
    }

    private void setStatusLayoutColor(int backgroundColor, int fontColor, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.getAncStatusLayout().setBackgroundResource(backgroundColor);
        viewHolder.getStatusText().setTextColor(fontColor);
        viewHolder.getDateStatusText().setTextColor(fontColor);
        viewHolder.getAlertStatusText().setTextColor(fontColor);
        viewHolder.getLabelDateStatus().setTextColor(fontColor);
    }

    private void setupClientProfileView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupIdDetailsView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.txtNoIbu().setText(String.valueOf(client.kiNumber()));
        viewHolder.txtUniqueId().setText(String.valueOf(client.getUniqueId()));
    }

    private void setupANCStatusView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.ancStatusHtp().setText(client.eddForDisplay()==null?"-":client.eddForDisplay());
        viewHolder.ancStatusUsiaKlinis().setText(client.usiaKlinis()==null?"-":client.usiaKlinis());
    }

    private void setupPemeriksaanView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        viewHolder.getPemeriksaanBB().setText(client.getBeratBadan());
        viewHolder.getPemeriksaanLILA().setText(client.getLILA());
    }

    private void setupResikoView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        List<String> allRisks = new ArrayList<>();
        allRisks.addAll(client.highRiskReason());
        allRisks.addAll(client.highPregnancyReason());
        String rkk = client.getRiwayatKomplikasiKebidanan().replaceAll("^[,\\s]+", "");
        String pk = client.getPenyakitKronis().replaceAll("^[,\\s]+", "");
        if(!Strings.isNullOrEmpty(rkk))
            allRisks.addAll(Arrays.asList(rkk.split(",")));
        if(!Strings.isNullOrEmpty(pk))
            allRisks.addAll(Arrays.asList(pk.split(",")));

        String alergi = client.getAlergi();

        String penyakit = StringUtils.join(allRisks, ',') +
                (!alergi.equalsIgnoreCase("NA") && !Strings.isNullOrEmpty(alergi) ? "(Alergi : " + alergi + " )" : "");

        viewHolder.getPenyakitKronis().setText(Strings.isNullOrEmpty(penyakit) ? "" : penyakit);

        if(!Strings.isNullOrEmpty(penyakit) || !Strings.isNullOrEmpty(alergi)) {
            viewHolder.getLayoutResikoANC().setBackgroundColor(Color.parseColor("#FAD5D5"));
        } else {
            viewHolder.getLayoutResikoANC().setBackgroundColor(Color.parseColor("#CCFFCC"));
        }
    }

    private void setupEditView(KIANCClient client, NativeKIANCRegisterViewHolder viewHolder) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }


    @Override
    public SmartRegisterClients getClients() {
        return controller.getKIANCClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return new OnClickFormLauncher(activity, formName, entityId, metaData);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
