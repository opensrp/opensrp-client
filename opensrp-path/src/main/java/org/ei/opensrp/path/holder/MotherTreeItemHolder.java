package org.ei.opensrp.path.holder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.joda.time.DateTime;

import java.util.Date;

import util.DateUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class MotherTreeItemHolder extends TreeNode.BaseNodeViewHolder<MotherTreeItemHolder.MotherTreeItem> {

    private PrintView arrowView;

    public MotherTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, MotherTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.mother_lookup_client, null, false);

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);

        if (!value.hasChildren) {
            arrowView.setVisibility(View.INVISIBLE);
        }

        CommonPersonObject client = value.commonPersonObject;

        final CommonPersonObjectClient pc = new CommonPersonObjectClient(client.getCaseId(), client.getColumnmaps(), client.getColumnmaps().get("first_name"));
        pc.setColumnmaps(client.getColumnmaps());

        String zeirId = getValue(pc.getColumnmaps(), "zeir_id", false);

        if (StringUtils.isNotBlank(zeirId) && zeirId.contains("_")) {
            zeirId = zeirId.split("_")[0];
        }

        fillValue((TextView) view.findViewById(R.id.child_zeir_id), zeirId);

        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
        final String childName = getName(firstName, lastName);

        fillValue((TextView) view.findViewById(R.id.child_name), childName);

        DateTime birthDateTime = new DateTime((new Date()).getTime());
        String dobString = getValue(pc.getColumnmaps(), "dob", false);
        String durationString = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                birthDateTime = new DateTime(dobString);
                String duration = DateUtils.getDuration(birthDateTime);
                if (duration != null) {
                    durationString = duration;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue((TextView) view.findViewById(R.id.child_age), durationString);

        fillValue((TextView) view.findViewById(R.id.child_card_number), pc.getColumnmaps(), "nrc_number", false);


        view.findViewById(R.id.select).setTag(client);
        view.findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (node.getClickListener() != null) {
                    node.getClickListener().onClick(node, pc);
                }
            }
        });

        AbsListView.LayoutParams clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        view.setLayoutParams(clientViewLayoutParams);
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class MotherTreeItem {
        public CommonPersonObject commonPersonObject;
        public boolean hasChildren;

        public MotherTreeItem(CommonPersonObject commonPersonObject, boolean hasChildren) {
            this.commonPersonObject = commonPersonObject;
            this.hasChildren = hasChildren;
        }
    }
}
