package org.ei.opensrp.indonesia.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.google.common.base.Strings;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.indonesia.view.controller.KartuIbuRegisterController;
import org.ei.opensrp.indonesia.view.viewHolder.NativeKIRegisterViewHolder;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.ECProfilePhotoLoader;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.ei.opensrp.view.viewHolder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_DATE_FIELD;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_TYPE_FIELD;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KIClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;

    private final AbsListView.LayoutParams clientViewLayoutParams;

    private final String maleChildAgeFormatString;
    private final String femaleChildAgeFormatString;

    private Drawable iconPencilDrawable;

    protected KartuIbuRegisterController controller;

    public KIClientsProvider(Context context, View.OnClickListener onClickListener, KartuIbuRegisterController controller) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(context.getResources(),
                context.getResources().getDrawable(R.drawable.woman_placeholder));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));


        maleChildAgeFormatString = context.getResources().getString(R.string.ec_register_male_child);
        femaleChildAgeFormatString = context.getResources().getString(R.string.ec_register_female_child);

    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;
        NativeKIRegisterViewHolder viewHolder;
        if (convertView == null) {
            itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_ki_client, null);
            viewHolder = new NativeKIRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeKIRegisterViewHolder) itemView.getTag();
        }

        KartuIbuClient kartuIbuClient = (KartuIbuClient) client;
        setupClientProfileView(kartuIbuClient, viewHolder);
        setupChildrenView(kartuIbuClient, viewHolder);
        setupEditView(kartuIbuClient, viewHolder);
        setupClientNoIbuView(kartuIbuClient, viewHolder);
        setupObsetriView(kartuIbuClient, viewHolder);
        setupClientEDDView(kartuIbuClient, viewHolder);
        setupStatusView(kartuIbuClient, viewHolder);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupClientProfileView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupChildrenView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.childrenView().bindData(client, maleChildAgeFormatString, femaleChildAgeFormatString);
    }

    private void setupClientNoIbuView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.txtNoIbu().setText(String.valueOf(client.getNoIbu()));
        viewHolder.txtUniqueId().setText(String.valueOf(client.getUniqueId()));
    }

    private void setupObsetriView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.bidanObsetriView().bind(client);
    }

    private void setupClientEDDView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.txtEdd().setText(String.valueOf(client.getEdd()));
        viewHolder.txtEddDue().setText(String.valueOf(client.getDueEdd()));
    }

    private void setupEditView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }

    private void setupStatusView(KartuIbuClient client, NativeKIRegisterViewHolder viewHolder) {

        viewHolder.statusView().hideAllLayout();

        if (client.status().containsKey(STATUS_TYPE_FIELD)) {

            String motherStatus = client.status().get(STATUS_TYPE_FIELD);
            AlertDTO anc1VisitAlert = client.getANCAlertByCategory(KartuIbuClient.CATEGORY_ANC);
            AlertDTO pncVisitAlert = client.getANCAlertByCategory(KartuIbuClient.CATEGORY_PNC);
            AlertDTO kbAlert = client.getANCAlertByCategory(KartuIbuClient.CATEGORY_KB);

            viewHolder.statusView().statusLayout(motherStatus).setVisibility(View.VISIBLE);

            if(!Strings.isNullOrEmpty(kbAlert.name())) {
                setStatusView(viewHolder.statusView().statusLayout(motherStatus), kbAlert, viewHolder);
                return;
            }

            if(!Strings.isNullOrEmpty(anc1VisitAlert.name())) {
                setStatusView(viewHolder.statusView().statusLayout(motherStatus), anc1VisitAlert, viewHolder);
                return;
            }

            if(!Strings.isNullOrEmpty(pncVisitAlert.name())) {
                setStatusView(viewHolder.statusView().statusLayout(motherStatus), pncVisitAlert, viewHolder);
                return;
            }

            ViewGroup statusViewGroup = viewHolder.statusView().statusLayout(motherStatus);
            viewHolder.statusView().dateView(statusViewGroup).setText(client.status().get(STATUS_DATE_FIELD));
            if(motherStatus.equalsIgnoreCase("kb")) {
                viewHolder.statusView().typeView(statusViewGroup).setText("KB" + (Strings.isNullOrEmpty(client.kbMethod()) ? "" : " - " + client.kbMethod()));
            } else {
                viewHolder.statusView().typeView(statusViewGroup).setText(motherStatus.toUpperCase());
            }
            viewHolder.statusView().labelDateView(statusViewGroup).setText("Date:");
            viewHolder.statusView().statusView(statusViewGroup).setText("-");
            setStatusLayoutColor(R.color.status_bar_text_almost_white, Color.parseColor("#000000"), viewHolder, statusViewGroup);

        } else {
            setStatusLayoutColor(R.color.status_bar_text_almost_white, Color.parseColor("#000000"), viewHolder, null);
        }

    }

    private void setStatusView(ViewGroup statusViewGroup, AlertDTO alert, NativeKIRegisterViewHolder viewHolder) {
        viewHolder.statusView().typeView(statusViewGroup).setText(alert.name());
        viewHolder.statusView().dateView(statusViewGroup).setText(alert.date());
        viewHolder.statusView().statusView(statusViewGroup).setText(alert.status().toUpperCase());
        setStatusLayoutColor(alert.alertStatus().backgroundColorResourceId(), alert.alertStatus().fontColor(), viewHolder, statusViewGroup);
        viewHolder.statusView().labelDateView(statusViewGroup).setText("Start");
    }

    private void setStatusLayoutColor(int backgroundColor, int fontColor, NativeKIRegisterViewHolder viewHolder, ViewGroup statusViewGroup) {
        viewHolder.statusView().setBackgroundResource(backgroundColor);
        if(statusViewGroup != null) {
            viewHolder.statusView().labelDateView(statusViewGroup).setTextColor(fontColor);
            viewHolder.statusView().typeView(statusViewGroup).setTextColor(fontColor);
            viewHolder.statusView().dateView(statusViewGroup).setTextColor(fontColor);
            viewHolder.statusView().statusView(statusViewGroup).setTextColor(fontColor);
        }
    }


    @Override
    public SmartRegisterClients getClients() {
        return controller.getKartuIbuClients();
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
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
