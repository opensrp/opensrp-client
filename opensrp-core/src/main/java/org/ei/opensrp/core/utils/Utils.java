/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ei.opensrp.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.ei.opensrp.core.R;
import org.ei.opensrp.core.db.repository.RegisterRepository;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.ei.opensrp.util.StringUtil.humanizeAndDoUPPERCASE;


/**
 * @author Maimoona
 * Class containing some static utility methods.
 */
public class Utils {
    private static final SimpleDateFormat UI_DF = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat UI_DTF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DB_DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Utils() {};

    public static JSONObject providerDetails(){
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
        try {
            JSONObject u = new JSONObject(context.allSettings().fetchUserInformation());
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String humanizeAndUppercase(String value, String... skipTokens){
        String res = humanizeAndDoUPPERCASE(value);
        for (String s: skipTokens) {
            res = res.replaceAll("(?i)"+s, s);
        }

        return res;
    }

    public static CommonPersonObject convertToCommonPersonObject(CommonPersonObjectClient client){
        CommonPersonObject co = new CommonPersonObject(client.getCaseId(), null, client.getDetails(), null);
        co.setColumnmaps(client.getColumnmaps());
        return co;
    }

    public static int ageInYears(CommonPersonObjectClient person, String dobField, ByColumnAndByDetails columnOrDetail, boolean suppressException){
        return ageInYears(person.getColumnmaps(), person.getDetails(), dobField, columnOrDetail, suppressException);
    }

    public static int ageInYears(CommonPersonObject person, String dobField, ByColumnAndByDetails columnOrDetail, boolean suppressException){
        return ageInYears(person.getColumnmaps(), person.getDetails(), dobField, columnOrDetail, suppressException);
    }

    public static int ageInYears(Map<String, String> columns, Map<String, String> details, String dobField, ByColumnAndByDetails columnOrDetail, boolean suppressException){
        int age = -1;
        try{
            age = Years.yearsBetween(new DateTime(getValue(columnOrDetail.equals(ByColumnAndByDetails.byColumn)?columns:details, dobField, false)), DateTime.now().plusDays(1)).getYears();
        }
        catch (Exception e){if (suppressException == false) throw new RuntimeException(e);}
        return age;
    }

    public static String convertDateFormat(String date, boolean suppressException){
        try{
            return UI_DF.format(DB_DF.parse(date));
        }
        catch (ParseException e) {
            if(!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static Date toDate(String date, boolean suppressException){
        try{
            return DB_DF.parse(date);
        }
        catch (ParseException e) {
            if(!suppressException) throw new RuntimeException(e);
        }
        return null;
    }

    public static String toDisplayDate(Date date){
        return UI_DF.format(date);
    }

    public static String toSqlDate(Date date){
        return DB_DF.format(date);
    }

    public static String toSqlDate(String date, boolean suppressException){
        try {
            return DB_DF.format(UI_DF.parse(date));
        } catch (ParseException e) {
            if(!suppressException) throw new RuntimeException(e);
        }
        return null;
    }

    public static String convertDateFormat(String date, String defaultV, boolean suppressException){
        try{
            return UI_DF.format(DB_DF.parse(date));
        }
        catch (ParseException e) {
            if(!suppressException) throw new RuntimeException(e);
        }
        return StringUtils.isNotBlank(defaultV)?defaultV:"";
    }

    public static String convertDateFormat(DateTime date){
        return UI_DF.format(date.toDate());
    }

    public static String convertDateTimeFormat(String date, boolean suppressException){
        try{
            return UI_DTF.format(DB_DTF.parse(date.replace("T", " ")));
        }
        catch (ParseException e) {
            e.printStackTrace();
            if(!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static void fillWithIdentifier(TextView v, Map<String, String> cm, String identifierType, boolean humanize) throws JSONException {
        JSONObject idl = new JSONObject((String) cm.get("identifiers"));
        v.setText(getValue(cm, idl.getString(identifierType), humanize));
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, boolean humanize){
        v.setText(getValue(cm, field, humanize));
    }

    public static void fillValue(TextView v, Map<String, String> cm, String field, String defaultV, boolean humanize){
        String val = getValue(cm, field, humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        v.setText(val);
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, boolean humanize){
        v.setText(getValue(pc, field, humanize));
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, String defaultV, boolean humanize){
        String val = getValue(pc, field, humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        v.setText(val);
    }

    public static final int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
            return context.getResources().getColor(id, context.getTheme());
        }else {
            return context.getResources().getColor(id);
        }
    }

    public static String getColorValue(Context cxt, AlertStatus alertStatus){
        if(alertStatus.equals(AlertStatus.upcoming)){
            return "#"+Integer.toHexString(cxt.getResources().getColor(R.color.alert_upcoming)).substring(2);
        }
        if(alertStatus.equals(AlertStatus.normal)){
            return "#"+Integer.toHexString(cxt.getResources().getColor(R.color.alert_normal)).substring(2);
        }
        if(alertStatus.equals(AlertStatus.urgent)){
            return "#"+Integer.toHexString(cxt.getResources().getColor(R.color.alert_urgent)).substring(2);
        }
        else {
            return "#"+Integer.toHexString(cxt.getResources().getColor(R.color.alert_na)).substring(2);
        }
    }

    public static void addToList(Map<String, String> locations, Map<String, TreeNode<String, Location>> locationMap, String locationTag) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            boolean tagFound = false;
            if (entry.getValue() != null) {
                Location l = entry.getValue().getNode();

                if(l.getTags() != null){
                    for (String s : l.getTags()) {
                        if (s.equalsIgnoreCase(locationTag)) {
                            locations.put(locationTag, l.getName());
                            tagFound = true;
                        }
                    }
                }
            }
            if (!tagFound) {
                if (entry.getValue().getChildren() != null) {
                    addToList(locations, entry.getValue().getChildren(), locationTag);
                }
            }
        }
    }

    public static void fillValue(TextView v, String value){
        v.setText(Html.fromHtml(value));
    }

    public static String formatValue(String value, boolean humanize){
        if(value == null){
            value = "";
        }
        return humanize? WordUtils.capitalize(StringUtil.humanize(value)):value;
    }

    public static String formatValue(Object value, boolean humanize){
        if(value == null){
            value = "";
        }
        return humanize? WordUtils.capitalize(StringUtil.humanize(value.toString())):value.toString();
    }
    public static String getValue(CommonPersonObjectClient pc, String field, boolean humanize){
        return formatValue(pc.getDetails().get(field), humanize);
    }

    public static String getValue(CommonPersonObjectClient pc, String field, String defaultV, boolean humanize){
        String val = formatValue(pc.getDetails().get(field), humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, String defaultV, boolean humanize){
        String val = formatValue(cm.get(field), humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, boolean humanize) {
        return formatValue(cm.get(field), humanize);
    }

    public static String getValue(JSONObject cm, String field, boolean humanize, boolean suppressException) {
        try {
            return formatValue(cm.get(field), humanize);
        } catch (JSONException e) {
            e.printStackTrace();
            if(suppressException==false) throw new RuntimeException(e);
        }
        return "";
    }

    public static String nonEmptyValue(Map<String, String> cm, boolean asc, boolean humanize, String... fields){
        List<String> l = Arrays.asList(fields);
        if(!asc){
            Collections.reverse(l);
        }
        for (String f : l) {
            String v = getValue(cm, f, humanize);
            if (v != "") {
                return v;
            }
        }
        return "";
    }

    public static boolean hasAnyEmptyValue(Map<String, String> cm, String postFix, String... fields){
        List<String> l = Arrays.asList(fields);
        for (String f : l) {
            String v = getValue(cm, f, false);
            if (v == "" && (StringUtils.isBlank(postFix) || StringUtils.isBlank(getValue(cm, f+postFix, false)))) {
                return true;
            }
        }
        return false;
    }

    public static String overridesToString(Map<String, String> overrides, SmartRegisterClient client, ByColumnAndByDetails byColumnAndByDetails){
        JSONObject overridejsonobject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                switch (byColumnAndByDetails) {
                    case byDetails:
                        overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getDetails().get(entry.getValue()));
                        break;
                    case byColumn:
                        overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getColumnmaps().get(entry.getValue()));
                        break;
                    default:
                        overridejsonobject.put(entry.getKey(), entry.getValue());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overridejsonobject.toString();
    }

    public static TableRow getDataRow(Context context, String label, String value, TableRow row){
        return getDataRow(context, label, value, row, false);
    }

    public enum Size{
        XSMALL, SMALL, MEDIUM, LARGE, XLARGE
    }

    public static void addRow(Activity activity, TableLayout table, String label, String value, Size size){
        if(size.equals(Size.MEDIUM)){
            View v = activity.getLayoutInflater().inflate(R.layout.tablerow_medium, null);
            ((TextView)v.findViewById(R.id.c1)).setText(label);
            ((TextView)v.findViewById(R.id.c2)).setText(value);

            table.addView(v);
        }
    }

    public static void addRow(Activity activity, TableLayout table, String label, View value, Size size){
        if(size.equals(Size.MEDIUM)){
            TableRow v = (TableRow) activity.getLayoutInflater().inflate(R.layout.tablerow_medium, null);
            ((TextView)v.findViewById(R.id.c1)).setText(label);
            v.removeView(v.findViewById(R.id.c2));
            v.addView(value);

            table.addView(v);
        }
    }

    public static void addRow(Activity activity, TableLayout table, String label, int span, Size size){
        if(size.equals(Size.MEDIUM)){
            TableRow v = (TableRow) activity.getLayoutInflater().inflate(R.layout.tablerow_medium, null);
            v.removeView(v.findViewById(R.id.c2));

            TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            lp.span = span;

            ((TextView)v.findViewById(R.id.c1)).setText(label);
            v.findViewById(R.id.c1).setLayoutParams(lp);

            table.addView(v);
        }
    }

    public static void addRow(Activity activity, TableLayout table, String label1, String value1, String label2, String value2, Size size){
        if(size.equals(Size.MEDIUM)){
            View v = activity.getLayoutInflater().inflate(R.layout.tablerow_medium_double, null);
            ((TextView)v.findViewById(R.id.c1)).setText(label1);
            ((TextView)v.findViewById(R.id.c2)).setText(value1);
            ((TextView)v.findViewById(R.id.c3)).setText(label2);
            ((TextView)v.findViewById(R.id.c4)).setText(value2);

            table.addView(v);
        }
    }

    public static TableRow getDataRow(Context context, String label, String value, TableRow row, boolean compact){
        TableRow tr = row;

        if(row == null){
            tr = new TableRow(context);
            TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            if(compact) {
                tr.setPadding(5, 3, 10, 3);
            }
            else {
                tr.setPadding(10, 5, 10, 5);
            }
        }

        TextView l = new TextView(context);
        l.setText(label + ": ");
        if(compact){
            l.setPadding(5, 2, 5, 1);
        }
        else {
            l.setPadding(20, 2, 20, 2);
        }
        l.setTextColor(Color.BLACK);
        l.setTextSize(14);
        l.setBackgroundColor(Color.WHITE);
        tr.addView(l);

        TextView v = new TextView(context);
        v.setSingleLine(false);
        v.setMaxLines(10);
        v.setText(value);
        if(compact){
            v.setPadding(5, 2, 5, 1);
        }
        else {
            v.setPadding(20, 2, 20, 2);
        }
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        tr.addView(v);

        return tr;
    }

    public static TableRow getEvenWidthDataRow(Context context, String label, String value, TableRow row){
        TableRow tr = row;
        if(row == null){
            tr = new TableRow(context);
            TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            tr.setPadding(10, 5, 10, 5);
        }

        TextView l = new TextView(context);
        l.setText(label + ": ");
        l.setPadding(20, 2, 20, 2);
        l.setTextColor(Color.BLACK);
        l.setTextSize(14);
        l.setBackgroundColor(Color.WHITE);
        tr.addView(l);

        TextView v = new TextView(context);
        v.setSingleLine(false);
        v.setMaxLines(4);
        v.setText(value);
        v.setPadding(20, 2, 20, 2);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        tr.addView(v);

        return tr;
    }

    public static TableRow getDataRow(Context context){
        return getDataRow(context, 0, 0);
    }

    public static TableRow getDataRow(Context context, int marginltr, int marginttb){
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        trlp.setMargins(marginltr, marginttb, marginltr, marginttb);
        tr.setLayoutParams(trlp);
        tr.setPadding(0, 0, 0, 0);
        // tr.setBackgroundColor(Color.BLUE);
        return tr;
    }

    public static int addAsInts(boolean ignoreEmpty, String... vals){
        int i = 0;
        for (String v : vals){
            i += ignoreEmpty&&StringUtils.isBlank(v)?0:Integer.parseInt(v);
        }
        return i;
    }

    public static int addAsInts(boolean ignoreEmpty, Map<String, String> map, String... variables){
        return addAsInts(ignoreEmpty, null, null, map, variables);
    }

    public static int addAsInts(boolean ignoreEmpty, String prefix, String postfix, Map<String, String> map, String... variables){
        prefix = StringUtils.isBlank(prefix)?"":prefix.trim();
        postfix = StringUtils.isBlank(postfix)?"":postfix.trim();
        int i = 0;
        for (String v : variables){
            v = prefix+v+postfix;
            i += ignoreEmpty&&StringUtils.isBlank(map.get(v))?0:Integer.parseInt(map.get(v));
        }
        return i;
    }

    public static TableRow addToRow(Context context, String value, TableRow row){
        return addToRow(context, value, row, false, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, int weight){
        return addToRow(context, value, row, false, weight);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact){
        return addToRow(context, value, row, compact, 1);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact, int weight){
        return addToRow(context, Html.fromHtml(value), row, compact, weight, Size.MEDIUM);
    }

    public static TableRow addToRow(Context context, Spanned value, TableRow row, boolean compact, int weight, Size size){
        TextView v = new TextView(context);
        v.setText(value);

        if (size.equals(Size.MEDIUM)) {
            if (compact) {
                v.setPadding(15, 1, 1, 1);
            } else {
                v.setPadding(5, 5, 2, 5);
            }
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.MATCH_PARENT, weight
            );
            params.setMargins(0, 0, 1, 1);
            v.setLayoutParams(params);
            v.setTextColor(Color.BLACK);
            v.setTextSize(compact?15:16);
            v.setBackgroundColor(Color.WHITE);
        }

        row.addView(v);
        return row;
    }

    public static String getPreference(Context context, String key, String defaultVal){
        return context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getString(key, defaultVal);
    }

    public static Gson getLongDateAwareGson(){
        Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement e, Type t, JsonDeserializationContext jd) throws JsonParseException {
                return new DateTime(e.getAsLong());
            }
        }).create();
        return  g;
    }

    public static boolean writePreference(Context context, String name, String value){
        SharedPreferences pref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(name, value);
        return ed.commit();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void setProfiePic(final Context context, final ImageView mImageView, String bindType, String entityId, final Object watermark){
        final ProfileImage photo = RegisterRepository.findImageByEntityId(bindType, entityId, "dp");
        if(photo != null){
            mImageView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    // Wait until layout to call Picasso
                    @Override
                    public void onGlobalLayout() {
                        // Ensure we call this only once
                        mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        setProfiePicFromPath(context, mImageView, photo.getFilepath(), watermark);
                    }
                });
        }
    }

    public static void setProfiePicFromPath(Context context, ImageView mImageView, String photoPath, Object watermark){
        if(watermark == null){
            Picasso.with(context).load(new File(photoPath))
                    .resize(mImageView.getWidth(), mImageView.getHeight())
                    .into(mImageView);
        }
        else {
            Picasso.with(context).load(new File(photoPath))
                    .resize(mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight())
                    .transform(new WatermarkTransformation(watermark))
                    .into(mImageView);
        }
    }

    public static void setProfiePic(Context context, ImageView mImageView, int photoResId, Object watermark){
        if(watermark == null){
            Picasso.with(context).load(photoResId)
                    .resize(mImageView.getWidth(), mImageView.getHeight())
                    .into(mImageView);
        }
        else {
            Picasso.with(context).load(photoResId)
                    .resize(mImageView.getMeasuredWidth(), mImageView.getMeasuredHeight())
                    .transform(new WatermarkTransformation(watermark))
                    .into(mImageView);
        }
    }

    public static void resetLocale(Context context, boolean suppressException){
        try{
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(context));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = org.ei.opensrp.Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        }catch(Exception e){
            e.printStackTrace();
            if(!suppressException){
                throw new RuntimeException(e);
            }
        }
    }

    public static String getPreviouslySavedDataForForm(Context context, String formName, String overridesStr, String id){
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String savedDataKey = formName + "savedPartialData";
            String overridesKey = formName + "overrides";
            String idKey = formName + "id";

            JSONObject overrides = new JSONObject();

            if (overrides != null){
                JSONObject json = new JSONObject(overridesStr);
                String s = json.getString("fieldOverrides");
                overrides = new JSONObject(s);
            }

            boolean idIsConsistent = id == null && !sharedPref.contains(idKey) ||
                    id != null && sharedPref.contains(idKey) && sharedPref.getString(idKey, null).equals(id);

            if (sharedPref.contains(savedDataKey) && sharedPref.contains(overridesKey) && idIsConsistent){
                String savedDataStr = sharedPref.getString(savedDataKey, null);
                String savedOverridesStr = sharedPref.getString(overridesKey, null);


                // the previously saved data is only returned if the overrides and id are the same ones used previously
                if (savedOverridesStr.equals(overrides.toString())) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    //after retrieving the value delete it from shared pref.
                    editor.remove(savedDataKey);
                    editor.remove(overridesKey);
                    editor.remove(idKey);
                    editor.apply();
                    return savedDataStr;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
