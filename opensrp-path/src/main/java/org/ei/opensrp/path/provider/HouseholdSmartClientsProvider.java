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
public class HouseholdSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    VaccineRepository vaccineRepository;
    WeightRepository weightRepository;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private static final String VACCINES_FILE = "vaccines.json";

    public HouseholdSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                         AlertService alertService, VaccineRepository vaccineRepository, WeightRepository weightRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.vaccineRepository = vaccineRepository;
        this.weightRepository = weightRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
    }

    @Override
    public void getView(SmartRegisterClient client, View convertView) {
        convertView.setLayoutParams(clientViewLayoutParams);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
//
        fillValue((TextView) convertView.findViewById(R.id.id), getValue(pc.getColumnmaps(), "HHID", false));
        fillValue((TextView) convertView.findViewById(R.id.registrationdate), getValue(pc.getColumnmaps(), "Date_Of_Reg", false));
        fillValue((TextView) convertView.findViewById(R.id.address), getValue(pc.getColumnmaps(), "address1", false));
//
//        String firstName = getValue(pc.getColumnmaps(), "first_name", true);
//        String lastName = getValue(pc.getColumnmaps(), "last_name", true);
//        String childName = getName(firstName, lastName);
//
//        String motherFirstName = getValue(pc.getColumnmaps(), "mother_first_name", true);
//        if (StringUtils.isBlank(childName) && StringUtils.isNotBlank(motherFirstName)) {
//            childName = "B/o " + motherFirstName.trim();
//        }
//        fillValue((TextView) convertView.findViewById(R.id.child_name), childName);
//
//        String motherName = getValue(pc.getColumnmaps(), "mother_first_name", true) + " " + getValue(pc, "mother_last_name", true);
//        if (!StringUtils.isNotBlank(motherName)) {
//            motherName = "M/G: " + motherName.trim();
//        }
//        fillValue((TextView) convertView.findViewById(R.id.child_mothername), motherName);
//
//        DateTime birthDateTime = new DateTime((new Date()).getTime());
//        String dobString = getValue(pc.getColumnmaps(), "dob", false);
//        String durationString = "";
//        if (StringUtils.isNotBlank(dobString)) {
//            try {
//                birthDateTime = new DateTime(dobString);
//                String duration = DateUtils.getDuration(birthDateTime);
//                if (duration != null) {
//                    durationString = duration;
//                }
//            } catch (Exception e) {
//                Log.e(getClass().getName(), e.toString(), e);
//            }
//        }

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
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_household_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    public enum State {
        DUE,
        OVERDUE,
        UPCOMING_NEXT_7_DAYS,
        UPCOMING,
        INACTIVE,
        LOST_TO_FOLLOW_UP,
        EXPIRED,
        WAITING,
        NO_ALERT,
        FULLY_IMMUNIZED
    }
}