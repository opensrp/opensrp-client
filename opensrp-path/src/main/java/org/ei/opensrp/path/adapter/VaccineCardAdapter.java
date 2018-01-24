package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.Photo;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.view.VaccineCard;
import org.ei.opensrp.path.view.VaccineGroup;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import util.ImageUtils;
import util.Utils;

import static util.Utils.getName;
import static util.Utils.getValue;

/**
 * Created by Jason Rogena - jrogena@ona.io on 22/02/2017.
 */
public class VaccineCardAdapter extends BaseAdapter {
    private static final String TAG = "VaccineCardAdapter";
    private final Context context;
    private HashMap<String, VaccineCard> vaccineCards;
    private final VaccineGroup vaccineGroup;
    private final String type;
    private SimpleDateFormat simple_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public VaccineCardAdapter(Context context, VaccineGroup vaccineGroup,String type) throws JSONException {
        this.context = context;
        this.vaccineGroup = vaccineGroup;
        vaccineCards = new HashMap<>();
        this.type = type;
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
                VaccineCard vaccineCard = new VaccineCard(context);
                vaccineCard.setOnVaccineStateChangeListener(vaccineGroup);
                vaccineCard.setOnClickListener(vaccineGroup);
                vaccineCard.getUndoB().setOnClickListener(vaccineGroup);
                vaccineCard.setId((int) getItemId(position));
                VaccineWrapper vaccineWrapper = new VaccineWrapper();
                vaccineWrapper.setId(vaccineGroup.getChildDetails().entityId());
                vaccineWrapper.setGender(vaccineGroup.getChildDetails().getDetails().get("gender"));
                vaccineWrapper.setName(vaccineName);
                vaccineWrapper.setDefaultName(vaccineName);
                DateTime dateTime = null;
                String dobString = "";
                if(type.equalsIgnoreCase("child")) {
                    dobString = Utils.getValue(vaccineGroup.getChildDetails().getColumnmaps(), "dob", false);
                    dateTime = new DateTime(dobString);
                }else if(type.equalsIgnoreCase("mother")){
                    dobString = Utils.getValue(vaccineGroup.getChildDetails().getColumnmaps(), "lmp", false);
                    try {
                        dateTime = new DateTime(simple_DATE_FORMAT.parse(dobString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                ///////////////fix for vaccine date display /////////////////////////////////
                VaccineRepo.Vaccine vaccine = null;
                ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines(type);
                for(int i = 0;i<vaccines.size();i++){
                    String displayvaccine = vaccines.get(i).display();
                    String wrappervaccinename =vaccineWrapper.getName();
                    if(vaccines.get(i).display().equalsIgnoreCase(vaccineWrapper.getName())){
                        vaccine = vaccines.get(i);
                    }else if(vaccines.get(i).display().toLowerCase().contains("measles") || vaccines.get(i).display().toLowerCase().contains("mr")){
                        if(vaccineWrapper.getName().toLowerCase().contains(vaccines.get(i).display().toLowerCase())){
                            vaccine = vaccines.get(i);
                        }
                    }
                }

                VaccineRepo.Vaccine prerequisitevaccine = null;
                Date prerequisitevaccineGivenDate = null;
                if(vaccine != null) {
                    if (vaccine.prerequisite() != null) {
                        prerequisitevaccine = vaccine.prerequisite();
                        VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                        List<Vaccine> givenVaccines = vaccineRepository.findByEntityId(vaccineGroup.getChildDetails().entityId());
                        for (int i = 0; i < givenVaccines.size(); i++) {
                            if (givenVaccines.get(i).getName().equalsIgnoreCase(prerequisitevaccine.display())) {
                                prerequisitevaccineGivenDate = givenVaccines.get(i).getDate();
                                Calendar dobCalender = Calendar.getInstance();
                                dobCalender.setTime(prerequisitevaccineGivenDate);
                                dobCalender.add(Calendar.DATE, vaccine.prerequisiteGapDays());
                                vaccineWrapper.setVaccineDate(new DateTime(dobCalender.getTime()));
                            }
                        }
                    }
                }
                //////////////////////////////////////////////////////////////////////////////////
                if(vaccineWrapper.getVaccineDate() ==null) {
                    if(StringUtils.isNotBlank(dobString) && dateTime != null) {
                        Calendar dobCalender = Calendar.getInstance();
//                    DateTime dateTime = new DateTime(dobString);
                        dobCalender.setTime(dateTime.toDate());
                        dobCalender.add(Calendar.DATE, vaccineGroup.getVaccineData().getInt("days_after_birth_due"));
                        vaccineWrapper.setVaccineDate(new DateTime(dobCalender.getTime()));
                    }
                }

                Photo photo = ImageUtils.profilePhotoByClient(vaccineGroup.getChildDetails());
                vaccineWrapper.setPhoto(photo);

                String zeirId = getValue(vaccineGroup.getChildDetails().getColumnmaps(), "zeir_id", false);
                vaccineWrapper.setPatientNumber(zeirId);

                String firstName = getValue(vaccineGroup.getChildDetails().getColumnmaps(), "first_name", true);
                String lastName = getValue(vaccineGroup.getChildDetails().getColumnmaps(), "last_name", true);
                String childName = getName(firstName, lastName);
                vaccineWrapper.setPatientName(childName.trim());

                vaccineGroup.updateWrapper(vaccineWrapper);
                vaccineGroup.updateWrapperStatus(vaccineWrapper,type);
                vaccineCard.setVaccineWrapper(vaccineWrapper);

                vaccineCards.put(vaccineName, vaccineCard);
            }

            //If last position, toggle RecordAll
            if (position == (getCount() - 1)) {
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
                for (VaccineCard curCard : vaccineCards.values()) {
                    if (curCard != null) curCard.updateState();
                }
            } else {// Update just the vaccines specified
                for (VaccineWrapper currWrapper : vaccinesToUpdate) {
                    if (vaccineCards.containsKey(currWrapper.getName())) {
                        vaccineCards.get(currWrapper.getName()).setVaccineWrapper(currWrapper);
                    }
                }
            }
        }
    }

    public ArrayList<VaccineWrapper> getDueVaccines() {
        ArrayList<VaccineWrapper> dueVaccines = new ArrayList<>();
        if (vaccineCards != null) {
            for (VaccineCard curCard : vaccineCards.values()) {
                if (curCard != null && (curCard.getState().equals(VaccineCard.State.DUE)
                        || curCard.getState().equals(VaccineCard.State.OVERDUE))) {
                    dueVaccines.add(curCard.getVaccineWrapper());
                }
            }
        }

        return dueVaccines;
    }

    public ArrayList<VaccineWrapper> getAllVaccineWrappers() {
        ArrayList<VaccineWrapper> allWrappers = new ArrayList<>();
        for (VaccineCard curCard : vaccineCards.values()) {
            allWrappers.add(curCard.getVaccineWrapper());
        }

        return allWrappers;
    }

}
