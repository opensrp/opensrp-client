package util;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                    if (services != null) {
                        for (int j = 0; i < services.length(); j++) {
                            JSONObject service = services.getJSONObject(j);
                            Long id = JsonFormUtils.getLong(service, "id");
                            String name = JsonFormUtils.getString(service, "name");
                            String dose = JsonFormUtils.getString(service, "dose");

                            ServiceType serviceType = new ServiceType();
                            serviceType.setId(id);
                            serviceType.setName(name);
                            serviceType.setServiceNameEntity(serviceNameEntity);
                            serviceType.setServiceNameEntityId(serviceNameEntityId);
                            serviceType.setDateEntity(dateEntity);
                            serviceType.setDateEntityId(dateEntityId);
                            if (dose != null && units != null) {
                                serviceType.setUnits(dose + "  " + units);
                            }
                            serviceType.setServiceLogic(serviceLogic);
                            recurringServiceTypeRepository.add(serviceType, database);

                        }
                    }

                }
            }
        } catch (JSONException e) {

        }
    }
}
