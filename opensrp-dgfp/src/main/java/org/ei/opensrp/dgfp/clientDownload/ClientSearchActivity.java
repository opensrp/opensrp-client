package org.ei.opensrp.dgfp.clientDownload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.text.MessageFormat;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.dgfp.BuildConfig;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.view.activity.SecuredActivity;

public class ClientSearchActivity extends SecuredActivity {
    private TextView brid;
    private TextView firstName;
    private TextView lastName;
    private TextView nid;
    private Button submit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.client_search);
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        brid = (TextView) findViewById(R.id.brid);
        nid = (TextView) findViewById(R.id.nid);

        submit = (Button) findViewById(R.id.submit);


        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientDownLoadService clientDownLoadService =
                        new ClientDownLoadService(ClientSearchActivity.this, ClientSearchActivity.this.context,
                                new HTTPAgent(ClientSearchActivity.this.context.applicationContext(),
                                        ClientSearchActivity.this.context.allSettings(),
                                        ClientSearchActivity.this.context.allSharedPreferences(),
                                        ClientSearchActivity.this.context.configuration()),
                                ClientSearchActivity.this.context.configuration(), ClientSearchActivity.this.context.allSettings(),
                                ClientSearchActivity.this.context.allSharedPreferences());

                clientDownLoadService.setTextData(ClientSearchActivity.this.firstName, ClientSearchActivity.this.lastName, ClientSearchActivity.this.brid, ClientSearchActivity.this.nid);
                clientDownLoadService.execute(new String[0]);
            }
        });
    }

    protected void onCreation() {
    }

    protected void onResumption() {
    }

    private class ClientDownLoadService extends AsyncTask<String, Integer, String> {
        private Context activitycontext;
        private AllSettings allSettings;
        private AllSharedPreferences allSharedPreferences;
        private TextView brid;
        private DristhiConfiguration configuration;
        public org.ei.opensrp.Context context;
        private TextView firstName;
        private HTTPAgent httpAgent;
        private TextView lastName;
        private TextView nid;
        private ProgressDialog progress;
        private String returnMessage;

        public ClientDownLoadService(Context activitycontext, org.ei.opensrp.Context context, HTTPAgent httpAgent, DristhiConfiguration configuration, AllSettings allSettings, AllSharedPreferences allSharedPreferences) {
            this.activitycontext = activitycontext;
            this.httpAgent = httpAgent;
            this.configuration = configuration;
            this.allSettings = allSettings;
            this.allSharedPreferences = allSharedPreferences;
            this.context = context;
        }

        public void setTextData(TextView firstName, TextView lastName, TextView brid, TextView nid) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.brid = brid;
            this.nid = nid;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progress = ProgressDialog.show(this.activitycontext, "please wait", "Getting Client");
        }

        protected String doInBackground(String... params) {
            this.returnMessage = pullClientFromServer();
            Log.d("response", this.returnMessage);
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.progress.dismiss();
            Intent intent = new Intent(ClientSearchActivity.this.getApplicationContext(), ClientsViewActivity.class);
            intent.putExtra("jsonStr", this.returnMessage);
            Log.d("jsonStr", this.returnMessage);
            ClientSearchActivity.this.startActivity(intent);
        }

        public String pullClientFromServer() {
            FetchStatus dataStatus = FetchStatus.nothingFetched;
            String anmId = this.allSharedPreferences.fetchRegisteredANM();
            String baseURL = this.configuration.dristhiBaseURL();
            String uri = MessageFormat.format("{0}/search?firstName={1}&nid={2}", new Object[]{baseURL, this.firstName.getText() + BuildConfig.FLAVOR, this.nid.getText() + BuildConfig.FLAVOR});
            Log.v("pull-uri", uri);
            Response<String> response = this.httpAgent.fetch(uri);
            if (!response.isFailure()) {
                return (String) response.payload();
            }
            org.ei.opensrp.util.Log.logError(MessageFormat.format("Client DownLoad failed.", new Object[0]));
            return "failed";
        }
    }
}
