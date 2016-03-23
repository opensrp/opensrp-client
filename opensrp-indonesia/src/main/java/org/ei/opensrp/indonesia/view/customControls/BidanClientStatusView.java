package org.ei.opensrp.indonesia.view.customControls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.view.viewHolder.ViewStubInflater;

import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.util.DateUtil.formatDate;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.ANC_STATUS;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.PNC_STATUS;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_DATE_FIELD;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_TYPE_FIELD;

/**
 * Created by Dimas Ciputra on 3/9/15.
 */
public class BidanClientStatusView extends FrameLayout {

    private Map<String, ViewStubInflater> statusLayouts;

    @SuppressWarnings("UnusedDeclaration")
    public BidanClientStatusView(Context context) {
        this(context, null, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public BidanClientStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BidanClientStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initialize() {
        this.statusLayouts = new HashMap<String, ViewStubInflater>();

        this.statusLayouts
                .put(ANC_STATUS, new ViewStubInflater((ViewStub) findViewById(R.id.anc_status_layout)));

        this.statusLayouts
                .put(PNC_STATUS, new ViewStubInflater((ViewStub) findViewById(R.id.pnc_status_layout)));

        this.statusLayouts
                .put("Kartu Ibu", new ViewStubInflater((ViewStub) findViewById(R.id.ki_status_layout)));

        this.statusLayouts
                .put("KB", new ViewStubInflater((ViewStub) findViewById(R.id.kb_status_layout)));
    }

    public void bindData(KartuIbuClient client) {
        hideAllLayout();

        if (client.status().containsKey(STATUS_TYPE_FIELD)) {
            String statusType = client.status().get(STATUS_TYPE_FIELD);
            String statusDate = formatDate(client.status().get(STATUS_DATE_FIELD));

            ViewGroup statusViewGroup = statusLayout(statusType);
            statusViewGroup.setVisibility(View.VISIBLE);
            dateView(statusViewGroup).setText(statusDate);

            if(statusType.equalsIgnoreCase("kb")) {
                typeView(statusViewGroup).setText("KB" +
                        (Strings.isNullOrEmpty(client.kbMethod()) ? "" : " - " + client.kbMethod()));
            }
        }
    }

    public ViewGroup statusLayout(String statusType) {
        return statusLayouts.get(statusType).get();
    }

    public void hideAllLayout() {
        for (String statusLayout : statusLayouts.keySet()) {
            statusLayouts.get(statusLayout).setVisibility(View.GONE);
        }
    }

    public TextView dateView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_status_date));
    }

    public TextView typeView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_status_type));
    }

    public TextView statusView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.txt_alert_status));
    }

    public TextView labelDateView(ViewGroup statusViewGroup) {
        return ((TextView) statusViewGroup.findViewById(R.id.str_edd_type));
    }

}
