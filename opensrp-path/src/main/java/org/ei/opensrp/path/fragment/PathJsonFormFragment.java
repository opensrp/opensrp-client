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
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.holder.ChildTreeItemHolder;
import org.ei.opensrp.path.holder.MotherTreeItemHolder;
import org.ei.opensrp.path.interactors.PathJsonFormInteractor;
import org.ei.opensrp.path.provider.ChildSmartClientsProvider;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;

import java.util.ArrayList;
import java.util.HashMap;
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


    //Mother Lookup
    public Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener() {
        return motherLookUpListener;
    }

    public void showMotherLookUp(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        if (snackbar != null) {
            snackbar.dismiss();
        }

        LinearLayout mainView = getMainView();
        View focusedView = mainView.findFocus();

        if (!map.isEmpty()) {
            snackbar = Snackbar
                    .make(focusedView, map.size() + " mother/guardian match.", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Tap to view", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //updateResults(map);
                    updateResults2(map);
                }
            });
            show(snackbar);

        }
    }

    private void updateResults(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mother_lookup_results, null);

        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setTitle(getString(R.string.mother_lookup_results));
        builder.setCancelable(true);

        alertDialog = builder.create();

        final List<CommonPersonObject> headers = new ArrayList<>();
        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            headers.add(entry.getKey());
        }


        final MotherLookUpSmartClientsProvider motherLookUpSmartClientsProvider = new MotherLookUpSmartClientsProvider(getActivity(), lookUpRecordOnClickLister);
        final ChildSmartClientsProvider childSmartClientsProvider = new ChildSmartClientsProvider(getActivity(), lookUpRecordOnClickLister, context().alertService(), VaccinatorApplication.getInstance().vaccineRepository(), VaccinatorApplication.getInstance().weightRepository());

        BaseExpandableListAdapter expandableListAdapter = new BaseExpandableListAdapter() {
            @Override
            public Object getChild(int groupPosition, int childPosititon) {
                return map.get(headers.get(groupPosition))
                        .get(childPosititon);
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public View getChildView(int groupPosition, final int childPosition,
                                     boolean isLastChild, View convertView, ViewGroup parent) {


                View v;
                if (convertView == null) {
                    v = childSmartClientsProvider.inflatelayoutForCursorAdapter();
                    v.setPadding(20, 0, 0, 0);
                } else {
                    v = convertView;
                }

                CommonPersonObject childPersonObject = (CommonPersonObject) getChild(groupPosition, childPosition);

                CommonPersonObjectClient client = new CommonPersonObjectClient(childPersonObject.getCaseId(), childPersonObject.getColumnmaps(), childPersonObject.getColumnmaps().get("first_name"));
                client.setColumnmaps(childPersonObject.getColumnmaps());

                childSmartClientsProvider.getView(client, v);

                return v;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return map.get(headers.get(groupPosition))
                        .size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return headers.get(groupPosition);
            }

            @Override
            public int getGroupCount() {
                return headers.size();
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                                     View convertView, ViewGroup parent) {

                View v;
                if (convertView == null) {
                    v = motherLookUpSmartClientsProvider.inflatelayoutForCursorAdapter();
                } else {
                    v = convertView;
                }

                CommonPersonObject parentPersonObject = (CommonPersonObject) getGroup(groupPosition);

                CommonPersonObjectClient client = new CommonPersonObjectClient(parentPersonObject.getCaseId(), parentPersonObject.getDetails(), parentPersonObject.getDetails().get("FWHOHFNAME"));
                client.setColumnmaps(parentPersonObject.getColumnmaps());

                motherLookUpSmartClientsProvider.getView(client, v);

                ExpandableListView mExpandableListView = (ExpandableListView) parent;
                mExpandableListView.expandGroup(groupPosition);

                return v;

            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

        };

        expandableListView.setAdapter(expandableListAdapter);

        alertDialog.show();

    }

    private void updateResults2(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {


        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.mother_lookup_default, null, false);
        final ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.container);
        rootView.findViewById(R.id.status_bar).setVisibility(View.GONE);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(rootView).setNegativeButton(R.string.dismiss, null);
        builder.setTitle(getString(R.string.mother_lookup_results));
        builder.setCancelable(true);

        alertDialog = builder.create();


        final TreeNode rootNode = TreeNode.root();

        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            CommonPersonObject motherObject = entry.getKey();
            List<CommonPersonObject> childList = entry.getValue();

            TreeNode motherNode = new TreeNode(new MotherTreeItemHolder.MotherTreeItem(motherObject)).setViewHolder(new MotherTreeItemHolder(getActivity()));
            motherNode.setExpanded(true);
            addChildren(motherNode, childList);
            rootNode.addChild(motherNode);

        }

        AndroidTreeView androidTreeView = new AndroidTreeView(getActivity(), rootNode);
        androidTreeView.setDefaultContainerStyle(com.vijay.jsonwizard.R.style.TreeNodeStyle);
        containerView.addView(androidTreeView.getView());

        alertDialog.show();
    }

    private void addChildren(TreeNode motherNode, List<CommonPersonObject> childList) {

        for (CommonPersonObject child : childList) {
            TreeNode childNode = new TreeNode(new ChildTreeItemHolder.ChildTreeItem(child)).setViewHolder(new ChildTreeItemHolder(getActivity()));
            motherNode.addChild(childNode);
        }

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

    final Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> motherLookUpListener = new Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>>() {
        @Override
        public void onEvent(HashMap<CommonPersonObject, List<CommonPersonObject>> data) {
            showMotherLookUp(data);
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


