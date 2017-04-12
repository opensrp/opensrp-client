package org.ei.opensrp.path.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.interactors.PathJsonFormInteractor;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;

import java.util.List;
import java.util.Map;

import static util.Utils.getValue;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathJsonFormFragment extends JsonFormFragment {

    Snackbar snackbar = null;
    AlertDialog alertDialog = null;

    public static PathJsonFormFragment getFormFragment(String stepName) {
        PathJsonFormFragment jsonFormFragment = new PathJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, PathJsonFormInteractor.getInstance());
    }

    public Context context() {
        return Context.getInstance().updateApplicationContext(this.getActivity().getApplicationContext());
    }

    public Listener<Map<String, List<CommonPersonObject>>> listener() {
        return lookUpListener;
    }

    public void showLookUp(String entityId, final List<CommonPersonObject> list) {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        LinearLayout mainView = getMainView();
        View focusedView = mainView.findFocus();

        if (!list.isEmpty()) {
            if (entityId.equalsIgnoreCase("mother")) {
                snackbar = Snackbar
                        .make(focusedView, list.size() + " mother/guardian match.", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Tap to view", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateResults(list);
                    }
                });
                show(snackbar);

            }
        }
    }

    private void updateResults(final List<CommonPersonObject> results) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mother_lookup_results, null);

        ListView listView = (ListView) view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setTitle(getString(R.string.mother_lookup_results));
        builder.setCancelable(true);

        alertDialog = builder.create();


        final MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider = new MotherLookUpSmartClientsProvider(getActivity(), lookUpRecordOnClickLister);
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return results.size();
            }

            @Override
            public Object getItem(int position) {
                return results.get(position);
            }

            @Override
            public long getItemId(int position) {
                return Long.valueOf(results.get(position).getCaseId().replaceAll("\\D+", ""));
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = motherLookUpSmartClientsProvider.inflatelayoutForCursorAdapter();
                } else {
                    v = convertView;
                }

                CommonPersonObject commonPersonObject = results.get(position);

                CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), commonPersonObject.getDetails().get("FWHOHFNAME"));
                client.setColumnmaps(commonPersonObject.getColumnmaps());

                motherLookUpSmartClientsProvider.getView(client, v);

                v.setOnClickListener(lookUpRecordOnClickLister);
                v.setTag(client);

                return v;
            }
        };

        listView.setAdapter(baseAdapter);

        alertDialog.show();

    }

    private void show(Snackbar snackbar) {

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());

        final AppCompatTextView actionView = (AppCompatTextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(textSize);
        actionView.setGravity(Gravity.CENTER);


        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

    }

    final Listener<Map<String, List<CommonPersonObject>>> lookUpListener = new Listener<Map<String, List<CommonPersonObject>>>() {
        @Override
        public void onEvent(Map<String, List<CommonPersonObject>> data) {

            for (Map.Entry<String, List<CommonPersonObject>> entry : data.entrySet()) {
                String entityId = entry.getKey();
                List<CommonPersonObject> list = entry.getValue();
                showLookUp(entityId, list);
            }
        }
    };

    final View.OnClickListener lookUpRecordOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }

            if (client != null) {
                Toast.makeText(getActivity(), client.getCaseId(), Toast.LENGTH_LONG).show();
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        }
    };


}


