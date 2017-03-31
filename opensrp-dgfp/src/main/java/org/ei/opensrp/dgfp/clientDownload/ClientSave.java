package org.ei.opensrp.dgfp.clientDownload;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.MessageFormat;
import java.util.List;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.convertor.FormSubmissionConvertor;
import org.ei.opensrp.dgfp.BuildConfig;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.FormSubmissionService;
import org.ei.opensrp.service.FormSubmissionSyncService;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.view.activity.SecuredActivity;

public class ClientSave extends SecuredActivity {
    private TextView allForm;
    private String clientCaseId;
    private FormSubmissionService formSubmissionService;

    private class ClientDownLoadService extends AsyncTask<String, Integer, String> {
        Context activitycontext;
        private AllSettings allSettings;
        private AllSharedPreferences allSharedPreferences;
        private TextView brid;
        private DristhiConfiguration configuration;
        public org.ei.opensrp.Context context;
        private TextView firstName;
        private HTTPAgent httpAgent;
        private TextView lastName;
        private TextView nid;
        ProgressDialog progress;
        FetchStatus returnMessage;

        /* renamed from: org.ei.opensrp.dgfp.clientDownload.ClientSave.ClientDownLoadService.1 */
        class C09321 extends TypeToken<List<FormSubmissionDTO>> {
            C09321() {
            }
        }

        /* renamed from: org.ei.opensrp.dgfp.clientDownload.ClientSave.ClientDownLoadService.2 */
        class C09332 extends TypeToken<List<FormSubmissionDTO>> {
            C09332() {
            }
        }

        public ClientDownLoadService(Context activitycontext, org.ei.opensrp.Context context, HTTPAgent httpAgent, DristhiConfiguration configuration, AllSettings allSettings, AllSharedPreferences allSharedPreferences) {
            this.activitycontext = activitycontext;
            this.httpAgent = httpAgent;
            this.configuration = configuration;
            this.allSettings = allSettings;
            this.allSharedPreferences = allSharedPreferences;
            this.context = context;
            ClientSave.this.formSubmissionService = org.ei.opensrp.Context.getInstance().formSubmissionService();
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
            Log.d("response", this.returnMessage + BuildConfig.FLAVOR);
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.progress.dismiss();
            ClientSave.this.allForm.setText(this.returnMessage + BuildConfig.FLAVOR);
            Log.d("client_form_submission", this.returnMessage + BuildConfig.FLAVOR);
        }

        public FetchStatus pullFromServer() {
            FetchStatus dataStatus = FetchStatus.nothingFetched;
            String anmId = this.allSharedPreferences.fetchRegisteredANM();
            int downloadBatchSize = this.configuration.syncDownloadBatchSize();
            String baseURL = this.configuration.dristhiBaseURL();
            while (true) {
                Response<String> response = this.httpAgent.fetch(MessageFormat.format("{0}/{1}?anm-id={2}&timestamp={3}&batch-size={4}", new Object[]{baseURL, FormSubmissionSyncService.FORM_SUBMISSIONS_PATH, anmId, this.allSettings.fetchPreviousFormSyncIndex(), Integer.valueOf(downloadBatchSize)}));
                if (response.isFailure()) {
                    org.ei.opensrp.util.Log.logError(MessageFormat.format("Form submissions pull failed.", new Object[0]));
                    return FetchStatus.fetchedFailed;
                }
                List<FormSubmissionDTO> formSubmissions = (List) new Gson().fromJson((String) response.payload(), new C09321().getType());
                if (formSubmissions.isEmpty()) {
                    return dataStatus;
                }
                ClientSave.this.formSubmissionService.processSubmissions(FormSubmissionConvertor.toDomain(formSubmissions));
                dataStatus = FetchStatus.fetched;
            }
        }

        public FetchStatus pullClientFromServer() {
            FetchStatus dataStatus = FetchStatus.nothingFetched;
            String anmId = this.allSharedPreferences.fetchRegisteredANM();
            String baseURL = this.configuration.dristhiBaseURL();
            String uri = MessageFormat.format("{0}/form-submissions/{1}", new Object[]{baseURL, ClientSave.this.clientCaseId});
            Log.v("pull-uri", uri);
            Response<String> response = this.httpAgent.fetch(uri);
            if (response.isFailure()) {
                org.ei.opensrp.util.Log.logError(MessageFormat.format("Client DownLoad failed.", new Object[0]));
                return FetchStatus.fetchedFailed;
            }
            List<FormSubmissionDTO> formSubmissions = (List) new Gson().fromJson((String) response.payload(), new C09332().getType());
            if (formSubmissions.isEmpty()) {
                return dataStatus;
            }
            ClientSave.this.formSubmissionService.processSubmissions(FormSubmissionConvertor.toDomain(formSubmissions));
            return FetchStatus.fetched;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.client_history);
        this.allForm = (TextView) findViewById(R.id.allform);
        this.clientCaseId = getIntent().getExtras().getString("clientCaseId");
        new ClientDownLoadService(this, this.context, new HTTPAgent(this.context.applicationContext(), this.context.allSettings(), this.context.allSharedPreferences(), this.context.configuration()), this.context.configuration(), this.context.allSettings(), this.context.allSharedPreferences()).execute(new String[0]);
    }

    protected void onCreation() {
    }

    protected void onResumption() {
    }
}
