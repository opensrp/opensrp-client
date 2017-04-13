package org.ei.opensrp.path.holder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.util.Date;

import util.DateUtils;
import util.ImageUtils;

import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class ChildTreeItemHolder extends TreeNode.BaseNodeViewHolder<ChildTreeItemHolder.ChildTreeItem> {

    public ChildTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, ChildTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.child_lookup_client, null, false);

        CommonPersonObject client = (CommonPersonObject) value.commonPersonObject;

        CommonPersonObjectClient pc = new CommonPersonObjectClient(client.getCaseId(), client.getColumnmaps(), client.getColumnmaps().get("first_name"));
        pc.setColumnmaps(client.getColumnmaps());

        fillValue((TextView) view.findViewById(R.id.child_zeir_id), getValue(pc.getColumnmaps(), "zeir_id", false));

        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
        String childName = getName(firstName, lastName);

        String motherFirstName = getValue(pc.getColumnmaps(), "mother_first_name", true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
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

        fillValue((TextView) view.findViewById(R.id.child_card_number), pc.getColumnmaps(), "epi_card_number", false);

        String gender = getValue(pc.getColumnmaps(), "gender", true);
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);

        ImageView profilePic = (ImageView) view.findViewById(R.id.child_profilepic);
        profilePic.setImageResource(defaultImageResId);
        if (pc.entityId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(org.ei.opensrp.R.id.entity_id, pc.getCaseId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

        view.findViewById(R.id.child_profile_info_layout).setTag(client);

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }

    public static class ChildTreeItem {
        public CommonPersonObject commonPersonObject;

        public ChildTreeItem(CommonPersonObject commonPersonObject) {
            this.commonPersonObject = commonPersonObject;
        }
    }
}
