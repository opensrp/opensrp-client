package org.ei.opensrp.path.service;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.path.repository.PathRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by coder on 5/19/17.
 */
public class HIA2Service {
    DateFormat dfyymm = new SimpleDateFormat("yyyy-MM");

    private static String EC_CHILD_TABLE = "ec_child";
    private static String CHN1_005_NAME = "CHN1-005";
    private static String CHN1_005_DHIS_ID = "n0uHub5ubqH";
    private static String CHN1_010 = "CHN1-010";
    private static String CHN1_010_DHIS_ID = "IWwblgpMxiS";
    private static String CHN1_011 = "CHN1-011";
    private static String CHN1_011_DHIS_ID = "unknown";
    private static String CHN1_015 = "CHN1-015";
    private static String CHN1_015_DHIS_ID = "fl4bPFJRI5j";
    private static String CHN1_020 = "CHN1-020";
    private static String CHN1_020_DHIS_ID = "ZDSUD6VHnoh";
    private static String CHN1_021 = "CHN1-021";
    private static String CHN1_021_DHIS_ID = "ZDSUD6VHnoh";
    private static String CHN1_025 = "CHN1-025";
    private static String CHN1_025_DHIS_ID = "YAY7yKAkSvq";
    private static String CHN1_030 = "CHN1-030";
    private static String CHN1_030_DHIS_ID = "WFxN7txijYV";
    private static String CHN2_005 = "CHN2-005";
    private static String CHN2_005_DHIS_ID = "adkGrSGNt3L";
    private static String CHN2_010 = "CHN2-010";
    private static String CHN2_010_DHIS_ID = "sSxqU6qPyXr";
    private static String CHN2_015 = "CHN2-015";
    private static String CHN2_015_DHIS_ID = "xIGHv5CY2fF";
    private static String CHN2_020 = "CHN2-020";
    private static String CHN2_020_DHIS_ID = "H5cadfqRh7I";
    private static String CHN2_025 = "CHN2-025";
    private static String CHN2_025_DHIS_ID = "xWDkbLq9kji";
    private static String CHN2_030 = "CHN2-030";
    private static String CHN2_030_DHIS_ID = "e10sC5c4pRz";
    private static String CHN2_035 = "CHN2-035";
    private static String CHN2_035_DHIS_ID = "lcpx7xdVC3z";
    private static String CHN2_040 = "CHN2-040";
    private static String CHN2_040_DHIS_ID = "hi9sRtkzimM";
    private static String CHN2_041 = "CHN2-041";
    private static String CHN2_041_DHIS_ID = "unknown";
    private static String CHN2_045 = "CHN2-045";
    private static String CHN2_045_DHIS_ID = "LpkrzZezPhP";
    private static String CHN2_050 = "CHN2-050";
    private static String CHN2_050_DHIS_ID = "AzLJv6qTtPO";
    private static String CHN2_051 = "CHN2-051";
    private static String CHN2_051_DHIS_ID = "unknown";
    private static String CHN2_055 = "CHN2-055";
    private static String CHN2_055_DHIS_ID = "gdrQ69fCF8B";
    private static String CHN2_060 = "CHN2-060";
    private static String CHN2_060_DHIS_ID = "ke26q8KPQPM";
    private static String CHN2_061 = "CHN2-061";
    private static String CCHN2_061_DHIS_ID = "unknown";


//FIXME to uniquely identify out of areas change group by child.base_entity_id to group by zeir_id
    public static void generateIndicators(final SQLiteDatabase database, int month) {

    }

    /**
     * Number of male children aged < 12 months who attended a clinic this month.
     *
     * @param database
     */
    private void getCHN1_005(SQLiteDatabase database, String gender) {
        String query = "select count(*) from " + EC_CHILD_TABLE + " child inner join " + PathRepository.Table.event.name() + " e on e." + PathRepository.event_column.baseEntityId.name() + "= child.id" +
                " where strftime('%Y-%m',e." + PathRepository.event_column.eventDate.name() + ")='" + dfyymm.format(new Date()) + "' and date(child.dob,'+12 months')<'now()' and child.gender=" + (gender.isEmpty() ? "Male" : gender);
        // lable,dhisatt,value

    }

    /**
     * Number of female children aged < 12 months who attended a clinic this month.
     */
    private void getCHN1_010(SQLiteDatabase db) {
        getCHN1_005(db, "Female");
    }

    /**
     * Number of total children aged < 12 months who attended a clinic this month.	"[CHN1-005] + [CHN1-010]
     * [Non-editable in the form]"
     */
    private void getCHN1_011() {

    }

    /**
     * Number of male children aged 12 to 59 months who attended a clinic this month
     */
    private void getCHN1_015(SQLiteDatabase db, String gender) {
        gender = gender == null || gender.isEmpty() ? "Male" : gender;
        String query = "select count(*) as count," + ageQuery() + " from ec_child child inner join event e on e.baseEntityId=child.base_entity_id where  child.gender='" + gender + "' and strftime('%Y-%m',e.eventDate) ='" + dfyymm.format(new Date()) + "' and age between 12 and 59";

    }

    /**
     * Number of female children aged 12 to 59 months who attended a clinic this month
     */
    private void getCHN1_020(SQLiteDatabase db) {
        getCHN1_015(db, "Female");
    }

    /**
     * Number of Total children aged 12 to 59 months who attended clinic this month
     * [CHN1-015] + [CHN1-020]
     * [Non-editable in the form]
     *
     * @param db
     */
    private void getCHN1_021(SQLiteDatabase db) {

    }

    /**
     * Number of total children < 5 who attended a clinic this month
     * "[CHN1-011] + [CHN1-021]
     * [Non-editable in the form]"
     *
     * @param db
     */
    private void getCHN1_025(SQLiteDatabase db) {

    }

    /**
     * Number of total children who attended clinic and are not part of clinic's catchment area
     * COUNT Number of total children who attended clinic and are not part of clinic's catchment area (i.e., total number of out of catchment area form submissions that month)
     *
     * @param db
     */
    private void getCHN1_030(SQLiteDatabase db) {
        String query = "select count(*) from ec_child child inner join event e on e.baseEntityId=child.base_entity_id where e.eventType like '%Out of Area Service%' and " + eventDateEqualsCurrentMonthQuery();
    }

    /**
     * Number of total children weighed aged 0-23 months who attended  clinic this month
     * using like for event since this total includes out of area service
     *
     * @param db
     */
    private void getCHN2_005(SQLiteDatabase db) {
        String query = "select count(*) as count," + ageQuery() + " from ec_child child inner join event e on e.baseEntityId=child.base_entity_id " +
                "where e.eventType='%Growth Monitoring%' and age <23 and " + eventDateEqualsCurrentMonthQuery();
    }

    /**
     * Number of total children weighed aged 24-59 months who attended  clinic this month
     *
     * @param db
     */
    private void getCHN2_010(SQLiteDatabase db) {
        String query = "select count(*) as count," + ageQuery() + " from ec_child child inner join event e on e.baseEntityId=child.base_entity_id " +
                "where e.eventType like '%Growth Monitoring%' and age between 24 and 59 and " + eventDateEqualsCurrentMonthQuery();
    }

    /**
     * Number of total children weighed aged < 5 years who attended  clinic this month	"[CHN2-005] + [CHN2-010]
     * [Non-editable in the form]"
     *
     * @param db
     */
    private void getCHN2_015(SQLiteDatabase db) {

    }

    /**
     * Number of children age 0-23 months who where weighed for = 2 consecutive months who did not gain >100g of weight in those months
     * COUNT number of children 0-23 months [Date_Birth] with [weight current visit - weight previous visits < 100g] who had = 2 consecutive weight encounters at this clinic
     *
     * @param db
     */
    private void getCHN2_020(SQLiteDatabase db) {

        String query = "select child.base_entity_id as beid,strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) as currentweightdate,(w.kg*1000) as currentweight," +
                "(select (pw.kg*1000) from weights pw where pw.base_entity_id=w.base_entity_id  and strftime('%Y-%m',datetime(pw.date/1000, 'unixepoch'))=strftime('%Y-%m',date('now'),'-1 months')  limit 1) as prevweight," +
                "(select (pw.kg*1000) from weights pw where pw.base_entity_id=w.base_entity_id  and strftime('%Y-%m',datetime(pw.date/1000, 'unixepoch'))=strftime('%Y-%m',date('now'),'-2 months')  limit 1 ) as last2monthsweight," +
                ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id where strftime('%Y-%m',date('now'))=currentweightdate and age <23 and (currentweight-prevweight>0 and prevweight-last2monthsweight>0) group by beid";

    }

    /**
     * Number of children 24-59 months who where weighed for = 2 consecutive months who did not gain >100g of weight in those months
     * COUNT number of children 24-59 months [Date_Birth]  with [weight current visit - weight previous visits < 100g] who had = 2 consecutive weight encounters at this clinic
     *
     * @param db
     */
    private void getCHN2_025(SQLiteDatabase db) {

        String query = "select child.base_entity_id as beid,strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) as currentweightdate,(w.kg*1000) as currentweight," +
                "(select (pw.kg*1000) from weights pw where pw.base_entity_id=w.base_entity_id  and strftime('%Y-%m',datetime(pw.date/1000, 'unixepoch'))=strftime('%Y-%m',date('now'),'-1 months')  limit 1) as prevweight," +
                "(select (pw.kg*1000) from weights pw where pw.base_entity_id=w.base_entity_id  and strftime('%Y-%m',datetime(pw.date/1000, 'unixepoch'))=strftime('%Y-%m',date('now'),'-2 months')  limit 1 ) as last2monthsweight," +
                ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id where strftime('%Y-%m',date('now'))=currentweightdate and age between 24 and 59 and (currentweight-prevweight>0 and prevweight-last2monthsweight>0) group by beid";

    }

    /**
     * Number of total children age < five years who where weighed for = 2 consecutive months who did not gain >100g of weight in those months
     * "[CHN2-020] + [CHN2-025]
     * [Non-editable in the form]"
     *
     * @param db
     */
    private void getCHN2_030(SQLiteDatabase db) {

    }

    /**
     * Number of total children age 0-23 months whose weight is between -2Z and -3Z scores
     *
     * @param db
     */
    private void getCHN2_035(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age<=23 and w.z_score between -2 and -3 group by child.base_entity_id;";

    }

    /**
     * Number of total children age 24-59 months whose weight is between -2Z and -3Z scores
     *
     * @param db
     */
    private void getCHN2_040(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age between 24 and 59 and w.z_score between -2 and -3 group by child.base_entity_id;";

    }

    /**
     * Number of total children age < 5 years whose weight is between -2Z and -3Z scores
     * "[CHN2-035] + [CHN2-040]
     * [Non-editable in the form]"
     *
     * @param db
     */
    private void getCHN2_041(SQLiteDatabase db) {

    }

    /**
     * Number of total children age 0-23 months whose weight is below -3Z scores
     *
     * @param db
     */
    private void getCHN2_045(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age<=23 and w.z_score< -3 group by child.base_entity_id;";
    }

    /**
     * Number of total children age 24-59 months whose weight is below -3Z scores
     *
     * @param db
     */
    private void getCHN2_050(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age between 24 and 59 and w.z_score < -3 group by child.base_entity_id;";
    }

    /**
     * Number of total children age < 5 years whose weight below -3Z scores
     * [CHN2-045] + [CHN2-050]
     * [Non-editable in the form]
     *
     * @param db
     */
    private void getCHN2_051(SQLiteDatabase db) {

    }

    /**
     * Number of total children age 0-23 months whose weight is above 2Z scores
     *
     * @param db
     */
    private void getCHN2_055(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age<=23 and w.z_score>2 group by child.base_entity_id;";


    }

    /**
     * Number of total children age 24-59 months whose weight is above 2Z scores
     * @param db
     */
    private void getCHN2_060(SQLiteDatabase db) {
        String query="select"+ageQuery() +
                "from weights w left join ec_child child on w.base_entity_id=child.base_entity_id" +
                "where strftime('%Y-%m',date('now'))=strftime('%Y-%m',datetime(w.date/1000, 'unixepoch')) and age between 24 and 59 and w.z_score >2 group by child.base_entity_id;";


    }

    /**
     * Number of total children age < 5 years whose weight is above 2Z scores
     * @param db
     */
    private void getCHN2_061(SQLiteDatabase db) {

    }


    private String ageQuery() {
        return "CAST ((julianday('now') - julianday(strftime('%Y-%m-%d',child.dob)))/(365/12) AS INTEGER)as age";
    }

    private String eventDateEqualsCurrentMonthQuery() {
        return "strftime('%Y-%m',e.eventDate) ='" + dfyymm.format(new Date()) + "'";
    }

}
