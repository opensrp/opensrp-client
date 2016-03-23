package org.ei.opensrp.vaccinator.field;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaccinator.R;

import java.util.Map;

/**
 * Created by engrmahmed14@gmail.com on 12/13/15.
 */
public class FieldMonitorDailyDetailActivity extends Activity {
    public static Map<String, String> userMap;
    public static Map<String, String> usedVaccines;
    public static CommonPersonObjectClient fieldclient;
    //private Map<String, String> WastedVaccines;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

//        Context context = Context.getInstance();


            //setting view
            setContentView(R.layout.field_detail_daily_activity);

            TextView vaccinatorIdTextView =(TextView)findViewById(R.id.fielddetail_vaccinatorid);
            TextView vaccinatorNameTextView =(TextView)findViewById(R.id.fielddetail_vaccinator_name);
            TextView centerIdTextView =(TextView)findViewById(R.id.fielddetail_centerid);
            TextView ucTextView =(TextView)findViewById(R.id.fielddetail_uc);
        vaccinatorIdTextView.setText(userMap.get("provider_id"));
        vaccinatorNameTextView.setText(userMap.get("provider_id"));
        centerIdTextView.setText(userMap.get("provider_location_id"));
        ucTextView.setText(userMap.get("provider_location_id"));


            TextView bcgUsedTextView =(TextView)findViewById(R.id.fielddetail_bcg_used);
            TextView bcgWastedTextView =(TextView)findViewById(R.id.fielddetail_bcg_wasted);

    // String bcgWasted=fieldclient.getDetails().get("bcg_wasted");
        String bcgWasted=fieldclient.getDetails().get("bcg_wasted")!=null?fieldclient.getDetails().get("bcg_wasted"):"N/A";
        bcgWastedTextView.setText(bcgWasted);
        bcgUsedTextView.setText(usedVaccines.get("bcg")!=null?usedVaccines.get("bcg"):"N/A");



        TextView opvUsedTextView =(TextView)findViewById(R.id.fielddetail_opv_used);
        TextView opvWastedTextView =(TextView)findViewById(R.id.fielddetail_opv_wasted);
        opvWastedTextView.setText(fieldclient.getDetails().get("opv_wasted")!=null?fieldclient.getDetails().get("opv_wasted"):"N/A");
        opvUsedTextView.setText(usedVaccines.get("opv")!=null?usedVaccines.get("opv"):"N/A");

        TextView ipvUsedTextView =(TextView)findViewById(R.id.fielddetail_ipv_used);
        TextView ipvWastedTextView =(TextView)findViewById(R.id.fielddetail_ipv_wasted);
        ipvWastedTextView.setText(fieldclient.getDetails().get("ipv_wasted")!=null?fieldclient.getDetails().get("ipv_wasted"):"N/A");
        ipvUsedTextView.setText(usedVaccines.get("ipv")!=null?usedVaccines.get("ipv"):"N/A");



        TextView pentavalentUsedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_used);
        TextView pentavalentWastedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_wasted);
        pentavalentWastedTextView.setText(fieldclient.getDetails().get("penta_wasted")!=null?fieldclient.getDetails().get("penta_wasted"):"N/A");
        pentavalentUsedTextView.setText(usedVaccines.get("pentavalent")!=null?usedVaccines.get("pentavalent"):"N/A");




        TextView measlesUsedTextView =(TextView)findViewById(R.id.fielddetail_measles_used);
        TextView measlesWastedTextView =(TextView)findViewById(R.id.fielddetail_measles_wasted);
        measlesWastedTextView.setText(fieldclient.getDetails().get("measles_wasted")!=null?fieldclient.getDetails().get("measles_wasted"):"N/A");
        measlesUsedTextView.setText(usedVaccines.get("measles")!=null?usedVaccines.get("measles"):"N/A");


        TextView safetyboxesUsedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_used);
        TextView safetyboxesWastedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_wasted);
        safetyboxesWastedTextView.setText(fieldclient.getDetails().get("safety_boxes_wasted")!=null?fieldclient.getDetails().get("safety_boxes_wasted"):"N/A");
        safetyboxesUsedTextView.setText(usedVaccines.get("safety_boxes")!=null?usedVaccines.get("safety_boxes"):"N/A");



        TextView syringesUsedTextView =(TextView)findViewById(R.id.fielddetail_syringes_used);
        TextView syringesWastedTextView =(TextView)findViewById(R.id.fielddetail_syringes_wasted);
        syringesWastedTextView.setText(fieldclient.getDetails().get("syringes_wasted")!=null?fieldclient.getDetails().get("syringes_wasted"):"N/A");
        syringesUsedTextView.setText(usedVaccines.get("syringes")!=null?usedVaccines.get("syringes"):"N/A");



        TextView tetanusUsedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_used);
        TextView tetanusWastedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_wasted);
        tetanusWastedTextView.setText(fieldclient.getDetails().get("tt_wasted")!=null?fieldclient.getDetails().get("tt_wasted"):"N/A");
        tetanusUsedTextView.setText(usedVaccines.get("tt")!=null?usedVaccines.get("tt"):"N/A");




        TextView dilutantsUsedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_used);
        TextView dilutantsWastedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_wasted);
        dilutantsWastedTextView.setText(fieldclient.getDetails().get("dilutants_wasted")!=null?fieldclient.getDetails().get("dilutants_wasted"):"N/A");
        dilutantsUsedTextView.setText(usedVaccines.get("dilutants")!=null?usedVaccines.get("dilutants"):"N/A");



        TextView pcvUsedTextView =(TextView)findViewById(R.id.fielddetail_pcv_used);
        TextView pcvWastedTextView =(TextView)findViewById(R.id.fielddetail_pcv_wasted);
        pcvWastedTextView.setText(fieldclient.getDetails().get("pcv_wasted")!=null?fieldclient.getDetails().get("pcv_wasted"):"N/A");
        pcvUsedTextView.setText(usedVaccines.get("pcv")!=null?usedVaccines.get("pcv"):"N/A");


    }
}
