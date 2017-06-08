package util;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.path.domain.ServiceSchedule;
import org.ei.opensrp.path.domain.ServiceTrigger;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by keyman on 17/05/2017.
 */
public class DatabaseUtils {

    public static void populateRecurringServices(Context context, SQLiteDatabase database, RecurringServiceTypeRepository recurringServiceTypeRepository) {
        try {
            String supportedRecurringServices = VaccinatorUtils.getSupportedRecurringServices(context);
            if (StringUtils.isNotBlank(supportedRecurringServices)) {
                JSONArray jsonArray = new JSONArray(supportedRecurringServices);
                if (jsonArray == null) {
                    return;
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String type = JsonFormUtils.getString(jsonObject, "type");
                    String serviceLogic = JsonFormUtils.getString(jsonObject, "service_logic");
                    String units = JsonFormUtils.getString(jsonObject, "units");
                    String serviceNameEntity = null;
                    String serviceNameEntityId = null;

                    JSONObject serviceName = JsonFormUtils.getJSONObject(jsonObject, "openmrs_service_name");
                    if (serviceName != null) {
                        serviceNameEntity = JsonFormUtils.getString(serviceName, "entity");
                        serviceNameEntityId = JsonFormUtils.getString(serviceName, "entity_id");
                    }

                    String dateEntity = null;
                    String dateEntityId = null;

                    JSONObject date = JsonFormUtils.getJSONObject(jsonObject, "openmrs_date");
                    if (serviceName != null) {
                        dateEntity = JsonFormUtils.getString(date, "entity");
                        dateEntityId = JsonFormUtils.getString(date, "entity_id");
                    }

                    JSONArray services = JsonFormUtils.getJSONArray(jsonObject, "services");

                    List<String> mileStones = new ArrayList<>();
                    if (services != null) {
                        for (int j = 0; j < services.length(); j++) {
                            JSONObject service = services.getJSONObject(j);
                            Long id = JsonFormUtils.getLong(service, "id");
                            String name = JsonFormUtils.getString(service, "name");
                            String dose = JsonFormUtils.getString(service, "dose");

                            JSONObject schedule = JsonFormUtils.getJSONObject(service, "schedule");

                            String prerequisite = null;
                            String preOffset = null;
                            String expiryOffSet = null;
                            String milestoneOffset = null;
                            try {
                                ServiceSchedule serviceSchedule = ServiceSchedule.getServiceSchedule(schedule);
                                ServiceTrigger dueTrigger = serviceSchedule.getDueTrigger();
                                ServiceTrigger expiryTrigger = serviceSchedule.getExpiryTrigger();

                                if (dueTrigger != null && expiryTrigger != null) {
                                    switch (dueTrigger.getReference()) {
                                        case DOB:
                                            prerequisite = dueTrigger.getReference().name().toLowerCase();
                                            break;
                                        case PREREQUISITE:
                                            prerequisite = dueTrigger.getReference().name().toLowerCase() + "|" + dueTrigger.getPrerequisite();
                                            break;
                                        case MULTIPLE:
                                            ServiceTrigger.Multiple multiple = dueTrigger.getMultiple();
                                            String condition = multiple.getCondition();
                                            List<String> prerequisites = multiple.getPrerequisites();
                                            String[] preArray = prerequisites.toArray(new String[prerequisites.size()]);

                                            prerequisite = dueTrigger.getReference().name().toLowerCase() + "|" + condition + "|" + Arrays.toString(preArray);
                                            break;

                                    }

                                    preOffset = dueTrigger.getOffset();
                                    mileStones.add(preOffset);

                                    expiryOffSet = expiryTrigger.getOffset();

                                    String[] milestoneArray = mileStones.toArray(new String[mileStones.size()]);
                                    milestoneOffset = Arrays.toString(milestoneArray);

                                }
                            } catch (Exception e) {
                                Log.e(DatabaseUtils.class.getName(), e.getMessage(), e);
                            }


                            ServiceType serviceType = new ServiceType();
                            serviceType.setId(id);
                            serviceType.setType(type);
                            serviceType.setName(name);
                            serviceType.setServiceNameEntity(serviceNameEntity);
                            serviceType.setServiceNameEntityId(serviceNameEntityId);
                            serviceType.setDateEntity(dateEntity);
                            serviceType.setDateEntityId(dateEntityId);
                            if (dose != null && units != null) {
                                serviceType.setUnits(dose + "  " + units);
                            }

                            serviceType.setServiceLogic(serviceLogic);
                            serviceType.setPrerequisite(prerequisite);
                            serviceType.setPreOffset(preOffset);
                            serviceType.setExpiryOffset(expiryOffSet);
                            serviceType.setMilestoneOffset(milestoneOffset);
                            recurringServiceTypeRepository.add(serviceType, database);

                        }
                    }

                }
            }
        } catch (JSONException e) {
            Log.e(DatabaseUtils.class.getName(), e.getMessage(), e);
        }
    }
}
