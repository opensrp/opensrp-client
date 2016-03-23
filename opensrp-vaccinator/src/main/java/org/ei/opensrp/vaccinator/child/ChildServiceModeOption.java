package org.ei.opensrp.vaccinator.child;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ANCSmartRegisterClient;
import org.ei.opensrp.view.contract.ChildSmartRegisterClient;
import org.ei.opensrp.view.contract.FPSmartRegisterClient;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildServiceModeOption extends ServiceModeOption {

   public ChildServiceModeOption(SmartRegisterClientsProvider provider){
       super(provider);

   }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.child_entries);
    }
    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 4;
            }

            @Override
            public int weightSum() {
                return 10;
            }

            @Override
            public int[] weights() {
                return new int[]{3,2,2,3};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.child_profile
                        , R.string.child_DOB, R.string.child_last_vaccine,
                        R.string.child_next_vaacine
                };
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {
       /// Log.d("child View : ", client.dateOfBirth());
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


}
