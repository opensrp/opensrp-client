package org.ei.opensrp.path.fragment.table;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.path.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildVaccineTable extends Fragment {

   private CommonPersonObject childObject;
    public ChildVaccineTable(){}
    @SuppressLint("ValidFragment")
    public ChildVaccineTable(CommonPersonObject childObject) {
        this.childObject=childObject;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.logDebug("reached childVaccineTable");
        //setting layout
        View rootView = inflater.inflate(R.layout.fragment_child_vaccine_table, container, false);


        TextView bcg0TextView= (TextView) rootView.findViewById(R.id.child_table_bcg0);
        TextView bcg1TextView= (TextView) rootView.findViewById(R.id.child_table_bcg1);
        TextView bcg2TextView= (TextView) rootView.findViewById(R.id.child_table_bcg2);
        TextView bcgtotalTextView= (TextView) rootView.findViewById(R.id.child_table_bcgtotal);


        TextView opv0_0TextView= (TextView) rootView.findViewById(R.id.child_table_opv0_0);
        TextView opv0_1TextView= (TextView) rootView.findViewById(R.id.child_table_opv0_1);
        TextView opv0_2TextView= (TextView) rootView.findViewById(R.id.child_table_opv0_2);
        TextView opv0totalTextView= (TextView) rootView.findViewById(R.id.child_table_opv0total);

        //sadfsdaf

        TextView opv1_0TextView= (TextView) rootView.findViewById(R.id.child_table_opv1_0);
        TextView opv1_1TextView= (TextView) rootView.findViewById(R.id.child_table_opv1_1);
        TextView opv1_2TextView= (TextView) rootView.findViewById(R.id.child_table_opv1_2);
        TextView opv1totalTextView= (TextView) rootView.findViewById(R.id.child_table_opv1total);




        TextView opv2_0TextView= (TextView) rootView.findViewById(R.id.child_table_opv2_0);
        TextView opv2_1TextView= (TextView) rootView.findViewById(R.id.child_table_opv2_1);
        TextView opv2_2TextView= (TextView) rootView.findViewById(R.id.child_table_opv2_2);
        TextView opv2totalTextView= (TextView) rootView.findViewById(R.id.child_table_opv2total);




        TextView opv3_0TextView= (TextView) rootView.findViewById(R.id.child_table_opv3_0);
        TextView opv3_1TextView= (TextView) rootView.findViewById(R.id.child_table_opv3_1);
        TextView opv3_2TextView= (TextView) rootView.findViewById(R.id.child_table_opv3_2);
        TextView opv3totalTextView= (TextView) rootView.findViewById(R.id.child_table_opv3total);



        TextView pcv1_0TextView= (TextView) rootView.findViewById(R.id.child_table_pcv1_0);
        TextView pcv1_1TextView= (TextView) rootView.findViewById(R.id.child_table_pcv1_1);
        TextView pcv1_2TextView= (TextView) rootView.findViewById(R.id.child_table_pcv1_2);
        TextView pcv1totalTextView= (TextView) rootView.findViewById(R.id.child_table_pcv1total);



        TextView pcv2_0TextView= (TextView) rootView.findViewById(R.id.child_table_pcv2_0);
        TextView pcv2_1TextView= (TextView) rootView.findViewById(R.id.child_table_pcv2_1);
        TextView pcv2_2TextView= (TextView) rootView.findViewById(R.id.child_table_pcv2_2);
        TextView pcv2totalTextView= (TextView) rootView.findViewById(R.id.child_table_pcv2total);


        TextView pcv3_0TextView= (TextView) rootView.findViewById(R.id.child_table_pcv3_0);
        TextView pcv3_1TextView= (TextView) rootView.findViewById(R.id.child_table_pcv3_1);
        TextView pcv3_2TextView= (TextView) rootView.findViewById(R.id.child_table_pcv3_2);
        TextView pcv3totalTextView= (TextView) rootView.findViewById(R.id.child_table_pcv3total);




        TextView penta1_0TextView= (TextView) rootView.findViewById(R.id.child_table_penta1_0);
        TextView penta1_1TextView= (TextView) rootView.findViewById(R.id.child_table_penta1_1);
        TextView penta1_2TextView= (TextView) rootView.findViewById(R.id.child_table_penta1_2);
        TextView penta1totalTextView= (TextView) rootView.findViewById(R.id.child_table_penta1total);



        TextView penta2_0TextView= (TextView) rootView.findViewById(R.id.child_table_penta2_0);
        TextView penta2_1TextView= (TextView) rootView.findViewById(R.id.child_table_penta2_1);
        TextView penta2_2TextView= (TextView) rootView.findViewById(R.id.child_table_penta2_2);
        TextView penta2totalTextView= (TextView) rootView.findViewById(R.id.child_table_penta2total);




        TextView penta3_0TextView= (TextView) rootView.findViewById(R.id.child_table_penta3_0);
        TextView penta3_1TextView= (TextView) rootView.findViewById(R.id.child_table_penta3_1);
        TextView penta3_2TextView= (TextView) rootView.findViewById(R.id.child_table_penta3_2);
        TextView penta3totalTextView= (TextView) rootView.findViewById(R.id.child_table_penta3total);



        TextView measles1_0TextView= (TextView) rootView.findViewById(R.id.child_table_measles1_0);
        TextView measles1_1TextView= (TextView) rootView.findViewById(R.id.child_table_measles1_1);
        TextView measles1_2TextView= (TextView) rootView.findViewById(R.id.child_table_measles1_2);
        TextView measles1totalTextView= (TextView) rootView.findViewById(R.id.child_table_measles1total);




        TextView measles2_0TextView= (TextView) rootView.findViewById(R.id.child_table_measles2_0);
        TextView measles2_1TextView= (TextView) rootView.findViewById(R.id.child_table_measles2_1);
        TextView measles2_2TextView= (TextView) rootView.findViewById(R.id.child_table_measles2_2);
        TextView measles2totalTextView= (TextView) rootView.findViewById(R.id.child_table_measles2total);




        TextView fullyimmunized_0TextView= (TextView) rootView.findViewById(R.id.child_table_fullyimmunized_0);
        TextView fullyimmunized_1TextView= (TextView) rootView.findViewById(R.id.child_table_fullyimmunized_1);
        TextView fullyimmunized_2TextView= (TextView) rootView.findViewById(R.id.child_table_fullyimmunized_2);
        TextView fullyimmunizedtotalTextView= (TextView) rootView.findViewById(R.id.child_table_fullyimmunizedtotal);

if(childObject!=null) {
    //setting values
    opv3_0TextView.setText(childObject.getColumnmaps().get("opv3_0") != null ? childObject.getColumnmaps().get("opv3_0") : "N/A");
    opv3_1TextView.setText(childObject.getColumnmaps().get("opv3_1") != null ? childObject.getColumnmaps().get("opv3_1") : "N/A");
    opv3_2TextView.setText(childObject.getColumnmaps().get("opv3_2") != null ? childObject.getColumnmaps().get("opv3_2") : "N/A");
    int opv3Total = Integer.parseInt(childObject.getColumnmaps().get("opv3_0") != null ? childObject.getColumnmaps().get("opv3_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv3_1") != null ? childObject.getColumnmaps().get("opv3_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv3_2") != null ? childObject.getColumnmaps().get("opv3_2") : "0");
    opv3totalTextView.setText(opv3Total + "");
    //setting values
    penta3_0TextView.setText(childObject.getColumnmaps().get("penta3_0") != null ? childObject.getColumnmaps().get("penta3_0") : "N/A");
    penta3_1TextView.setText(childObject.getColumnmaps().get("penta3_1") != null ? childObject.getColumnmaps().get("penta3_1") : "N/A");
    penta3_2TextView.setText(childObject.getColumnmaps().get("penta3_2") != null ? childObject.getColumnmaps().get("penta3_2") : "N/A");
    int penta3Total = Integer.parseInt(childObject.getColumnmaps().get("penta3_0") != null ? childObject.getColumnmaps().get("penta3_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta3_1") != null ? childObject.getColumnmaps().get("penta3_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta3_2") != null ? childObject.getColumnmaps().get("penta3_2") : "0");
    penta3totalTextView.setText(penta3Total + "");
    //setting values
    measles1_0TextView.setText(childObject.getColumnmaps().get("measles1_0") != null ? childObject.getColumnmaps().get("measles1_0") : "N/A");
    measles1_1TextView.setText(childObject.getColumnmaps().get("measles1_1") != null ? childObject.getColumnmaps().get("measles1_1") : "N/A");
    measles1_2TextView.setText(childObject.getColumnmaps().get("measles1_2") != null ? childObject.getColumnmaps().get("measles1_2") : "N/A");
    int measles1Total = Integer.parseInt(childObject.getColumnmaps().get("measles1_0") != null ? childObject.getColumnmaps().get("measles1_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("measles1_1") != null ? childObject.getColumnmaps().get("measles1_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("measles1_2") != null ? childObject.getColumnmaps().get("measles1_2") : "0");
    measles1totalTextView.setText(measles1Total + "");
    //setting values
    measles2_0TextView.setText(childObject.getColumnmaps().get("measles2_0") != null ? childObject.getColumnmaps().get("measles2_0") : "N/A");
    measles2_1TextView.setText(childObject.getColumnmaps().get("measles2_1") != null ? childObject.getColumnmaps().get("measles2_1") : "N/A");
    measles2_2TextView.setText(childObject.getColumnmaps().get("measles2_2") != null ? childObject.getColumnmaps().get("measles2_2") : "N/A");
    int measles2Total = Integer.parseInt(childObject.getColumnmaps().get("measles2_0") != null ? childObject.getColumnmaps().get("measles2_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("measles2_1") != null ? childObject.getColumnmaps().get("measles2_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("measles2_2") != null ? childObject.getColumnmaps().get("measles2_2") : "0");
    measles2totalTextView.setText(measles2Total + "");
    //setting values
    penta2_0TextView.setText(childObject.getColumnmaps().get("penta2_0") != null ? childObject.getColumnmaps().get("penta2_0") : "N/A");
    penta2_1TextView.setText(childObject.getColumnmaps().get("penta2_1") != null ? childObject.getColumnmaps().get("penta2_1") : "N/A");
    penta2_2TextView.setText(childObject.getColumnmaps().get("penta2_2") != null ? childObject.getColumnmaps().get("penta2_2") : "N/A");
    int penta2Total = Integer.parseInt(childObject.getColumnmaps().get("penta2_0") != null ? childObject.getColumnmaps().get("penta2_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta2_1") != null ? childObject.getColumnmaps().get("penta2_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta2_2") != null ? childObject.getColumnmaps().get("penta2_2") : "0");
    penta2totalTextView.setText(penta2Total + "");
//setting values
    penta1_0TextView.setText(childObject.getColumnmaps().get("penta1_0") != null ? childObject.getColumnmaps().get("penta1_0") : "N/A");
    penta1_1TextView.setText(childObject.getColumnmaps().get("penta1_1") != null ? childObject.getColumnmaps().get("penta1_1") : "N/A");
    penta1_2TextView.setText(childObject.getColumnmaps().get("penta1_2") != null ? childObject.getColumnmaps().get("penta1_2") : "N/A");
    int penta1Total = Integer.parseInt(childObject.getColumnmaps().get("penta1_0") != null ? childObject.getColumnmaps().get("penta1_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta1_1") != null ? childObject.getColumnmaps().get("penta1_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("penta1_2") != null ? childObject.getColumnmaps().get("penta1_2") : "0");
    penta1totalTextView.setText(penta1Total + "");
    //setting values
    pcv3_0TextView.setText(childObject.getColumnmaps().get("pcv3_0") != null ? childObject.getColumnmaps().get("pcv3_0") : "N/A");
    pcv3_1TextView.setText(childObject.getColumnmaps().get("pcv3_1") != null ? childObject.getColumnmaps().get("pcv3_1") : "N/A");
    pcv3_2TextView.setText(childObject.getColumnmaps().get("pcv3_2") != null ? childObject.getColumnmaps().get("pcv3_2") : "N/A");
    int pcv3Total = Integer.parseInt(childObject.getColumnmaps().get("pcv3_0") != null ? childObject.getColumnmaps().get("pcv3_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv3_1") != null ? childObject.getColumnmaps().get("pcv3_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv3_2") != null ? childObject.getColumnmaps().get("pcv3_2") : "0");
    pcv3totalTextView.setText(pcv3Total + "");
    //setting values
    pcv2_0TextView.setText(childObject.getColumnmaps().get("pcv2_0") != null ? childObject.getColumnmaps().get("pcv2_0") : "N/A");
    pcv2_1TextView.setText(childObject.getColumnmaps().get("pcv2_1") != null ? childObject.getColumnmaps().get("pcv2_1") : "N/A");
    pcv2_2TextView.setText(childObject.getColumnmaps().get("pcv2_2") != null ? childObject.getColumnmaps().get("pcv2_2") : "N/A");
    int pcv2Total = Integer.parseInt(childObject.getColumnmaps().get("pcv2_0") != null ? childObject.getColumnmaps().get("pcv2_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv2_1") != null ? childObject.getColumnmaps().get("pcv2_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv2_2") != null ? childObject.getColumnmaps().get("pcv2_2") : "0");
    pcv2totalTextView.setText(pcv2Total + "");
    //setting values
    pcv1_0TextView.setText(childObject.getColumnmaps().get("pcv1_0") != null ? childObject.getColumnmaps().get("pcv1_0") : "N/A");
    pcv1_1TextView.setText(childObject.getColumnmaps().get("pcv1_1") != null ? childObject.getColumnmaps().get("pcv1_1") : "N/A");
    pcv1_2TextView.setText(childObject.getColumnmaps().get("pcv1_2") != null ? childObject.getColumnmaps().get("pcv1_2") : "N/A");
    int pcv1Total = Integer.parseInt(childObject.getColumnmaps().get("pcv1_0") != null ? childObject.getColumnmaps().get("pcv1_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv1_1") != null ? childObject.getColumnmaps().get("pcv1_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("pcv1_2") != null ? childObject.getColumnmaps().get("pcv1_2") : "0");
    pcv1totalTextView.setText(pcv1Total + "");
    //setting values
    opv2_0TextView.setText(childObject.getColumnmaps().get("opv2_0") != null ? childObject.getColumnmaps().get("opv2_0") : "N/A");
    opv2_1TextView.setText(childObject.getColumnmaps().get("opv2_1") != null ? childObject.getColumnmaps().get("opv2_1") : "N/A");
    opv2_2TextView.setText(childObject.getColumnmaps().get("opv2_2") != null ? childObject.getColumnmaps().get("opv2_2") : "N/A");
    int opv2Total = Integer.parseInt(childObject.getColumnmaps().get("opv2_0") != null ? childObject.getColumnmaps().get("opv2_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv2_1") != null ? childObject.getColumnmaps().get("opv2_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv2_2") != null ? childObject.getColumnmaps().get("opv2_2") : "0");
    opv2totalTextView.setText(opv2Total + "");
    //setting values
    opv1_0TextView.setText(childObject.getColumnmaps().get("opv1_0") != null ? childObject.getColumnmaps().get("opv1_0") : "N/A");
    opv1_1TextView.setText(childObject.getColumnmaps().get("opv1_1") != null ? childObject.getColumnmaps().get("opv1_1") : "N/A");
    opv1_2TextView.setText(childObject.getColumnmaps().get("opv1_2") != null ? childObject.getColumnmaps().get("opv1_2") : "N/A");
    int opv1Total = Integer.parseInt(childObject.getColumnmaps().get("opv1_0") != null ? childObject.getColumnmaps().get("opv1_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv1_1") != null ? childObject.getColumnmaps().get("opv1_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv1_2") != null ? childObject.getColumnmaps().get("opv1_2") : "0");
    opv1totalTextView.setText(opv1Total + "");
    //setting values
    bcg0TextView.setText(childObject.getColumnmaps().get("bcg_0") != null ? childObject.getColumnmaps().get("bcg_0") : "N/A");
    bcg1TextView.setText(childObject.getColumnmaps().get("bcg_1") != null ? childObject.getColumnmaps().get("bcg_1") : "N/A");
    bcg2TextView.setText(childObject.getColumnmaps().get("bcg_2") != null ? childObject.getColumnmaps().get("bcg_2") : "N/A");
    int bcgTotal = Integer.parseInt(childObject.getColumnmaps().get("bcg_0") != null ? childObject.getColumnmaps().get("bcg_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("bcg_1") != null ? childObject.getColumnmaps().get("bcg_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("bcg_2") != null ? childObject.getColumnmaps().get("bcg_2") : "0");

    bcgtotalTextView.setText(bcgTotal + "");
    //setting values
    opv0_0TextView.setText(childObject.getColumnmaps().get("opv0_0") != null ? childObject.getColumnmaps().get("opv0_0") : "N/A");
    opv0_1TextView.setText(childObject.getColumnmaps().get("opv0_1") != null ? childObject.getColumnmaps().get("opv0_1") : "N/A");
    opv0_2TextView.setText(childObject.getColumnmaps().get("opv0_2") != null ? childObject.getColumnmaps().get("opv0_2") : "N/A");
    int opv0Total = Integer.parseInt(childObject.getColumnmaps().get("opv0_0") != null ? childObject.getColumnmaps().get("opv0_0") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv0_1") != null ? childObject.getColumnmaps().get("opv0_1") : "0") + Integer.parseInt(childObject.getColumnmaps().get("opv0_2") != null ? childObject.getColumnmaps().get("opv0_2") : "0");

    opv0totalTextView.setText(opv0Total + "");
}
        // Inflate the layout for this fragment
        return rootView;//inflater.inflate(R.layout.fragment_child_vaccine_table, container, false);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.fragment_child_vaccine_table);
    }
}
