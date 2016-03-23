package org.ei.opensrp.indonesia.view.viewHolder;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.customControls.BidanClientProfileView;
import org.ei.opensrp.indonesia.view.customControls.BidanObsetriView;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKBRegisterViewHolder {
    private final BidanClientProfileView profileInfoLayout;
    private final BidanObsetriView bidanObsetriView;
    private TextView txtKbType;
    private TextView txtKbStart;
    private TextView txtNoIbu;
    private TextView txtRiskHB;
    private TextView txtRiskPK;
    private TextView txtRiskIMS;
    private TextView txtRiskLILA;
    private final ImageButton editButton;

    public NativeKBRegisterViewHolder(ViewGroup itemView) {
        this.profileInfoLayout = (BidanClientProfileView) itemView.findViewById(R.id.profile_info_layout_ki);
        this.profileInfoLayout.initialize();

        this.bidanObsetriView = (BidanObsetriView) itemView.findViewById(R.id.obsetri_layout);
        this.bidanObsetriView.initialize();

        this.txtKbType = (TextView) itemView.findViewById(R.id.kb_method);
        this.txtKbStart = (TextView) itemView.findViewById(R.id.kb_mulai);
        this.txtNoIbu = (TextView) itemView.findViewById(R.id.no_ibu);

        this.editButton = (ImageButton) itemView.findViewById(R.id.btn_edit);

        this.txtRiskHB = (TextView) itemView.findViewById(R.id.risk_HB);
        this.txtRiskPK = (TextView) itemView.findViewById(R.id.risk_PenyakitKronis);
        this.txtRiskIMS = (TextView) itemView.findViewById(R.id.risk_IMS);
        this.txtRiskLILA = (TextView) itemView.findViewById(R.id.risk_LILA);
    }

    public BidanClientProfileView profileInfoLayout() {
        return profileInfoLayout;
    }
    public BidanObsetriView bidanObsetriView() { return bidanObsetriView; }
    public TextView txtNoIbu() { return txtNoIbu; }
    public TextView txtKbType() { return txtKbType; }
    public TextView txtKbStart() { return txtKbStart; }
    public ImageButton editButton() { return editButton; }

    public TextView getTxtRiskHB() {
        return txtRiskHB;
    }

    public TextView getTxtRiskPK() {
        return txtRiskPK;
    }

    public TextView getTxtRiskIMS() {
        return txtRiskIMS;
    }

    public TextView getTxtRiskLILA() {
        return txtRiskLILA;
    }
}
