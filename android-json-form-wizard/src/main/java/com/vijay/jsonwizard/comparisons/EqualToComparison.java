package com.vijay.jsonwizard.comparisons;

import android.util.Log;

import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

public class EqualToComparison extends Comparison {
    private static final String TAG = "EqualToComparison";
    @Override
    public boolean compare(String a, String type, String b) {
        try {
            switch (type) {
                case TYPE_STRING:
                    if (a == null) a = DEFAULT_STRING;
                    return a.compareTo(b) == 0;
                case TYPE_NUMERIC:
                    if (a == null) a = DEFAULT_NUMERIC;
                    if (b == null) b = DEFAULT_NUMERIC;
                    return Double.valueOf(a) == Double.valueOf(b);
                case TYPE_DATE:
                    if (a == null) a = DEFAULT_DATE;
                    if (b == null) b = DEFAULT_DATE;
                    Date dateA = DatePickerFactory.DATE_FORMAT.parse(a);
                    Date dateB = DatePickerFactory.DATE_FORMAT.parse(b);
                    return dateA.getTime() == dateB.getTime();
                case TYPE_ARRAY:
                    if (a == null) a = DEFAULT_ARRAY;
                    if (b == null) b = DEFAULT_ARRAY;

                    // An array is only equal to another if they have the same number of items
                    // and all these items are in both arrays
                    try {
                        JSONArray aArray = new JSONArray(a);
                        JSONArray bArray = new JSONArray(b);

                        ArrayList<String> aList = new ArrayList<>();
                        for(int i = 0; i < aArray.length(); i++) {
                            aList.add(aArray.getString(i));
                        }

                        ArrayList<String> bList = new ArrayList<>();
                        for(int i = 0; i < bArray.length(); i++) {
                            bList.add(bArray.getString(i));
                        }

                        aList.removeAll(bList);

                        return aList.size() == 0;
                    } catch (JSONException e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String getFunctionName() {
        return "equalTo";
    }
}