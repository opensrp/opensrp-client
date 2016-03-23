package org.ei.opensrp.indonesia.view.viewHolder;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.customControls.BidanClientProfileView;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class NativeKIANCRegisterViewHolder {

    private final BidanClientProfileView profileInfoLayout;
    private final TextView ancStatusUsiaKlinis;
    private final TextView ancStatusHtp;
    private final TextView pemeriksaanLILA;
    private final TextView pemeriksaanBB;
    private final TextView penyakitKronis;
    private final ViewGroup layoutResikoANC;
    private final ImageButton editButton;
    private final RelativeLayout ancStatusLayout;
    private final TextView statusText;
    private final TextView labelDateStatus;
    private final TextView dateStatusText;
    private final TextView alertStatusText;

    private final RelativeLayout detailIdLayout;
    private TextView txtNoIbu;
    private TextView txtUniqueId;

    public NativeKIANCRegisterViewHolder(ViewGroup itemView) {
        this.profileInfoLayout = (BidanClientProfileView) itemView.findViewById(R.id.profile_info_layout_ki);
        this.profileInfoLayout.initialize();

        this.detailIdLayout = (RelativeLayout) itemView.findViewById(R.id.id_detail_layout);
        this.txtNoIbu = (TextView) this.detailIdLayout.findViewById(R.id.no_ibu);
        this.txtUniqueId = (TextView) this.detailIdLayout.findViewById(R.id.unique_id);

        this.ancStatusUsiaKlinis = (TextView) itemView.findViewById(R.id.txt_usia_klinis);
        this.ancStatusHtp = (TextView) itemView.findViewById(R.id.txt_htpt);
        this.pemeriksaanLILA = (TextView) itemView.findViewById(R.id.txt_ki_lila_bb);
        this.pemeriksaanBB = (TextView) itemView.findViewById(R.id.txt_ki_beratbadan_tb);
        this.penyakitKronis = (TextView) itemView.findViewById(R.id.txt_ki_anc_penyakit_kronis);
        this.layoutResikoANC = (ViewGroup) itemView.findViewById(R.id.layout_resiko_anc);
        this.editButton = (ImageButton) itemView.findViewById(R.id.btn_edit);

        this.ancStatusLayout = (RelativeLayout) itemView.findViewById(R.id.anc_status_layout);
        this.statusText = (TextView) this.ancStatusLayout.findViewById(R.id.txt_status_type);
        this.labelDateStatus = (TextView) this.ancStatusLayout.findViewById(R.id.label_status_date);
        this.dateStatusText = (TextView) this.ancStatusLayout.findViewById(R.id.txt_status_date);
        this.alertStatusText = (TextView) this.ancStatusLayout.findViewById(R.id.txt_alert_status);

    }

    public ImageButton editButton() { return editButton; }
    public TextView ancStatusUsiaKlinis() { return ancStatusUsiaKlinis; }
    public TextView ancStatusHtp() { return ancStatusHtp; }

    public BidanClientProfileView profileInfoLayout() {
        return profileInfoLayout;
    }
    public TextView getPemeriksaanLILA() {
        return pemeriksaanLILA;
    }
    public TextView getPemeriksaanBB() {
        return pemeriksaanBB;
    }

    public ViewGroup getLayoutResikoANC() {
        return layoutResikoANC;
    }
    public TextView txtNoIbu() { return txtNoIbu; }
    public TextView txtUniqueId() { return txtUniqueId; }

    public TextView getPenyakitKronis() {
        return penyakitKronis;
    }

    public TextView getLabelDateStatus() {
        return labelDateStatus;
    }

    public TextView getDateStatusText() {
        return dateStatusText;
    }

    public TextView getAlertStatusText() {
        return alertStatusText;
    }

    public TextView getStatusText() {
        return statusText;
    }

    public RelativeLayout getAncStatusLayout() {
        return ancStatusLayout;
    }
}
