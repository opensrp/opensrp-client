package org.ei.opensrp.path.service.intent;

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

    public static void generateIndicators(final SQLiteDatabase database, int month) {

    }

    /**
     * Number of male children aged < 12 months who attended a clinic this month.
     *
     * @param database
     */
    private void getCHN1_005(SQLiteDatabase database, String gender) {
        String query = "select count(*) from " + EC_CHILD_TABLE + " child inner join " + PathRepository.Table.event.name() + " e on e." + PathRepository.event_column.baseEntityId.name() + "= child.id" +
                " where strftime('%Y-%m',e." + PathRepository.event_column.eventDate.name() + ")=" + dfyymm.format(new Date()) + " and date(child.dob,'+12 months')<'now()' and child.gender=" + (gender.isEmpty() ? "Male" : gender);
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
    private void getCHN1_015() {

    }

}
