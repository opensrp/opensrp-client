package util;

import android.support.v4.util.TimeUtils;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by keyman on 17/02/2017.
 */
public class DateUtils {

    public static String getDuration(DateTime dateTime) {
        if (dateTime != null) {
            Calendar dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(dateTime.toDate());
            dateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            dateCalendar.set(Calendar.MINUTE, 0);
            dateCalendar.set(Calendar.SECOND, 0);
            dateCalendar.set(Calendar.MILLISECOND, 0);

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long timeDiff = Math.abs(dateCalendar.getTimeInMillis() - today.getTimeInMillis());
            return getDuration(timeDiff);
        }
        return null;
    }

    public static String getDuration(long timeDiff) {
        StringBuilder builder = new StringBuilder();
        TimeUtils.formatDuration(timeDiff, builder);
        String duration = "";
        if (timeDiff >= 0
                && timeDiff <= TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)) {
            // Represent in days
            long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
            duration = days + "d";
        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)) {
            // Represent in weeks and days
            int weeks = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
            int days = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(weeks * 7, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

            if (days >= 7) {
                days = 0;
                weeks++;
            }

            duration = weeks + "w";
            if (days > 0) {
                duration += " " + days + "d";
            }
        } else if (timeDiff > TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)
                && timeDiff <= TimeUnit.MILLISECONDS.convert(363, TimeUnit.DAYS)) {
            // Represent in months and weeks
            int months = (int) Math.floor((float) timeDiff /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
            int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                    months * 30, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

            if (weeks >= 4) {
                weeks = 0;
                months++;
            }

            if(months < 12) {
                duration = months + "m";
                if (weeks > 0 && months < 12) {
                    duration += " " + weeks + "w";
                }
            }
            else if (months >= 12) {
                duration = "1y";
            }
        } else {
            // Represent in years and months
            int years = (int) Math.floor((float) timeDiff
                    / TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
            int months = (int) Math.floor((float) (timeDiff -
                    TimeUnit.MILLISECONDS.convert(years * 365, TimeUnit.DAYS)) /
                    TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

            if (months >= 12) {
                months = 0;
                years++;
            }

            duration = years + "y";
            if (months > 0) {
                duration += " " + months + "m";
            }
        }

        return duration;

    }

}
