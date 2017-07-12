package org.ei.opensrp.path.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.R;
import org.ei.opensrp.view.activity.DrishtiApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import util.VaccinatorUtils;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;

public class ProviderProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }

        setContentView(R.layout.provider_profile);

        HashMap<String, String> providerdt = VaccinatorUtils.providerDetails();

        ((TextView)findViewById(R.id.detail_heading)).setText("Provider Details");

        String programId = nonEmptyValue(providerdt, true, false, "provider_id");
        ((TextView)findViewById(R.id.details_id_label)).setText(programId);

        ((TextView)findViewById(R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.report_detail_info_table1);

        dt.addView(getDataRow(this, "ID", programId, null));
        dt.addView(getDataRow(this, "Name", getValue(providerdt, "provider_name", true), null));
        dt.addView(getDataRow(this, "Team Identifier", getValue(providerdt, "provider_identifier", false), null));
        dt.addView(getDataRow(this, "Team", getValue(providerdt, "provider_team", true), null));

        dt.addView(getDataRow(this, "Province", getValue(providerdt, "provider_province", true), null));
        dt.addView(getDataRow(this, "City", getValue(providerdt, "provider_city", true), null));
        dt.addView(getDataRow(this, "Town", getValue(providerdt, "provider_town", true), null));
        dt.addView(getDataRow(this, "UC", getValue(providerdt, "provider_uc", true), null));
        dt.addView(getDataRow(this, "Center", getValue(providerdt, "provider_location_id", true), null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }

    }

    protected Context context() {
        return Context.getInstance().updateApplicationContext(this.getApplicationContext());
    }
}
