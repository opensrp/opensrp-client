package org.ei.opensrp.path.fragment.table;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.path.R;


public class WomanVaccineTable extends Fragment {

    private CommonPersonObject pregnantWomanObject;

   public WomanVaccineTable(){}
    @SuppressLint("ValidFragment")
    public WomanVaccineTable(CommonPersonObject pregnantWomanObject) {
      this.pregnantWomanObject=pregnantWomanObject;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_woman_vaccine_table, container, false);

        TextView pragnantWomanTT1=(TextView)rootView.findViewById(R.id.woman_vaccine_tt1_0);
        TextView pragnantWomanTT2=(TextView)rootView.findViewById(R.id.woman_vaccine_tt2_0);
        TextView pragnantWomanTT3=(TextView)rootView.findViewById(R.id.woman_vaccine_tt3_0);
        TextView pragnantWomanTT4=(TextView)rootView.findViewById(R.id.woman_vaccine_tt4_0);
        TextView pragnantWomanTT5=(TextView)rootView.findViewById(R.id.woman_vaccine_tt5_0);

        if(pregnantWomanObject!=null) {
            pragnantWomanTT1.setText(pregnantWomanObject.getColumnmaps().get("tt1") != null ? pregnantWomanObject.getColumnmaps().get("tt1") : "");
            pragnantWomanTT2.setText(pregnantWomanObject.getColumnmaps().get("tt2") != null ? pregnantWomanObject.getColumnmaps().get("tt2") : "");
            pragnantWomanTT3.setText(pregnantWomanObject.getColumnmaps().get("tt3") != null ? pregnantWomanObject.getColumnmaps().get("tt3") : "");
            pragnantWomanTT4.setText(pregnantWomanObject.getColumnmaps().get("tt4") != null ? pregnantWomanObject.getColumnmaps().get("tt4") : "");
            pragnantWomanTT5.setText(pregnantWomanObject.getColumnmaps().get("tt5") != null ? pregnantWomanObject.getColumnmaps().get("tt5") : "");
        }
        return rootView;//inflater.inflate(R.layout.fragment_woman_vaccine_table, container, false);
    }
}
