package org.ei.opensrp.path.domain;

import org.ei.opensrp.path.application.VaccinatorApplication;
import org.opensrp.api.constants.Gender;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 31/05/2017.
 */

public class ZScore {
    public static int MAX_REPRESENTED_AGE = 60;
    private final Gender gender;
    private final int month;
    private final double l;
    private final double m;
    private final double s;
    private final double sd3Neg;
    private final double sd2Neg;
    private final double sd1Neg;
    private final double sd0;
    private final double sd1;
    private final double sd2;
    private final double sd3;

    public ZScore(Gender gender, int month, double l, double m, double s, double sd3Neg,
                  double sd2Neg, double sd1Neg, double sd0, double sd1, double sd2, double sd3) {
        this.gender = gender;
        this.month = month;
        this.l = l;
        this.m = m;
        this.s = s;
        this.sd3Neg = sd3Neg;
        this.sd2Neg = sd2Neg;
        this.sd1Neg = sd1Neg;
        this.sd0 = sd0;
        this.sd1 = sd1;
        this.sd2 = sd2;
        this.sd3 = sd3;
    }

    /**
     * This method calculates Z (The z-score) using the formulae provided here https://www.cdc.gov/growthcharts/percentile_data_files.htm
     *
     * @param x The weight to use
     * @return
     */
    public double getZ(double x) {
        if (l != 0) {
            return (Math.pow((x / m), l) - 1) / (l * s);
        } else {
            return Math.log(x / m) / s;
        }
    }

    /**
     * This method calculates X (weight) given the Z-Score
     *
     * @param z The z-score to use to calculate X
     * @return
     */
    public double getX(double z) {
        if (l != 0) {
            return m * Math.pow(Math.E, Math.log((z * l * s) + 1) / l);
        } else {
            return m * Math.pow(Math.E, z * s);
        }
    }

    public static Double calculate(Gender gender, Date dateOfBirth, Date weighingDate, double weight) {
        if (dateOfBirth != null && gender != null && weighingDate != null) {
            int ageInMonths = getAgeInMonths(dateOfBirth, weighingDate);
            List<ZScore> zScores = VaccinatorApplication.getInstance().zScoreRepository().findByGender(gender);

            ZScore zScoreToUse = null;
            for (ZScore curZScore : zScores) {
                if (curZScore.month == ageInMonths) {
                    zScoreToUse = curZScore;
                    break;
                }
            }

            if (zScoreToUse != null) {
                return new Double(zScoreToUse.getZ(weight));
            }
        }

        return null;
    }

    /**
     * This method calculates the expected weight given
     *
     * @param gender
     * @param ageInMonths
     * @param z
     * @return
     */
    public static Double reverse(Gender gender, int ageInMonths, Double z) {
        List<ZScore> zScores = VaccinatorApplication.getInstance().zScoreRepository().findByGender(gender);

        ZScore zScoreToUse = null;
        for (ZScore curZScore : zScores) {
            if (curZScore.month == ageInMonths) {
                zScoreToUse = curZScore;
                break;
            }
        }

        if (zScoreToUse != null) {
            return new Double(zScoreToUse.getX(z));
        }

        return null;
    }

    public static int getAgeInMonths(Date dateOfBirth, Date weighingDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        standardiseCalendarDate(dobCalendar);

        Calendar weighingCalendar = Calendar.getInstance();
        weighingCalendar.setTime(weighingDate);
        standardiseCalendarDate(weighingCalendar);

        int result = 0;
        if (dobCalendar.getTimeInMillis() <= weighingCalendar.getTimeInMillis()) {
            int yearDiff = weighingCalendar.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
            result = (yearDiff * 12) + (weighingCalendar.get(Calendar.MONTH) - dobCalendar.get(Calendar.MONTH));
        }

        return result;
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }
}
