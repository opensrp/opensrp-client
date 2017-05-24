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
     * Number of total children < 5 who attended a clinic this month 	"[CHN1-011] + [CHN1-021]
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
        String query = "select count(*) from ec_child child inner join event e on e.baseEntityId=child.base_entity_id where e.eventType='Out of Catchment Service' where " + eventDateEqualsCurrentMonthQuery();
    }

    private String ageQuery() {
        return "CAST ((julianday('now') - julianday(strftime('%Y-%m-%d',child.dob)))/(365/12) AS INTEGER)as age";
    }

    private String eventDateEqualsCurrentMonthQuery() {
        return "strftime('%Y-%m',e.eventDate) ='" + dfyymm.format(new Date()) + "'";
    }

}
