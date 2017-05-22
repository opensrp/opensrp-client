package com.example.opensrp_thrivepk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.domain.LoginResponse.SUCCESS;


public class ProviderProfileActivity extends Activity {
    Context ctx = Context.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_profile);

        JSONObject providerdt = null;
        try {
            providerdt = VaccinatorUtils.providerFullDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.detail_heading)).setText("Provider Details");

        findViewById(R.id.refreshProvider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    
                    new AsyncCallWS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((TextView)findViewById(R.id.details_id_label)).setText(getValue(providerdt, "provider_id", false, true));

        ((TextView)findViewById(R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        fillTable(providerdt);

        initializeProgressDialog();

        ImageButton back = (ImageButton)findViewById(org.ei.opensrp.core.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fillTable(JSONObject providerdt){
        Log.v(getClass().getName(), "PROVIDER DETAILS MAP:"+providerdt);

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.report_detail_info_table1);
        dt.removeAllViews();

        addRow(this, dt, "ID", getValue(providerdt, "provider_id", false, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Name", getValue(providerdt, "provider_name", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Team Identifier", getValue(providerdt, "provider_identifier", false, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Team", getValue(providerdt, "provider_team", true, true), Utils.Size.MEDIUM);

        addRow(this, dt, "Province", getValue(providerdt, "provider_province", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "City", getValue(providerdt, "provider_city", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Town", getValue(providerdt, "provider_town", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "UC", getValue(providerdt, "provider_uc", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Center", getValue(providerdt, "provider_location_id", true, true), Utils.Size.MEDIUM);
        try {
            addRow(this, dt, "Roles", providerdt.getJSONObject("user").getJSONArray("roles").toString().replace("\"", "").replace(",", ", "), Utils.Size.MEDIUM);
            addRow(this, dt, "Permissions", providerdt.getJSONObject("user").getJSONArray("permissions").toString().replace("\"", "").replace(",", ", "), Utils.Size.MEDIUM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching provider details");
        progressDialog.setMessage("Connecting to server ... "+getString(org.ei.opensrp.core.R.string.loggin_in_dialog_message));
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, LoginResponse> {

        @Override
        protected LoginResponse doInBackground(Void... params) {
            Log.i(getClass().getName(), "starting doInBackground");
            return ctx.userService().isValidRemoteLogin(ctx.allSharedPreferences().fetchRegisteredANM(), ctx.allSettings().fetchANMPassword());
        }

        @Override
        protected void onPostExecute(LoginResponse r) {
            Log.v(getClass().getName(), r.toString() + r.payload());
            if(isFinishing()) {
                return;
            }

            if (r == SUCCESS) {
                UserService usr = ctx.userService();
                // using user for data saving leads to internal call to another background task
                // which doesnot update data immediately and hence not shown on UI
                // so use settings service directly to save data
                AllSettings aset = ctx.allSettings();

                String loc = usr.getUserLocation(r.payload());
                Log.v(getClass().getName(), "LOCATION: " + loc);
                if (StringUtils.isNotBlank(loc)) aset.saveANMLocation(loc);

                String user = usr.getUserData(r.payload());
                Log.v(getClass().getName(), "USER:" + user);
                if (StringUtils.isNotBlank(user)) aset.saveUserInformation(user);

                try {
                    Utils.writePreference(getBaseContext(), "team", new JSONObject(r.payload()).getJSONObject("team").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    fillTable(VaccinatorUtils.providerFullDetails());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.v(getClass().getName(), "SUCCESS - user data refreshed");

                Toast.makeText(getBaseContext(), "SUCCESS - user data refreshed ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getBaseContext(), "ERROR while fetching provider information remotely; " + (r.payload()==null?"Check your internet connection":r.payload()), Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {

            if(!isFinishing()) {
                progressDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public void onBackPressed() {
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Going back to home...");

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
