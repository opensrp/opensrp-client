package org.ei.opensrp.path.provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Maimoona on 7/2/2016.
 */
public abstract class ClientsProvider implements SmartRegisterClientsProvider {
    protected final LayoutInflater inflater;
    protected final Context context;
    protected final View.OnClickListener onClickListener;
    protected final AlertService alertService;
    protected final int txtColorBlack;
    protected final AbsListView.LayoutParams clientViewLayoutParams;

    protected final CommonPersonObjectController controller;

    public ClientsProvider(Context context, View.OnClickListener onClickListener,
                                     CommonPersonObjectController controller, AlertService alertService) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }
}
