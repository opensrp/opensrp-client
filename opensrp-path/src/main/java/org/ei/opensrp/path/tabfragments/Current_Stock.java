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

import org.ei.opensrp.Context;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.FormUtils;
import org.json.JSONObject;

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
        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = Context.getInstance();
                Intent intent = new Intent(getActivity().getApplicationContext(), PathJsonFormActivity.class);
                try {
                    JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("stock_received_form");
                    String formmetadata = form.toString();
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


                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }
}
