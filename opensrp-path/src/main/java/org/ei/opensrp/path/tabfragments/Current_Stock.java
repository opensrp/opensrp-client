package org.ei.opensrp.path.tabfragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.activity.StockControlActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.FormUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.JsonFormUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Current_Stock#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Current_Stock extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String KEY = "key";
    private static final String VALUE ="value" ;
    public static final String STEP1 = "step1";
    public static final String FIELDS = "fields";
    public static final SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Current_Stock() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Current_Stock.
     */
    // TODO: Rename and change types and number of parameters
    public static Current_Stock newInstance(String param1, String param2) {
        Current_Stock fragment = new Current_Stock();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current__stock, container, false);
        Button received = (Button)view.findViewById(R.id.received);
        TextView vaccine_name = (TextView)view.findViewById(R.id.name);
        vaccine_name.setText(((StockControlActivity)getActivity()).vaccine_type.getName() + " Stock: ");

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Context.getInstance();
                Intent intent = new Intent(getActivity().getApplicationContext(), PathJsonFormActivity.class);
                try {
                    JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("stock_received_form");
                    String vaccine_name = ((StockControlActivity)getActivity()).vaccine_type.getName();
                    String formmetadata = form.toString().replace("[vaccine]",vaccine_name);
                    intent.putExtra("json", formmetadata);
                    startActivityForResult(intent, REQUEST_CODE_GET_JSON);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if (resultCode == getActivity().RESULT_OK) {

                try {
                    String jsonString = data.getStringExtra("json");
                    Log.d("JSONResult", jsonString);
                    processStock(jsonString);

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }

    private void processStock(String jsonString) {
        JSONObject jsonForm = null;
        try {
            Context context = Context.getInstance();
            jsonForm = new JSONObject(jsonString);
            JSONArray fields = fields(jsonForm);
            String Date_Stock_Received = getFieldValue(fields, "Date_Stock_Received");
            String Received_Stock_From = getFieldValue(fields, "Received_Stock_From");
            if(Received_Stock_From.equalsIgnoreCase("DHO")){
                Received_Stock_From = getFieldValue(fields, "Received_Stock_From");
            }else{
                Received_Stock_From = getFieldValue(fields, "Received_Stock_From_Other");
            }
            String vials_received = getFieldValue(fields, "Vials_Received");

            StockRepository str = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(Date_Stock_Received)) {
                Date dateTime = formatDate(Date_Stock_Received, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }



            Stock stock = new Stock(null,"received",allSharedPreferences.fetchRegisteredANM(),Integer.parseInt(vials_received),encounterDate,Received_Stock_From,StockRepository.TYPE_Unsynced,System.currentTimeMillis(),""+((StockControlActivity)(getActivity())).vaccine_type.getId());
            str.add(stock);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public static Date formatDate(String dateString, boolean startOfToday) {
        try {

            if (StringUtils.isBlank(dateString)) {
                return null;
            }

            if (dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
                return dd_MM_yyyy.parse(dateString);
            } else if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return DateUtil.parseDate(dateString);
            }

        } catch (ParseException e) {
            Log.e("parse date error", "", e);
        }

        return null;
    }
    private static JSONArray fields(JSONObject jsonForm) {
        try {

            JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Log.e("JSonExp in currentstock", "", e);
        }
        return null;
    }
    private static String getString(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getString(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    private static String getFieldValue(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return getString(jsonObject, VALUE);
            }
        }
        return null;
    }
    private static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            return null;

        }
    }
}
