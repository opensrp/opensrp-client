package org.ei.opensrp.dgfp.clientDownload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import io.fabric.sdk.android.services.settings.SettingsJsonConstants;
import java.util.ArrayList;
import java.util.HashMap;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.dgfp.BuildConfig;
import org.ei.opensrp.dgfp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientsViewActivity extends Activity {

    private String TAG = ClientsViewActivity.class.getSimpleName();;
    ArrayList<HashMap<String, String>> contactList;
    private ListView listView;
    private String url = null;

    public ClientsViewActivity() {
        this.url = null;
        this.TAG = ClientsViewActivity.class.getSimpleName();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_view);
        this.contactList = new ArrayList();
        this.listView = (ListView) findViewById(R.id.list);
        Bundle bundle = getIntent().getExtras();
        this.url = BuildConfig.FLAVOR;
        Log.d(SettingsJsonConstants.APP_URL_KEY, this.url);
        processJSON(bundle.getString("jsonStr"));
        buildView();
    }

    private void processJSON(String jsonStr) {
        if (jsonStr != null) {
            try {
                JSONArray clients = new JSONObject(jsonStr).getJSONArray("clients");
                for (int i = 0; i < clients.length(); i++) {
                    JSONObject c = clients.getJSONObject(i);
                    String firstName = c.getString("firstName");
                    String nationalId = c.getString("nationalId");
                    String birthId = c.getString("birthId");
                    String clientCaseId = c.getString(AllConstants.CASE_ID);
                    HashMap<String, String> client = new HashMap();
                    client.put("firstName", "Name : " + firstName);
                    client.put("nationalId", "NID : " + nationalId);
                    client.put("birthId", "BRID : " + birthId);
                    client.put("clientCaseId", clientCaseId);
                    Log.d("client data : ", "firstName : " + firstName + " nid : " + nationalId + " brid : " + birthId);
                    this.contactList.add(client);
                }
                return;
            } catch (JSONException e) {
                Log.e(this.TAG, "Json parsing error: " + e.getMessage());
                Toast.makeText(ClientsViewActivity.this.getApplicationContext(),
                        "Json parsing error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.e(this.TAG, "Couldn't get json from server.");
        Toast.makeText(ClientsViewActivity.this.getApplicationContext(),
                "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_SHORT).show();

    }

    private void buildView() {
        this.listView.setAdapter(new SimpleAdapter(this, this.contactList,R.layout.client_item_list, new String[]{"firstName", "nationalId", "birthId"}, new int[]{R.id.firstName, R.id.nationalId, R.id.birthId}));

        this.listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ClientsViewActivity.this.getApplicationContext(), ClientSave.class);
                intent.putExtra("clientCaseId", (String) ((HashMap) ClientsViewActivity.this.contactList.get(position)).get("clientCaseId"));
                startActivity(intent);
            }
        });
    }
}
