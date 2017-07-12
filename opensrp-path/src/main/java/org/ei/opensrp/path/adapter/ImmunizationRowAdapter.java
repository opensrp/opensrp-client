package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.domain.Photo;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.view.VaccineCard;
import org.ei.opensrp.path.view.ImmunizationRowCard;
import org.ei.opensrp.path.view.ImmunizationRowGroup;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import util.ImageUtils;
import util.Utils;

import static util.Utils.getValue;

/**
 * Created by raihan on 13/03/2017.
 */
public class ImmunizationRowAdapter extends BaseAdapter {
    private static final String TAG = "ImmunizationRowAdapter";
    private final Context context;
    private HashMap<String, ImmunizationRowCard> vaccineCards;
    private final ImmunizationRowGroup vaccineGroup;
    public boolean editmode;

    public ImmunizationRowAdapter(Context context, ImmunizationRowGroup vaccineGroup, boolean editmode) throws JSONException {
        this.context = context;
        this.editmode = editmode;
        this.vaccineGroup = vaccineGroup;
        vaccineCards = new HashMap<>();
    }

    @Override
    public int getCount() {
        try {
            return vaccineGroup.getVaccineData().getJSONArray("vaccines").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return vaccineCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 231231 + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            JSONObject vaccineData = vaccineGroup.getVaccineData().getJSONArray("vaccines")
                    .getJSONObject(position);
            String vaccineName = vaccineData.getString("name");
            if (!vaccineCards.containsKey(vaccineName)) {
                ImmunizationRowCard vaccineCard = new ImmunizationRowCard(context, editmode);
                vaccineCard.setOnVaccineStateChangeListener(vaccineGroup);
                vaccineCard.setOnClickListener(vaccineGroup);
                vaccineCard.getUndoB().setOnClickListener(vaccineGroup);
                vaccineCard.setId((int) getItemId(position));
                VaccineWrapper vaccineWrapper = new VaccineWrapper();
                vaccineWrapper.setId(vaccineGroup.getChildDetails().entityId());
                vaccineWrapper.setGender(vaccineGroup.getChildDetails().getDetails().get("gender"));
                vaccineWrapper.setName(vaccineName);

                String dobString = Utils.getValue(vaccineGroup.getChildDetails().getColumnmaps(), "dob", false);
                if (StringUtils.isNotBlank(dobString)) {
                    Calendar dobCalender = Calendar.getInstance();
                    DateTime dateTime = new DateTime(dobString);
                    dobCalender.setTime(dateTime.toDate());
                    dobCalender.add(Calendar.DATE, vaccineGroup.getVaccineData().getInt("days_after_birth_due"));
                    vaccineWrapper.setVaccineDate(new DateTime(dobCalender.getTime()));
                }


                Photo photo = ImageUtils.profilePhotoByClient(vaccineGroup.getChildDetails());
                vaccineWrapper.setPhoto(photo);

                String zeirId = getValue(vaccineGroup.getChildDetails().getColumnmaps(), "zeir_id", false);
                vaccineWrapper.setPatientNumber(zeirId);
                vaccineWrapper.setPatientName(getValue(vaccineGroup.getChildDetails().getColumnmaps(), "first_name", true) + " " + getValue(vaccineGroup.getChildDetails().getColumnmaps(), "last_name", true));

                vaccineGroup.updateWrapper(vaccineWrapper);
                vaccineGroup.updateWrapperStatus(vaccineWrapper);
                vaccineCard.setVaccineWrapper(vaccineWrapper);

                vaccineCards.put(vaccineName, vaccineCard);
                vaccineGroup.toggleRecordAllTV();
            }

            return vaccineCards.get(vaccineName);
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    public void update(ArrayList<VaccineWrapper> vaccinesToUpdate) {
        if (vaccineCards != null) {
            if (vaccinesToUpdate == null) {// Update all vaccines
                for (ImmunizationRowCard curCard : vaccineCards.values()) {
                    if (curCard != null) curCard.updateState();
                }
            } else {// Update just the vaccines specified
                for (VaccineWrapper currWrapper : vaccinesToUpdate) {
                    if (vaccineCards.containsKey(currWrapper.getName())) {
                        vaccineCards.get(currWrapper.getName()).updateState();
                    }
                }
            }
        }
    }

    public ArrayList<VaccineWrapper> getDueVaccines() {
        ArrayList<VaccineWrapper> dueVaccines = new ArrayList<>();
        if (vaccineCards != null) {
            for (ImmunizationRowCard curCard : vaccineCards.values()) {
                if (curCard != null && (curCard.getState().equals(VaccineCard.State.DUE)
                        || curCard.getState().equals(VaccineCard.State.OVERDUE))) {
                    dueVaccines.add(curCard.getVaccineWrapper());
                }
            }
        }

        return dueVaccines;
    }

    private void updateWrapper(VaccineWrapper tag) {
        List<Vaccine> vaccineList = vaccineGroup.getVaccineList();
        if (!vaccineList.isEmpty()) {
            for (Vaccine vaccine : vaccineList) {
                if (tag.getName().equals(vaccine.getName()) && vaccine.getDate() != null) {
                    long diff = vaccine.getUpdatedAt() - vaccine.getDate().getTime();
                    if (diff > 0 && TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 1) {
                        tag.setUpdatedVaccineDate(new DateTime(vaccine.getDate()), false);
                    } else {
                        tag.setUpdatedVaccineDate(new DateTime(vaccine.getDate()), true);
                    }
                    tag.setDbKey(vaccine.getId());
                }
            }
        }

    }
}
