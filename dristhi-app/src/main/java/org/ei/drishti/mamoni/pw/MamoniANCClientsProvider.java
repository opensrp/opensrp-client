package org.ei.drishti.mamoni.pw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import org.ei.drishti.R;
import org.ei.drishti.commonregistry.PersonObjectClient;
import org.ei.drishti.commonregistry.PersonObjectController;
import org.ei.drishti.provider.SmartRegisterClientsProvider;
import org.ei.drishti.view.contract.SmartRegisterClient;
import org.ei.drishti.view.contract.SmartRegisterClients;
import org.ei.drishti.view.dialog.FilterOption;
import org.ei.drishti.view.dialog.ServiceModeOption;
import org.ei.drishti.view.dialog.SortOption;
import org.ei.drishti.view.viewHolder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class MamoniANCClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected PersonObjectController controller;

    public MamoniANCClientsProvider(Context context,
                                    View.OnClickListener onClickListener,
                                    PersonObjectController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;

        itemView = (ViewGroup) inflater().inflate(R.layout.mamoni_reg_row, null);
        TextView name = (TextView)itemView.findViewById(R.id.name);
        TextView husbandname = (TextView)itemView.findViewById(R.id.husbandname);
        TextView district = (TextView)itemView.findViewById(R.id.district);
        TextView age = (TextView)itemView.findViewById(R.id.age);
        TextView hhid = (TextView)itemView.findViewById(R.id.hhid);
        TextView coupleno = (TextView)itemView.findViewById(R.id.coupleno);
        TextView status = (TextView)itemView.findViewById(R.id.status);
        TextView husbandnamecolumn = (TextView)itemView.findViewById(R.id.husbandnamecolumn);
        TextView tt = (TextView)itemView.findViewById(R.id.tt);

        Button edit = (Button)itemView.findViewById(R.id.edit);
        edit.setOnClickListener(onClickListener);
        edit.setTag(smartRegisterClient);

        PersonObjectClient pc = (PersonObjectClient) smartRegisterClient;

        hhid.setText(pc.getDetails().get("PW_HHID")!=null?pc.getDetails().get("PW_HHID"):"");
        name.setText(pc.getName()!=null?pc.getName():"");
        age.setText(pc.getDetails().get("PW_Age")!=null?pc.getDetails().get("PW_Age"):"");
        husbandname.setText(pc.getDetails().get("PW_Husband_Name")!=null?pc.getDetails().get("PW_Husband_Name"):"");
        district.setText("");
        coupleno.setText(pc.getDetails().get("PW_Couple_No")!=null?pc.getDetails().get("PW_Couple_No"):"");
        status.setText("Pregnant");
        husbandnamecolumn.setText(pc.getDetails().get("PW_Husband_Name")!=null?pc.getDetails().get("PW_Husband_Name"):"");
        tt.setText("");


        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
