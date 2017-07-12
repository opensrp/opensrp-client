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

    public void createWeightWidget(LayoutInflater inflater, LinearLayout fragmentContainer, HashMap<String, String> last_five_weight_map, ArrayList<View.OnClickListener> listeners, ArrayList<Boolean> editenabled) {

        LinearLayout tableLayout = (LinearLayout) fragmentContainer.findViewById(R.id.weightvalues);
        tableLayout.removeAllViews();
        ViewGroup.LayoutParams weightvaluesparams = tableLayout.getLayoutParams();

        int i = 0;
        for (Map.Entry<String, String> entry : last_five_weight_map.entrySet()) {
            View view = createTableRowForWeight(inflater, tableLayout, "" + entry.getKey(), "" + entry.getValue(), editenabled.get(i), listeners.get(i));

            tableLayout.addView(view);
            i++;
        }
    }
}
