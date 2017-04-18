package org.ei.opensrp.path.provider;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.DateUtils;
import util.ImageUtils;
import util.VaccinateActionUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;
import static util.VaccinatorUtils.generateScheduleList;
import static util.VaccinatorUtils.nextVaccineDue;
import static util.VaccinatorUtils.receivedVaccines;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildLookUpSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private static final String VACCINES_FILE = "vaccines.json";

    public ChildLookUpSmartClientsProvider(Context context, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
    }

    @Override
    public void getView(SmartRegisterClient client, View convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        fillValue((TextView) convertView.findViewById(R.id.child_zeir_id), getValue(pc.getColumnmaps(), "zeir_id", false));

        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
        String childName = getName(firstName, lastName);

        String motherFirstName = getValue(pc.getColumnmaps(), "mother_first_name", true);
        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
            childName = "B/o " + motherFirstName.trim();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_name), childName);

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
        fillValue((TextView) convertView.findViewById(R.id.child_age), durationString);

        fillValue((TextView) convertView.findViewById(R.id.child_card_number), pc.getColumnmaps(), "epi_card_number", false);

        String gender = getValue(pc.getColumnmaps(), "gender", true);
        int defaultImageResId = ImageUtils.profileImageResourceByGender(gender);

        ImageView profilePic = (ImageView) convertView.findViewById(R.id.child_profilepic);
        profilePic.setImageResource(defaultImageResId);
        if (client.entityId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profilePic.setTag(org.ei.opensrp.R.id.entity_id, pc.getCaseId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(profilePic, 0, 0));
        }

        convertView.findViewById(R.id.child_profile_info_layout).setTag(client);
        convertView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        convertView.setLayoutParams(clientViewLayoutParams);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.child_lookup_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

}