package org.ei.opensrp.path.holder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
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


        CommonPersonObject client = value.commonPersonObject;

        CommonPersonObjectClient pc = new CommonPersonObjectClient(client.getCaseId(), client.getColumnmaps(), client.getColumnmaps().get("first_name"));
        pc.setColumnmaps(client.getColumnmaps());


        fillValue((TextView) view.findViewById(R.id.child_zeir_id), getValue(pc.getColumnmaps(), "zeir_id", false));

        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
        String childName = getName(firstName, lastName);

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

        String gender = "female";
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);

        ImageView profilePic = (ImageView) view.findViewById(R.id.child_profilepic);
        profilePic.setImageResource(defaultImageResId);
        if (pc.entityId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(org.ei.opensrp.R.id.entity_id, pc.getCaseId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

        view.findViewById(R.id.child_profile_info_layout).setTag(client);
        // view.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);


        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class MotherTreeItem {
        public CommonPersonObject commonPersonObject;

        public MotherTreeItem(CommonPersonObject commonPersonObject) {
            this.commonPersonObject = commonPersonObject;
        }
    }
}
