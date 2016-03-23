package org.ei.opensrp.vaccinator.field;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaccinator.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by engrmahmed14@gmail.com on 12/13/15.
 */
public class FieldMonitorMonthlyDetailActivity extends Activity {

    public static Map<String, String> usedVaccines;
    public static Map<String, String> userMap;
    public static CommonPersonObjectClient fieldclient;
    public static HashMap<String, String> wastedVaccines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.field_detail_monthly_activity);

        TextView vaccinatorIdTextView =(TextView)findViewById(R.id.fielddetail_vaccinatorid_monthly);
        TextView vaccinatorNameTextView =(TextView)findViewById(R.id.fielddetail_vaccinator_name_monthly);
        TextView centerIdTextView =(TextView)findViewById(R.id.fielddetail_centerid_monthly);
        TextView ucTextView =(TextView)findViewById(R.id.fielddetail_uc_monthly);
        vaccinatorIdTextView.setText(userMap.get("provider_id"));
        vaccinatorNameTextView.setText(userMap.get("provider_id"));
        centerIdTextView.setText(userMap.get("provider_location_id"));
        ucTextView.setText(userMap.get("provider_location_id"));
        TextView bcgUsedTextView =(TextView)findViewById(R.id.fielddetail_bcg_used_monthly);
        TextView bcgWastedTextView =(TextView)findViewById(R.id.fielddetail_bcg_inhand_monthly);


        String bcgWasted=fieldclient.getDetails().get("bcg_balance_in_hand")!=null?fieldclient.getDetails().get("bcg_balance_in_hand"):"N/A";
        bcgWastedTextView.setText(bcgWasted);
        bcgUsedTextView.setText(usedVaccines.get("bcg")!=null?usedVaccines.get("bcg"):"0");



        TextView opvUsedTextView =(TextView)findViewById(R.id.fielddetail_opv_used_monthly);
        TextView opvWastedTextView =(TextView)findViewById(R.id.fielddetail_opv_inhand_monthly);
        opvWastedTextView.setText(fieldclient.getDetails().get("opv_balance_in_hand") != null ? fieldclient.getDetails().get("opv_balance_in_hand") : "N/A");
       int opv1= Integer.parseInt(usedVaccines.get("opv_0") != null ? usedVaccines.get("opv_0") : "0");
        int opv2= Integer.parseInt(usedVaccines.get("opv_1") != null ? usedVaccines.get("opv_1") : "0");
        int opv3= Integer.parseInt(usedVaccines.get("opv_2") != null ? usedVaccines.get("opv_2") : "0");
        int opvUsed= opv1+opv2+opv3;//Integer.parseInt(usedVaccines.get("opv_0")!=null?usedVaccines.get("opv_0"):"0")+Integer.parseInt(usedVaccines.get("opv_1")!=null?usedVaccines.get("opv_1"):"0")+Integer.parseInt(usedVaccines.get("opv_2)"!=null?usedVaccines.get("opv_2"):"0"));
        opvUsedTextView.setText(opvUsed>0 ?opvUsed+"":"0");

        TextView ipvUsedTextView =(TextView)findViewById(R.id.fielddetail_ipv_used_monthly);
        TextView ipvWastedTextView =(TextView)findViewById(R.id.fielddetail_ipv_inhand_monthly);
        ipvWastedTextView.setText(fieldclient.getDetails().get("ipv_balance_in_hand")!=null?fieldclient.getDetails().get("ipv_balance_in_hand"):"N/A");
        int pcvUsed= Integer.parseInt(usedVaccines.get("pcv_1") != null ? usedVaccines.get("pcv_1") : "0")+ Integer.parseInt(usedVaccines.get("pcv_2") != null ? usedVaccines.get("pcv_2") : "0")+ Integer.parseInt(usedVaccines.get("pcv_3") != null ? usedVaccines.get("pcv_3") : "0");
        ipvUsedTextView.setText(pcvUsed>0 ?opvUsed+"":"0");



        TextView pentavalentUsedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_used_monthly);
        TextView pentavalentWastedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_inhand_monthly);
        pentavalentWastedTextView.setText(fieldclient.getDetails().get("penta_balance_in_hand")!=null?fieldclient.getDetails().get("penta_balance_in_hand"):"N/A");
        int pentaUsed= Integer.parseInt(usedVaccines.get("pentavalent_1") != null ? usedVaccines.get("pentavalent_1") : "0")+ Integer.parseInt(usedVaccines.get("pentavalent_2") != null ? usedVaccines.get("pentavalent_2") : "0")+ Integer.parseInt(usedVaccines.get("pentavalent_3") != null ? usedVaccines.get("pentavalent_3") : "0");
        pentavalentUsedTextView.setText(pentaUsed>0 ?opvUsed+"":"0");




        TextView measlesUsedTextView =(TextView)findViewById(R.id.fielddetail_measles_used_monthly);
        TextView measlesWastedTextView =(TextView)findViewById(R.id.fielddetail_measles_inhand_monthly);
        measlesWastedTextView.setText(fieldclient.getDetails().get("measles_balance_in_hand")!=null?fieldclient.getDetails().get("measles_balance_in_hand"):"N/A");
        int measlesUsed= Integer.parseInt(usedVaccines.get("measles_1") != null ? usedVaccines.get("measles_1") : "0")+ Integer.parseInt(usedVaccines.get("measles_2") != null ? usedVaccines.get("measles_2") : "0");
        measlesUsedTextView.setText(measlesUsed>0 ?opvUsed+"":"0");


        TextView safetyboxesUsedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_used_monthly);
        TextView safetyboxesWastedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_inhand_monthly);
        safetyboxesWastedTextView.setText(fieldclient.getDetails().get("safety_boxes_balance_in_hand")!=null?fieldclient.getDetails().get("safety_boxes_balance_in_hand"):"N/A");
     //   safetyboxesUsedTextView.setText(usedVaccines.get("safety_boxes")!=null?usedVaccines.get("safety_boxes"):"N/A");



        TextView syringesUsedTextView =(TextView)findViewById(R.id.fielddetail_syringes_used_monthly);
        TextView syringesWastedTextView =(TextView)findViewById(R.id.fielddetail_syringes_inhand_monthly);
        syringesWastedTextView.setText(fieldclient.getDetails().get("syringes_balance_in_hand")!=null?fieldclient.getDetails().get("syringes_balance_in_hand"):"N/A");
       syringesUsedTextView.setText(usedVaccines.get("syringes") != null ? usedVaccines.get("syringes"):"N/A");



        TextView tetanusUsedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_used_monthly);
        TextView tetanusWastedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_inhand_monthly);
        tetanusWastedTextView.setText(fieldclient.getDetails().get("tt_balance_in_hand")!=null?fieldclient.getDetails().get("tt_balance_in_hand"):"N/A");
        tetanusUsedTextView.setText(usedVaccines.get("tt")!=null?usedVaccines.get("tt"):"N/A");




        TextView dilutantsUsedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_used_monthly);
        TextView dilutantsWastedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_inhand_monthly);
        dilutantsWastedTextView.setText(fieldclient.getDetails().get("dilutants_balance_in_hand")!=null?fieldclient.getDetails().get("dilutants_balance_in_hand"):"N/A");
      //  dilutantsUsedTextView.setText(usedVaccines.get("dilutants")!=null?usedVaccines.get("dilutants"):"N/A");



    /*    TextView pcvUsedTextView =(TextView)findViewById(R.id.fielddetail_pcv_used);
        TextView pcvWastedTextView =(TextView)findViewById(R.id.fielddetail_pcv_inhand_monthly);
        pcvWastedTextView.setText(fieldclient.getDetails().get("pcv_balance_in_hand")!=null?fieldclient.getDetails().get("pcv_balance_in_hand"):"N/A");
        pcvUsedTextView.setText(usedVaccines.get("pcv")!=null?usedVaccines.get("pcv"):"N/A");

*/


    }
}
