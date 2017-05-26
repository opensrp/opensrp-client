package org.ei.opensrp.path.domain;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Keyman on 26/05/2017.
 */

public class ServiceSchedule {

    private final ServiceTrigger dueTrigger;
    private final ServiceTrigger expiryTrigger;


    public static ServiceSchedule getServiceSchedule(JSONObject schedule)
            throws JSONException {
        ServiceTrigger dueTrigger = ServiceTrigger.init(schedule.getJSONObject("due"));
        ServiceTrigger expiryTrigger = ServiceTrigger.init(schedule.optJSONObject("expiry"));
        return new ServiceSchedule(dueTrigger, expiryTrigger);
    }

    public ServiceSchedule(ServiceTrigger dueTrigger, ServiceTrigger expiryTrigger) {
        this.dueTrigger = dueTrigger;
        this.expiryTrigger = expiryTrigger;
    }

    public ServiceTrigger getDueTrigger() {
        return dueTrigger;
    }

    public ServiceTrigger getExpiryTrigger() {
        return expiryTrigger;
    }

    public static DateTime addOffsetToDateTime(DateTime dateTime, List<String> offsets) {

        if (dateTime != null && offsets != null && !offsets.isEmpty()) {
            for (String offset : offsets) {
                dateTime = addOffsetToDateTime(dateTime, offset);
            }
        }
        return dateTime;
    }

    public static DateTime addOffsetToDateTime(DateTime dateTime, String offset) {
        if (dateTime != null && offset != null) {
            offset = offset.replace(" ", "").toLowerCase();
            Pattern p1 = Pattern.compile("([-+]{1})(.*)");
            Matcher m1 = p1.matcher(offset);
            if (m1.find()) {
                String comparitorString = m1.group(1);
                String valueString = m1.group(2);

                int comparitor = 1;
                if (comparitorString.equals("-")) {
                    comparitor = -1;
                }

                String[] values = valueString.split(",");
                for (int i = 0; i < values.length; i++) {
                    Pattern p2 = Pattern.compile("(\\d+)([dwmy]{1})");
                    Matcher m2 = p2.matcher(values[i]);

                    if (m2.find()) {
                        int curValue = comparitor * Integer.parseInt(m2.group(1));
                        String fieldString = m2.group(2);
                        if (fieldString.equals("d")) {
                            dateTime.plusDays(curValue);
                        } else if (fieldString.equals("m")) {
                            dateTime.plusMonths(curValue);
                        } else if (fieldString.equals("y")) {
                            dateTime.plusYears(curValue);
                        }
                    }
                }
            }
        }

        return dateTime;
    }
}

