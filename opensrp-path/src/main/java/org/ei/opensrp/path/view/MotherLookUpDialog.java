package org.ei.opensrp.path.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.holder.ChildTreeItemHolder;
import org.ei.opensrp.path.holder.MotherTreeItemHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotherLookUpDialog extends Dialog implements TreeNode.TreeNodeClickListener {

    private final Context context;
    private TreeNode rootNode;
    private CommonPersonObjectClient selectClient;

    public MotherLookUpDialog(Context context, HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        super(context);
        this.context = context;
        init(map);
    }

    private void init(final HashMap<CommonPersonObject, List<CommonPersonObject>> map) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.lookup_dialog_tree_view);
        LinearLayout canvas = (LinearLayout) this.findViewById(R.id.canvas);


        rootNode = TreeNode.root();

        for (Map.Entry<CommonPersonObject, List<CommonPersonObject>> entry : map.entrySet()) {
            CommonPersonObject motherObject = entry.getKey();
            List<CommonPersonObject> childList = entry.getValue();
            boolean hasChildren = childList != null && !childList.isEmpty();

            TreeNode motherNode = new TreeNode(new MotherTreeItemHolder.MotherTreeItem(motherObject, hasChildren)).setViewHolder(new MotherTreeItemHolder(context));
            motherNode.setExpanded(true);
            motherNode.setClickListener(this);
            addChildren(motherNode, childList);
            rootNode.addChild(motherNode);

        }


        AndroidTreeView androidTreeView = new AndroidTreeView(context, rootNode);
        androidTreeView.setDefaultContainerStyle(R.style.TreeNodeStyle);

        canvas.addView(androidTreeView.getView());
    }

    private void addChildren(TreeNode motherNode, List<CommonPersonObject> childList) {

        for (CommonPersonObject child : childList) {
            TreeNode childNode = new TreeNode(new ChildTreeItemHolder.ChildTreeItem(child)).setViewHolder(new ChildTreeItemHolder(context));
            motherNode.addChild(childNode);
        }

    }

    @Override
    public void onClick(TreeNode node, Object value) {
        if (value instanceof CommonPersonObjectClient) {
            selectClient = (CommonPersonObjectClient) value;
            dismiss();
        }
    }

    public CommonPersonObjectClient getSelectClient() {
        return selectClient;
    }
}
