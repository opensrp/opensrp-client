package org.ei.opensrp.path.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.interactors.PathJsonFormInteractor;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;
import org.ei.opensrp.path.viewstates.PathJsonFormFragmentViewState;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.MotherLookUpUtils;

import static util.Utils.getValue;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathJsonFormFragment extends JsonFormFragment {

    static String stepName;

    Snackbar snackbar = null;
    AlertDialog alertDialog = null;
    boolean lookedUp = false;

    public static PathJsonFormFragment getFormFragment(String stepName) {
        PathJsonFormFragment jsonFormFragment = new PathJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        PathJsonFormFragment.stepName = stepName;
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    protected PathJsonFormFragmentViewState createViewState() {
        return new PathJsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, PathJsonFormInteractor.getInstance());
    }

    public Context context() {
        return Context.getInstance().updateApplicationContext(this.getActivity().getApplicationContext());
    }


    //Mother Lookup
    public Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener() {
        return motherLookUpListener;
    }

    public void showMotherLookUp(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        if (!map.isEmpty()) {
            tapToView(map);
        } else {
            if(snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void updateResults(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mother_lookup_results, null);

        ListView listView = (ListView) view.findViewById(R.id.list_view);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);

        alertDialog = builder.create();

        final List<CommonPersonObject> mothers = new ArrayList<>();
        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            mothers.add(entry.getKey());
        }

        final MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider = new MotherLookUpSmartClientsProvider(getActivity(), lookUpRecordOnClickLister);
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mothers.size();
            }

            @Override
            public Object getItem(int position) {
                return mothers.get(position);
            }

            @Override
            public long getItemId(int position) {
                return Long.valueOf(mothers.get(position).getCaseId().replaceAll("\\D+", ""));
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = motherLookUpSmartClientsProvider.inflatelayoutForCursorAdapter();
                } else {
                    v = convertView;
                }

                CommonPersonObject commonPersonObject = mothers.get(position);
                List<CommonPersonObject> children = map.get(commonPersonObject);

                motherLookUpSmartClientsProvider.getView(commonPersonObject, children, v);

                v.setOnClickListener(lookUpRecordOnClickLister);
                v.setTag(MotherLookUpSmartClientsProvider.convert(commonPersonObject));

                return v;
            }
        };

        listView.setAdapter(baseAdapter);
        alertDialog.show();

    }

    public void clearMotherLookUp() {
        String entityId = "mother";
        Map<String, List<View>> lookupMap = getLookUpMap();
        if (lookupMap.containsKey(entityId)) {
            List<View> lookUpViews = lookupMap.get(entityId);
            if (lookUpViews != null && !lookUpViews.isEmpty()) {

                for (View view : lookUpViews) {

                    if (view instanceof MaterialEditText) {
                        MaterialEditText materialEditText = (MaterialEditText) view;
                        enableEditText(materialEditText);
                        materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
                        materialEditText.setText("");


                    }
                }

                Map<String, String> metadataMap = new HashMap<>();
                metadataMap.put("entity_id", "");
                metadataMap.put("value", "");

                writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                lookedUp = false;
            }
        }
    }

    private void tapToView(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        snackbar = Snackbar
                .make(getMainView(), map.size() + " mother/guardian match(es).", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Tap to view", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResults(map);
                //updateResultTree(map);
            }
        });
        show(snackbar, 30000);

    }

    private void clearView() {
        snackbar = Snackbar
                .make(getMainView(), "Undo Lookup.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                clearMotherLookUp();
            }
        });
        show(snackbar, 30000);
    }

    private void show(final Snackbar snackbar, int duration) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.snackbar_background_yellow);

        final AppCompatTextView actionView = (AppCompatTextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.text_black));

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.text_black));

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);

    }

    private void disableEditText(MaterialEditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
    }

    private void enableEditText(MaterialEditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    private void lookupDialogDismissed(CommonPersonObjectClient pc) {
        if (pc != null) {

            String entityId = "mother";

            Map<String, List<View>> lookupMap = getLookUpMap();
            if (lookupMap.containsKey(entityId)) {
                List<View> lookUpViews = lookupMap.get(entityId);
                if (lookUpViews != null && !lookUpViews.isEmpty()) {

                    for (View view : lookUpViews) {

                        String key = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
                        String text = "";

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.firstName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.firstName, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.lastName)) {
                            text = getValue(pc.getColumnmaps(), MotherLookUpUtils.lastName, true);
                        }

                        if (StringUtils.containsIgnoreCase(key, MotherLookUpUtils.birthDate)) {
                            String dobString = getValue(pc.getColumnmaps(), MotherLookUpUtils.dob, false);
                            if (StringUtils.isNotBlank(dobString)) {
                                try {
                                    DateTime birthDateTime = new DateTime(dobString);
                                    text = DatePickerFactory.DATE_FORMAT.format(birthDateTime.toDate());
                                } catch (Exception e) {
                                    Log.e(getClass().getName(), e.toString(), e);
                                }
                            }
                        }

                        if (view instanceof MaterialEditText) {
                            MaterialEditText materialEditText = (MaterialEditText) view;
                            materialEditText.setTag(com.vijay.jsonwizard.R.id.after_look_up, true);
                            materialEditText.setText(text);
                            materialEditText.setInputType(InputType.TYPE_NULL);
                            disableEditText(materialEditText);

                        }
                    }

                    Map<String, String> metadataMap = new HashMap<>();
                    metadataMap.put("entity_id", entityId);
                    metadataMap.put("value", getValue(pc.getColumnmaps(), MotherLookUpUtils.baseEntityId, false));

                    writeMetaDataValue(FormUtils.LOOK_UP_JAVAROSA_PROPERTY, metadataMap);

                    lookedUp = true;
                    clearView();
                }
            }
        }
    }

    final Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener = new Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>>() {
        @Override
        public void onEvent(HashMap<CommonPersonObject, List<CommonPersonObject>> data) {
            if (!lookedUp) {
                showMotherLookUp(data);
            }
        }
    };

    final View.OnClickListener lookUpRecordOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                CommonPersonObjectClient client = null;
                if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                    client = (CommonPersonObjectClient) view.getTag();
                }

                if (client != null) {
                    lookupDialogDismissed(client);
                }
            }
        }
    };

    public void getLabelViewFromTag(String labeltext,String todisplay){
//        super.getMainView();
        updateRelevantTextView(getMainView(),todisplay,labeltext);

//                findViewWithTag("labelHeaderImage")).setText("is it possible");
    }
    public void updateRelevantTextView(LinearLayout mMainView, String textstring, String currentKey) {
        if(mMainView != null) {
            int childCount = mMainView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = mMainView.getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String key = (String) textView.getTag(com.vijay.jsonwizard.R.id.key);
                    if (key.equals(currentKey)) {
                        textView.setText(textstring);
                    }
                }
//            else if(view instanceof  ViewGroup){
//                updateRelevantTextView((ViewGroup) view,textstring,currentKey);
//            }
            }
        }
    }
    public String getRelevantTextViewString( String currentKey) {
        String toreturn = "";
        if(getMainView() != null) {
            int childCount = getMainView().getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = getMainView().getChildAt(i);
                if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    String key = (String) textView.getTag(com.vijay.jsonwizard.R.id.key);
                    if (key.equals(currentKey)) {
                        toreturn = textView.getText().toString();
                    }
                }
//            else if(view instanceof  ViewGroup){
//                updateRelevantTextView((ViewGroup) view,textstring,currentKey);
//            }
            }
        }
        return toreturn;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean balancecheck = true;

        if (item.getItemId() == com.vijay.jsonwizard.R.id.action_save) {
            JSONObject object = getStep("step1");
            try {
                if (object.getString("title").contains("Stock Issued") || object.getString("title").contains("Stock Received") || object.getString("title").contains("Stock Loss/Adjustment")) {
                    balancecheck = ((PathJsonFormActivity)getActivity()).checkIfBalanceNegative();
                }
            }catch (Exception e){

            }
        }
        if(balancecheck) {
            return super.onOptionsItemSelected(item);
        }else{
            final Snackbar snackbar = Snackbar
                    .make(getMainView(), "Please make sure the balance is not less than zero.", Snackbar.LENGTH_LONG);
            snackbar.setAction("Close", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });

// Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
            return true;
        }
    }


}


