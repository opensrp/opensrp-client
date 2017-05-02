package org.ei.opensrp.path.viewComponents;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by raihan on 2/26/17.
 */
public class WidgetFactory {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public View createTableRow(LayoutInflater inflater, ViewGroup container, String labelString, String valueString){
        View rows = inflater.inflate(R.layout.tablerows, container, false);
        TextView label = (TextView)rows.findViewById(R.id.label);
        TextView value = (TextView)rows.findViewById(R.id.value);

        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }
    public View createTableRowForWeight(LayoutInflater inflater, ViewGroup container, String labelString, String valueString, boolean editenabled,View.OnClickListener listener){
        View rows = inflater.inflate(R.layout.tablerows_weight, container, false);
        TextView label = (TextView)rows.findViewById(R.id.label);
        TextView value = (TextView)rows.findViewById(R.id.value);
        Button edit = (Button)rows.findViewById(R.id.edit);
        if(editenabled){
            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(listener);
        }else{
            edit.setVisibility(View.INVISIBLE);
        }
        label.setText(labelString);
        value.setText(valueString);
        return rows;
    }
    public View createWeightWidget(LayoutInflater inflater, ViewGroup container, HashMap<String,String> last_five_weight_map,ArrayList<View.OnClickListener> listeners,ArrayList<Boolean> editenabled){
        View weightwidget = inflater.inflate(R.layout.weightwidget, container, false);
        LinearLayout tableLayout = (LinearLayout) weightwidget.findViewById(R.id.weightvalues);
        ViewGroup.LayoutParams weightvaluesparams = tableLayout.getLayoutParams();

        int i = 0;
        for (Map.Entry<String, String> entry : last_five_weight_map.entrySet())
        {
            View view = createTableRowForWeight(inflater,tableLayout,""+entry.getKey(),""+entry.getValue(),editenabled.get(i),listeners.get(i));

            tableLayout.addView(view);
            i++;
        }

        return weightwidget;
    }
    public View createImmunizationWidget(LayoutInflater inflater, ViewGroup container, List<Vaccine> vaccines, boolean editmode){

        View immunization_widget = inflater.inflate(R.layout.immunization_widget, container, false);
        TableLayout immunizationholder = (TableLayout)immunization_widget.findViewById(R.id.immunizationholder);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.
//        immunizationholder.setPadding(10,10,10,10);

        layoutParams.setMargins(10, 10, 10, 10);
        for(int i = 0 ;i<vaccines.size();i++){
            immunizationholder.addView(createImmunizationRow(inflater,container,vaccines.get(i),editmode),layoutParams);
        }

        return immunization_widget;
    }
    public View createImmunizationRow(LayoutInflater inflater, ViewGroup container, Vaccine vaccines, boolean editmode){

        View vaccineRow = inflater.inflate(R.layout.vaccinate_row_view, container, false);

//        TableLayout tableLayout = (TableLayout)vaccineRow.findViewById(R.id.weightvalues);
        TextView vaccinename = (TextView)vaccineRow.findViewById(R.id.vaccine);
        TextView date = (TextView)vaccineRow.findViewById(R.id.date);
        Button status = (Button)vaccineRow.findViewById(R.id.status);
        Button undobutton = (Button)vaccineRow.findViewById(R.id.undo);
        if(editmode) {
            undobutton.setVisibility(View.VISIBLE);
        }else{
            undobutton.setVisibility(View.GONE);
        }
        vaccinename.setText(vaccines.getName());
        status.setBackgroundColor(Context.getInstance().getColorResource(R.color.alert_complete_green));
        date.setText(DATE_FORMAT.format(vaccines.getDate()));
        return vaccineRow;
    }
}
