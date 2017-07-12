package org.ei.opensrp.path.option;

import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class StatusSort implements CursorSortOption {
    private String name;

    public StatusSort(String name) {
        this.name = name;
    }

    @Override
    public String sort() {
        return "";
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, commoncomparator);
        return allClients;
    }

    Comparator<SmartRegisterClient> commoncomparator = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient oneClient, SmartRegisterClient anotherClient2) {
            HashMap<String, Integer> m = new HashMap(){{
                put("completed", 0);
                put("expired", 1);
                put("upcoming", 2);
                put("normal", 3);
                put("urgent", 4);
            }};

            List<Alert> alertlist_for_client1 = Context.getInstance().alertService().findByEntityIdAndAlertNames(oneClient.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");
            List<Alert> alertlist_for_client2 = Context.getInstance().alertService().findByEntityIdAndAlertNames(anotherClient2.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");

            Log.i(this.getClass().getName(), "ALERT 1 SIZE: "+alertlist_for_client1.toString());
            Log.i(this.getClass().getName(), "ALERT 2 SIZE: "+alertlist_for_client2.toString());

            int max1 = 0;
            for (int i = 0; i < alertlist_for_client1.size(); i++) {
                Alert a = alertlist_for_client1.get(i);
                Log.i(this.getClass().getName(), "Alert 1:"+a.toString());
                if (m.get(a.status().name()) > max1){
                    max1 = m.get(a.status().name());
                }
            }

            int max2 = 0;
            for (int i = 0; i < alertlist_for_client2.size(); i++) {
                Alert a = alertlist_for_client2.get(i);
                Log.i(this.getClass().getName(), "Alert 2:"+a.toString());
                if (m.get(a.status().name()) > max2){
                    max2 = m.get(a.status().name());
                }
            }

            if(max1 == max2) return 0;
            if (max1 > max2) return -1;
            else return 1;
        }
    };
}
