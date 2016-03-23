package org.ei.opensrp.indonesia.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.KBClient;
import org.ei.opensrp.indonesia.view.controller.KohortKBRegisterController;
import org.ei.opensrp.indonesia.view.viewHolder.NativeKBRegisterViewHolder;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.ECProfilePhotoLoader;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.ei.opensrp.view.viewHolder.ProfilePhotoLoader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KBClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final ProfilePhotoLoader photoLoader;

    private final AbsListView.LayoutParams clientViewLayoutParams;


    private Drawable iconPencilDrawable;

    protected KohortKBRegisterController controller;

    public KBClientsProvider(Context context, View.OnClickListener onClickListener, KohortKBRegisterController controller) {
        this.context = context;
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        photoLoader = new ECProfilePhotoLoader(context.getResources(),
                context.getResources().getDrawable(R.drawable.woman_placeholder));

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));

    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;
        NativeKBRegisterViewHolder viewHolder;
        if (convertView == null) {
                itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_kb_client, null);
            viewHolder = new NativeKBRegisterViewHolder(itemView);
            itemView.setTag(viewHolder);
        } else {
            itemView = (ViewGroup) convertView;
            viewHolder = (NativeKBRegisterViewHolder) itemView.getTag();
        }

        KBClient kartuIbuClient = (KBClient) client;
        setupClientProfileView(kartuIbuClient, viewHolder);
        setupEditView(kartuIbuClient, viewHolder);
        setupClientNoIbuView(kartuIbuClient, viewHolder);
        setupObsetriView(kartuIbuClient, viewHolder);
        setupJenisKontrasepsiView(kartuIbuClient, viewHolder);
        setupRiskView(kartuIbuClient, viewHolder);

        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void setupHighlightColor(ViewGroup itemView, int index) {
        if(index%2==0) {
            itemView.setBackgroundColor(Color.parseColor("#E0F5FF"));
        } else {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }

    private void setupClientProfileView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        viewHolder.profileInfoLayout().bindData(client, photoLoader);
        viewHolder.profileInfoLayout().setOnClickListener(onClickListener);
        viewHolder.profileInfoLayout().setTag(client);
    }

    private void setupClientNoIbuView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        viewHolder.txtNoIbu().setText(String.valueOf(client.getNoIbu()));
    }

    private void setupObsetriView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        viewHolder.bidanObsetriView().bindKB(client);
    }

    private void setupRiskView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        viewHolder.getTxtRiskHB().setText(client.getHB());
        viewHolder.getTxtRiskIMS().setText(client.getIMS());
        viewHolder.getTxtRiskLILA().setText(client.getLILA());
        viewHolder.getTxtRiskPK().setText(client.getPenyakitKronis());
    }

    private void setupJenisKontrasepsiView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        viewHolder.txtKbType().setText(String.valueOf(client.getJenisKontrasepsi()));
        viewHolder.txtKbStart().setText(String.valueOf(client.getTglKunjungan()));
    }

    private void setupEditView(KBClient client, NativeKBRegisterViewHolder viewHolder) {
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewHolder.editButton().setImageDrawable(iconPencilDrawable);
        viewHolder.editButton().setOnClickListener(onClickListener);
        viewHolder.editButton().setTag(client);
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getKBClients();
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
