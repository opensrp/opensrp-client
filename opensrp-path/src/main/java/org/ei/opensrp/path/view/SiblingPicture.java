package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseActivity;
import org.ei.opensrp.path.activity.ChildDetailTabbedActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;
import org.ei.opensrp.path.toolbar.BaseToolbar;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.json.JSONException;
import org.opensrp.api.constants.Gender;

import java.util.HashMap;
import java.util.Map;

import util.ImageUtils;
import util.JsonFormUtils;
import util.Utils;

/**
 * Created by Jason Rogena - jrogena@ona.io on 09/05/2017.
 */

public class SiblingPicture extends RecyclerView.ViewHolder {
    private final View itemView;
    private final Context context;
    private ImageView profilePhoto;
    private TextView initials;

    public SiblingPicture(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.context = itemView.getContext();
        init();
    }

    private void init() {
        profilePhoto = (ImageView) itemView.findViewById(R.id.profile_photo);
        initials = (TextView) itemView.findViewById(R.id.initials);
    }

    public void setChildBaseEntityId(BaseActivity baseActivity, String baseEntityId) {
        Utils.startAsyncTask(new GetChildDetailsTask(baseActivity, baseEntityId), null);
    }

    private class GetChildDetailsTask extends AsyncTask<Void, Void, CommonPersonObjectClient> {
        private final String baseEntityId;
        private final BaseActivity baseActivity;
        private DetailsRepository detailsRepository;

        public GetChildDetailsTask(BaseActivity baseActivity, String baseEntityId) {
            this.baseActivity = baseActivity;
            this.baseEntityId = baseEntityId;
            detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        }

        @Override
        protected CommonPersonObjectClient doInBackground(Void... params) {
            CommonPersonObject rawDetails = baseActivity.getOpenSRPContext()
                    .commonrepository("ec_child").findByBaseEntityId(baseEntityId);
            if (rawDetails != null) {
                // Get extra child details
                CommonPersonObjectClient childDetails = MotherLookUpSmartClientsProvider.convert(rawDetails);
                childDetails.getColumnmaps().putAll(detailsRepository.getAllDetailsForClient(baseEntityId));

                // Check if child has a profile pic
                ProfileImage profileImage = baseActivity.getOpenSRPContext()
                        .imageRepository().findByEntityId(baseEntityId);

                childDetails.getColumnmaps().put("has_profile_image", "true");
                if (profileImage == null) {
                    childDetails.getColumnmaps().put("has_profile_image", "false");
                }

                // Get mother details
                String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(),
                        "relational_id", false);

                Map<String, String> motherDetails = new HashMap<>();
                motherDetails.put("mother_first_name", "");
                motherDetails.put("mother_last_name", "");
                motherDetails.put("mother_dob", "");
                motherDetails.put("mother_nrc_number", "");
                if (!TextUtils.isEmpty(motherBaseEntityId)) {
                    CommonPersonObject rawMotherDetails = baseActivity.getOpenSRPContext()
                            .commonrepository("ec_mother").findByBaseEntityId(motherBaseEntityId);
                    if (rawMotherDetails != null) {
                        motherDetails.put("mother_first_name",
                                Utils.getValue(rawMotherDetails.getColumnmaps(), "first_name", false));
                        motherDetails.put("mother_last_name",
                                Utils.getValue(rawMotherDetails.getColumnmaps(), "last_name", false));
                        motherDetails.put("mother_dob",
                                Utils.getValue(rawMotherDetails.getColumnmaps(), "dob", false));
                        motherDetails.put("mother_nrc_number",
                                Utils.getValue(rawMotherDetails.getColumnmaps(), "nrc_number", false));
                    }
                }
                childDetails.getColumnmaps().putAll(motherDetails);
                childDetails.setDetails(childDetails.getColumnmaps());

                return childDetails;
            }

            return null;
        }

        @Override
        protected void onPostExecute(CommonPersonObjectClient childDetails) {
            super.onPostExecute(childDetails);
            if (childDetails != null) {
                updatePicture(baseActivity, baseEntityId, childDetails);
            }
        }
    }

    private void updatePicture(final BaseActivity baseActivity, String baseEntityId,
                               final CommonPersonObjectClient childDetails) {
        Gender gender = Gender.UNKNOWN;
        int genderColor = R.color.gender_neutral_green;
        int genderLightColor = R.color.gender_neutral_light_green;
        String genderString = Utils.getValue(childDetails.getColumnmaps(), "gender", false);
        if (genderString != null && genderString.toLowerCase().equals("female")) {
            gender = Gender.FEMALE;
            genderColor = R.color.female_pink;
            genderLightColor = R.color.female_light_pink;
        } else if (genderString != null && genderString.toLowerCase().equals("male")) {
            gender = Gender.MALE;
            genderColor = R.color.male_blue;
            genderLightColor = R.color.male_light_blue;
        }

        if (Utils.getValue(childDetails.getColumnmaps(), "has_profile_image", false).equals("true")) {
            profilePhoto.setVisibility(View.VISIBLE);
            initials.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            initials.setTextColor(context.getResources().getColor(android.R.color.black));
            profilePhoto.setTag(org.ei.opensrp.R.id.entity_id, baseEntityId);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId,
                    OpenSRPImageLoader.getStaticImageListener(profilePhoto,
                            ImageUtils.profileImageResourceByGender(gender),
                            ImageUtils.profileImageResourceByGender(gender)));
        } else {
            profilePhoto.setVisibility(View.GONE);
            initials.setBackgroundColor(context.getResources().getColor(genderLightColor));
            initials.setTextColor(context.getResources().getColor(genderColor));
        }

        final String firstName = Utils.getValue(childDetails.getColumnmaps(), "first_name", true);
        final String lastName = Utils.getValue(childDetails.getColumnmaps(), "last_name", true);

        if (Utils.getValue(childDetails.getColumnmaps(), "has_profile_image", false).equals("false")) {
            initials.setVisibility(View.VISIBLE);
            String initialsString = "";
            if (!TextUtils.isEmpty(firstName)) {
                initialsString = firstName.substring(0, 1);
            }

            if (!TextUtils.isEmpty(lastName)) {
                initialsString = initialsString + lastName.substring(0, 1);
            }

            initials.setText(initialsString.toUpperCase());
        } else {
            initials.setVisibility(View.GONE);
        }

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, firstName + " " + lastName, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(baseActivity, ChildDetailTabbedActivity.class);
                Bundle bundle = new Bundle();
                try {
                    BaseToolbar baseToolbar = baseActivity.getBaseToolbar();
                    if (baseToolbar instanceof LocationSwitcherToolbar) {
                        bundle.putString("location_name",
                                JsonFormUtils.getOpenMrsLocationId(baseActivity.getOpenSRPContext(),
                                        ((LocationSwitcherToolbar) baseToolbar).getCurrentLocation()));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bundle.putSerializable(ChildDetailTabbedActivity.EXTRA_CHILD_DETAILS, childDetails);
                bundle.putSerializable(ChildDetailTabbedActivity.EXTRA_REGISTER_CLICKABLES, null);
                intent.putExtras(bundle);

                baseActivity.startActivity(intent);
            }
        });
    }
}
