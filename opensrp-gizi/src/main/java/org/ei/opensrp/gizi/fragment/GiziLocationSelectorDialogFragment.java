package org.ei.opensrp.gizi.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.base.Strings;

import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;

import java.util.Map;

import atv.holder.SelectableItemHolder;
import atv.model.TreeNode;
import atv.view.AndroidTreeView;

import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by koros on 2/25/16.
 */
public class GiziLocationSelectorDialogFragment extends DialogFragment {

    private final SecuredNativeSmartRegisterActivity parentActivity;
    private final DialogOption[] options;
    private final DialogOptionModel dialogOptionModel;
    private final String locationJSONString;
    public static String savestate ;
    AndroidTreeView tView;
    public String formname;
    // private final Object tag;

    private GiziLocationSelectorDialogFragment(SecuredNativeSmartRegisterActivity activity,
                                               DialogOptionModel dialogOptionModel,
                                               String locationJSONString
            , String formname) {
        this.formname = formname;
        this.parentActivity = activity;
        this.options = dialogOptionModel.getDialogOptions();
        this.dialogOptionModel = dialogOptionModel;
        this.locationJSONString = locationJSONString;
        // this.tag = tag;
    }

    public static GiziLocationSelectorDialogFragment newInstance(
            SecuredNativeSmartRegisterActivity activity,
            DialogOptionModel dialogOptionModel,
            String locationJSONString,String formname) {
        return new GiziLocationSelectorDialogFragment(activity, dialogOptionModel, locationJSONString,formname);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = new LinearLayout(getActivity());
        TreeNode root = TreeNode.root();

        LocationTree locationTree = EntityUtils.fromJson(locationJSONString, LocationTree.class);

        Map<String,org.opensrp.api.util.TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();

        // creating the tree
        locationTreeToTreNode(root, locationMap);

        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultContainerStyle(org.ei.opensrp.R.style.TreeNodeStyle);
        tView.setSelectionModeEnabled(false);

        if(savestate != null){
            tView.restoreState(savestate);
        }

        // tView.getSelected().get(1).
        dialogView.addView(tView.getView());
        return dialogView;
    }

    public TreeNode createNode(String locationlevel, String locationname){
        TreeNode node = new TreeNode(locationname,locationlevel).setViewHolder(new SelectableItemHolder(getActivity(),locationlevel+": "));
        node.setSelectable(false);
        addselectlistener(node, "");
        return node;
    }

    public void addChildToParentNode(TreeNode parent,TreeNode [] nodes){
        for (int i = 0;i<nodes.length;i++){
            parent.addChild(nodes[i]);
        }
    }

    //    public void addselectlistener (TreeNode node,final String id){
//        node.setClickListener(new TreeNode.TreeNodeClickListener() {
//            @Override
//            public void onClick(TreeNode node, Object value) {
//                if(node.isLeaf()){
//                    node.getParent().getName();
//                    JSONObject locationjson = new JSONObject();
//                    TreeNode traversingnode = node;
//                   try {
//                       locationjson.put("location_name", traversingnode.getName().replace(" ","_"));
//                       locationjson.put("existing_location", id);
//                   }catch (Exception e){
//
//                   }
//                    FieldOverrides fieldOverrides = new FieldOverrides(locationjson.toString());
//                    //  Toast.makeText(getActivity(),fieldOverrides.getJSONString(),Toast.LENGTH_LONG).show();
//                    parentActivity.startFormActivity(formname, null, fieldOverrides.getJSONString());
//                    savestate = tView.getSaveState();
//                    dismiss();
//                }
//            }
//        });
//    }
    public void addselectlistener (TreeNode node,final String id){
        node.setClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                if(node.isLeaf()){
                    JSONObject locationjson = new JSONObject();
                    try {
                        locationjson.put("location_name", node.getName().replace(" ","_"));
                        locationjson.put("existing_location", id);
                    }catch (Exception e){

                    }
                    TreeNode traversingnode = node;
                    while(!traversingnode.isRoot()){
                        try {
                            locationjson.put("existing_"+traversingnode.getlocationlevel(), traversingnode.getName());
                        }catch(Exception e){

                        }
                        traversingnode = traversingnode.getParent();
                    }
                    FieldOverrides fieldOverrides = new FieldOverrides(locationjson.toString());
                    parentActivity.startFormActivity(formname, null, fieldOverrides.getJSONString());
                    savestate = tView.getSaveState();
                    dismiss();
                }
            }
        });
    }

    public void locationTreeToTreNode(TreeNode node, Map<String,org.opensrp.api.util.TreeNode<String, Location>> location) {

        for(Map.Entry<String, org.opensrp.api.util.TreeNode<String, Location>> entry : location.entrySet()) {
            String locationTag = entry.getValue().getNode().getTags().iterator().next();
            TreeNode tree = createNode(
                    Strings.isNullOrEmpty(locationTag)?"-":humanize(locationTag),
                    humanize(entry.getValue().getLabel()));
            node.addChild(tree);
            addselectlistener(tree, entry.getValue().getId());
            if(entry.getValue().getChildren() != null) {
                locationTreeToTreNode(tree, entry.getValue().getChildren());
            }
        }
    }

}
