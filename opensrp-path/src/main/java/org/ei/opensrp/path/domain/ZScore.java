package org.ei.opensrp.path.domain;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.opensrp.api.constants.Gender;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 31/05/2017.
 */

public class ZScore {
    public static double MAX_REPRESENTED_AGE = 60d;
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

    public static int getZScoreColor(double zScore) {
        zScore = Math.abs(zScore);
        if (zScore < 2) {
            return R.color.z_score_0;
        } else if (zScore >= 2 && zScore < 3) {
            return R.color.z_score_2;
        } else {
            return R.color.z_score_3;
        }
    }

    public static double roundOff(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
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
            int ageInMonths = (int) Math.round(getAgeInMonths(dateOfBirth, weighingDate));
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
     * @param ageInMonthsDouble
     * @param z
     * @return
     */
    public static Double reverse(Gender gender, double ageInMonthsDouble, Double z) {
        int ageInMonths = (int) Math.round(ageInMonthsDouble);
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

    public static double getAgeInMonths(Date dateOfBirth, Date weighingDate) {
        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.setTime(dateOfBirth);
        standardiseCalendarDate(dobCalendar);

        Calendar weighingCalendar = Calendar.getInstance();
        weighingCalendar.setTime(weighingDate);
        standardiseCalendarDate(weighingCalendar);

        double result = 0;
        if (dobCalendar.getTimeInMillis() <= weighingCalendar.getTimeInMillis()) {
            result = ((double)(weighingCalendar.getTimeInMillis() - dobCalendar.getTimeInMillis())) / 2629746000l;
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
