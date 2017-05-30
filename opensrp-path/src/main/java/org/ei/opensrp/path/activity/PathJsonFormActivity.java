package org.ei.opensrp.path.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.fragment.PathJsonFormFragment;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import util.JsonFormUtils;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathJsonFormActivity extends JsonFormActivity {

    private int generatedId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeFormFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, PathJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        refreshCalculateLogic();
    }

    private void refreshCalculateLogic() {
//        Toast.makeText(this,currentJsonState(),Toast.LENGTH_LONG).show();
        String label = "";
        JSONObject object = getStep("step1");
        try {
            if (object.getString("title").contains("Stock Received")) {
                int currentBalance = 0;
                int displaybalance = 0;
                JSONArray fields = object.getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject questions = fields.getJSONObject(i);
                    if (questions.has("key")) {
                        if (questions.getString("key").equalsIgnoreCase("Date_Stock_Received")) {
                            if (questions.has("value")) {
                                Date encounterDate = new Date();
                                label = questions.getString("value");
                                if(label != null){

                                    if (StringUtils.isNotBlank(label)) {
                                        Date dateTime = JsonFormUtils.formatDate(label, false);
                                        if (dateTime != null) {
                                            encounterDate = dateTime;
                                        }
                                    }
                                }
                                String vaccineName = object.getString("title").replace("Stock Received","").trim();
                                StockRepository str = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(), Context.getInstance().alertService());
                                currentBalance = str.getBalanceFromNameAndDate(vaccineName,encounterDate.getTime());


                            }
                        }
                        if (questions.getString("key").equalsIgnoreCase("Vials_Received")) {
                            if (questions.has("value")) {

                                String vialsvalue = questions.getString("value");
                                if(vialsvalue != null && !vialsvalue.equalsIgnoreCase("")){
                                    displaybalance = currentBalance + Integer.parseInt(vialsvalue);
                                }
                        }

                        }
                    }

                ArrayList<View> views = getFormDataViews();
                    if(generatedId == -1) {
                        TextView balancesheet = new TextView(this);
                        generatedId = ViewUtil.generateViewId();
                        balancesheet.setId(generatedId);
                        balancesheet.setTag(com.vijay.jsonwizard.R.id.address,"");

                        balancesheet.setText("" + displaybalance);
                        addFormDataView(balancesheet);
                    }else{
                        ((TextView)findViewById(generatedId)).setText(""+displaybalance);
                    }
//                for(int j = 0;j<views.size();j++){
//                    if(((String)views.get(j).getTag(com.vijay.jsonwizard.R.id.key)).equalsIgnoreCase("balance")){
////                        Toast.makeText(this,""+displaybalance,Toast.LENGTH_LONG).show();
////                        ((EditText)views.get(j)).setText(""+currentBalance);
//
//
//                    }
//                }
            }
        }
        }catch(JSONException e){
                e.printStackTrace();
            }


        }


}
