package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.path.domain.Photo;
import org.ei.opensrp.path.domain.ServiceWrapper;
import org.ei.opensrp.path.view.ServiceRowCard;
import org.ei.opensrp.path.view.ServiceRowGroup;
import org.ei.opensrp.path.view.VaccineCard;
import org.joda.time.DateTime;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import util.ImageUtils;
import util.Utils;

import static util.Utils.getName;
import static util.Utils.getValue;

/**
 * Created by keyman on 15/05/2017.
 */
public class ServiceRowAdapter extends BaseAdapter {
    private static final String TAG = "ServiceRowAdapter";
    private final Context context;
    private HashMap<String, ServiceRowCard> serviceRowCards;
    private final ServiceRowGroup serviceRowGroup;
    public boolean editmode;

    public ServiceRowAdapter(Context context, ServiceRowGroup serviceRowGroup, boolean editmode) throws JSONException {
        this.context = context;
        this.editmode = editmode;
        this.serviceRowGroup = serviceRowGroup;
        serviceRowCards = new LinkedHashMap<>();
    }

    @Override
    public int getCount() {
        List<ServiceType> types = serviceRowGroup.getServiceTypes();
        if (types == null || types.isEmpty()) {
            return 0;
        }
        return types.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceRowCards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 231231 + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ServiceType serviceType = serviceRowGroup.getServiceTypes().get(position);
            if (!serviceRowCards.containsKey(serviceType.getName())) {
                ServiceRowCard serviceRowCard = new ServiceRowCard(context, editmode);
                serviceRowCard.setOnClickListener(serviceRowGroup);
                serviceRowCard.getUndoB().setOnClickListener(serviceRowGroup);
                serviceRowCard.setId((int) getItemId(position));
                ServiceWrapper serviceWrapper = new ServiceWrapper();
                serviceWrapper.setId(serviceRowGroup.getChildDetails().entityId());
                serviceWrapper.setGender(serviceRowGroup.getChildDetails().getDetails().get("gender"));
                serviceWrapper.setDefaultName(serviceType.getName());

                String dobString = Utils.getValue(serviceRowGroup.getChildDetails().getColumnmaps(), "dob", false);
                if (StringUtils.isNotBlank(dobString)) {
                    Calendar dobCalender = Calendar.getInstance();
                    DateTime dateTime = new DateTime(dobString);
                    dobCalender.setTime(dateTime.toDate());
                    serviceWrapper.setDob(new DateTime(dobCalender.getTime()));
                }

                Photo photo = ImageUtils.profilePhotoByClient(serviceRowGroup.getChildDetails());
                serviceWrapper.setPhoto(photo);

                String zeirId = getValue(serviceRowGroup.getChildDetails().getColumnmaps(), "zeir_id", false);
                serviceWrapper.setPatientNumber(zeirId);

                String firstName = getValue(serviceRowGroup.getChildDetails().getColumnmaps(), "first_name", true);
                String lastName = getValue(serviceRowGroup.getChildDetails().getColumnmaps(), "last_name", true);
                String childName = getName(firstName, lastName);
                serviceWrapper.setPatientName(childName.trim());

                serviceRowGroup.updateWrapperStatus(serviceWrapper);
                serviceRowGroup.updateWrapper(serviceWrapper);
                serviceRowCard.setServiceWrapper(serviceWrapper);

                serviceRowCards.put(serviceType.getName(), serviceRowCard);
            }

            return serviceRowCards.get(serviceType.getName());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    public void update(ArrayList<ServiceWrapper> servicesToUpdate) {
        if (serviceRowCards != null) {
            if (servicesToUpdate == null) {// Update all vaccines
                for (ServiceRowCard curCard : serviceRowCards.values()) {
                    if (curCard != null) curCard.updateState();
                }
            } else {// Update just the vaccines specified
                for (ServiceWrapper currWrapper : servicesToUpdate) {
                    if (serviceRowCards.containsKey(currWrapper.getName())) {
                        serviceRowCards.get(currWrapper.getName()).updateState();
                    }
                }
            }
        }
    }

}
