package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseActivity;
import org.ei.opensrp.path.activity.ChildDetailTabbedActivity;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;
import org.ei.opensrp.path.toolbar.BaseToolbar;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.DetailsRepository;
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

public class SiblingPicture extends LinearLayout {
    private Context context;
    private ImageView profilePhoto;

    public SiblingPicture(Context context) {
        super(context);
        init(context);
    }

    public SiblingPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SiblingPicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SiblingPicture(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_sibling_picture, this, true);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
    }

    public void setChildBaseEntityId(BaseActivity baseActivity, String baseEntityId) {
        new GetChildDetailsTask(baseActivity, baseEntityId).execute();
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
                    .commonrepository("ec_child").findByCaseID(baseEntityId);
            if (rawDetails != null) {
                // Get extra child details
                CommonPersonObjectClient childDetails = MotherLookUpSmartClientsProvider.convert(rawDetails);
                childDetails.getColumnmaps().putAll(detailsRepository.getAllDetailsForClient(baseEntityId));

                // Get mother details
                String motherBaseEntityId = Utils.getValue(childDetails.getColumnmaps(), "relational_id", false);

                Map<String, String> motherDetails = new HashMap<>();
                motherDetails.put("mother_first_name", "");
                motherDetails.put("mother_last_name", "");
                motherDetails.put("mother_dob", "");
                motherDetails.put("mother_nrc_number", "");
                if (!TextUtils.isEmpty(motherBaseEntityId)) {
                    CommonPersonObject rawMotherDetails = baseActivity.getOpenSRPContext()
                            .commonrepository("ec_mother").findByCaseID(motherBaseEntityId);
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
        String genderString = Utils.getValue(childDetails.getColumnmaps(), "gender", false);
        if (genderString != null && genderString.toLowerCase().equals("female")) {
            gender = Gender.FEMALE;
        } else if (genderString != null && genderString.toLowerCase().equals("male")) {
            gender = Gender.MALE;
        }

        profilePhoto.setTag(org.ei.opensrp.R.id.entity_id, baseEntityId);
        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId,
                OpenSRPImageLoader.getStaticImageListener(profilePhoto,
                        ImageUtils.profileImageResourceByGender(gender),
                        ImageUtils.profileImageResourceByGender(gender)));

        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String firstName = Utils.getValue(childDetails.getColumnmaps(), "first_name", true);
                String lastName = Utils.getValue(childDetails.getColumnmaps(), "last_name", true);
                Toast.makeText(context, firstName + " " + lastName, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        this.setOnClickListener(new OnClickListener() {
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
