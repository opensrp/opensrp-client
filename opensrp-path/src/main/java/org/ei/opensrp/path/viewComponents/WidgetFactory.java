package org.ei.opensrp.path.viewComponents;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.path.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.DateUtils;


/**
 * Created by raihan on 2/26/17.
 */
public class WidgetFactory {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public View createTableRow(LayoutInflater inflater, ViewGroup container, String labelString, String valueString) {
        View rows = inflater.inflate(R.layout.tablerows, container, false);
        TextView label = (TextView) rows.findViewById(R.id.label);
        TextView value = (TextView) rows.findViewById(R.id.value);

        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }

    public View createTableRowForWeight(LayoutInflater inflater, ViewGroup container, String labelString, String valueString, boolean editenabled, View.OnClickListener listener) {
        View rows = inflater.inflate(R.layout.tablerows_weight, container, false);
        TextView label = (TextView) rows.findViewById(R.id.label);
        TextView value = (TextView) rows.findViewById(R.id.value);
        Button edit = (Button) rows.findViewById(R.id.edit);
        if (editenabled) {
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(listener);
        } else {
            edit.setVisibility(View.INVISIBLE);
        }
        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }

    public void createWeightWidget(LayoutInflater inflater, LinearLayout fragmentContainer, HashMap<Long, Pair<Long, String>> last_five_weight_map, ArrayList<View.OnClickListener> listeners, ArrayList<Boolean> editenabled) {

        LinearLayout tableLayout = (LinearLayout) fragmentContainer.findViewById(R.id.weightvalues);
        tableLayout.removeAllViews();
        Long [] timediffArray = new Long [last_five_weight_map.size()];
        String [] weightArray = new String [last_five_weight_map.size()];

        Long tempValueHolder = 0l;
        String weightvalueHolder = "";
        for (Map.Entry<Long, Pair<Long, String>> entry : last_five_weight_map.entrySet()) {
            Pair<Long, String> pair = entry.getValue();
            Long timediff = pair.first;
            String weightString = pair.second;
            for(int j = 0;j<timediffArray.length;j++){
                if(timediffArray[j] == null){
                    timediffArray[j] = timediff;
                    weightArray[j] = weightString;
                    break;
                }else if(timediff<timediffArray[j]){
                    tempValueHolder = timediffArray[j];
                    timediffArray[j] = timediff;
                    timediff = tempValueHolder;

                    weightvalueHolder = weightArray [j];
                    weightArray[j] = weightString;
                    weightString = weightvalueHolder;
                }
            }

        }


        for(int i = timediffArray.length-1; i > -1;i--){
            View view = createTableRowForWeight(inflater, tableLayout, DateUtils.getDuration(timediffArray[i]), weightArray[i], editenabled.get(i), listeners.get(i));

            tableLayout.addView(view);
        }
    }
}
