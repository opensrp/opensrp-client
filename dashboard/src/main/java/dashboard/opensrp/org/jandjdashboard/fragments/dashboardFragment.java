package dashboard.opensrp.org.jandjdashboard.fragments;

import android.support.v4.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 4/18/18.
 */

public abstract class dashboardFragment extends Fragment {

    public abstract void refresh(String from, String to);
    public boolean samedate(Date date1, Date date2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
