package org.ei.opensrp.view.activity;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public abstract class NativeHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private String locationDialogTAG = "locationDialogTAG";
    private PendingFormSubmissionService pendingFormSubmissionService;
    Activity activity=this;
    private Listener<Boolean> onSyncStartListener;
    private Listener<Boolean> onSyncCompleteListener;
    private Listener<String> onFormSubmittedListener;
    private Listener<String> updateANMDetailsListener;

    protected Listener<Boolean> onSyncStartListener(){
        return new Listener<Boolean>() {
            @Override
            public void onEvent(Boolean data) {
                if (updateMenuItem != null) {
                    updateMenuItem.setActionView(R.layout.progress);
                }
            }
        };
    }

    protected Listener<Boolean> onSyncCompleteListener(){
        return new Listener<Boolean>() {
            @Override
            public void onEvent(Boolean data) {
                //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
                updateRemainingFormsToSyncCount();
                if (updateMenuItem != null) {
                    updateMenuItem.setActionView(null);
                }
                updateRegisterCounts();
            }
        };
    }

    protected Listener<String> onFormSubmittedListener(){
        return new Listener<String>() {
            @Override
            public void onEvent(String instanceId) {
                updateRegisterCounts();
            }
        };
    }

    protected Listener<String> updateANMDetailsListener(){
        return new Listener<String>() {
            @Override
            public void onEvent(String data) {
                updateRegisterCounts();
            }
        };
    }

    public abstract int smartRegistersHomeLayout();

    @Override
    protected void onCreation() {
        Log.i(getClass().getName(), "Creating Home Activity Views:");

        setContentView(smartRegistersHomeLayout());
        setupViewsAndListeners();
        initialize();
    }

    public abstract void setupViewsAndListeners();

    protected void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        onSyncStartListener = onSyncStartListener();
        onSyncCompleteListener = onSyncCompleteListener();
        onFormSubmittedListener = onFormSubmittedListener();
        updateANMDetailsListener = updateANMDetailsListener();

        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.logo_header));
        getSupportActionBar().setLogo(R.drawable.logo_header);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResumption() {
        Log.i(getClass().getName(), "Setting up views and listeners");

        setupViewsAndListeners();

        Log.i(getClass().getName(), "Updating Counts");

        updateRegisterCounts();

        Log.i(getClass().getName(), "Updating Sync Indicator");

        updateSyncIndicator();

        Log.i(getClass().getName(), "Updating Remaining Forms To Sync Count");

        updateRemainingFormsToSyncCount();

        Log.i(getClass().getName(), "Fully resumed Home ");
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(final HomeContext anmDetails) {
                Handler mainHandler = new Handler(getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateRegisterCounts(anmDetails);
                    }
                };
                mainHandler.post(myRunnable);
            }
        });
    }

    protected abstract void updateRegisterCounts(HomeContext homeContext);

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.i(getClass().getName(), "Updating menu items");

        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);
        remainingFormsToSyncMenuItem.setVisible(true);

        remainingFormsToSyncMenuItem.setTitle("Loading counts ...");

        updateSyncIndicator();

        updateRemainingFormsToSyncCount();

        Log.i(getClass().getName(), "Updated menu items");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.updateMenuItem) {
            updateFromServer();
            return true;
        }
        else if(i == R.id.switchLanguageMenuItem){
            //todo
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context.actionService(), context.formSubmissionSyncService(),
                new SyncProgressIndicator(), context.allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    protected void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context.allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    protected void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null || pendingFormSubmissionService == null) {
            return;
        }
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                long size = pendingFormSubmissionService.pendingFormSubmissionCount();
                if (size > 0) {
                    remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
                } else {
                    remainingFormsToSyncMenuItem.setTitle("0 " + getString(R.string.unsynced_forms_count_message));
                }
            }
        });
    }

    protected Register initRegister(String permission, int containerId, int registerButtonId, View.OnClickListener registerClickListener,
                RegisterCountView[] registerCountViews) {
        boolean allowRegister = false;
        // if any permission specified user must have that otherwise ignore permission
        if(StringUtils.isNotBlank(permission)) {
            try {
                if(Utils.providerDetails().has("permissions")
                        && Utils.providerDetails().getJSONArray("permissions").toString().toLowerCase().contains(permission.toLowerCase()) ) {
                    allowRegister = true;
                }
            } catch (JSONException e) {
               throw new RuntimeException(e);
            }
        }
        else {
            allowRegister = true;
        }

        Register reg = null;
        if (allowRegister){
            View container = findViewById(containerId);
            container.setVisibility(View.VISIBLE);
            ImageButton registerButton = (ImageButton)findViewById(registerButtonId);
            if(registerClickListener !=  null){
                registerButton.setOnClickListener(registerClickListener);
            }

            Map<Integer, RegisterCountView> countViews = new HashMap<>();
            for (RegisterCountView rcv: registerCountViews) {
                countViews.put(rcv.getViewId(), rcv);
            }
            reg = new Register(true, permission, container, registerButton, countViews);
        }
        else {
            View container = findViewById(containerId);
            container.setVisibility(View.GONE);

            reg = new Register(false, permission, container, null, null);
        }

        return reg;
    }
    
    public enum CountMethod {
        AUTO, MANUAL, NONE
    }
    
    public class RegisterCountView {
        private final CountMethod countMethod;
        private final String filter;
        private final int viewId;
        private String countQuery;
        private String postFix;
        private String table;
        private int currentCount;

        public RegisterCountView(int viewId, String table, String filter, String postFix, CountMethod countMethod) {
            this.viewId = viewId;
            this.table = table;
            this.filter = filter;
            this.postFix = postFix;
            this.countMethod = countMethod;
        }

        public CountMethod getCountMethod() {
            return countMethod;
        }

        public String getTable() {
            return table;
        }

        public int getViewId() {
            return viewId;
        }

        public String getCountQuery() {
            return countQuery;
        }

        public String getPostFix() {
            return postFix;
        }

        public void setPostFix(String postFix) {
            this.postFix = postFix;
        }

        public void setCountTable(String table) {
            this.table = table;
            this.countQuery = "SELECT COUNT(*) c FROM "+table;
        }


        public int getCurrentCount() {
            return currentCount;
        }

        public void setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
        }
    }

    public class Register {
        private final boolean allowed;
        private final String permission;
        private final View container;
        private final ImageButton registerButton;
        private final Map<Integer, RegisterCountView> countViews;

        Register(boolean allowed, String permission, View container, ImageButton registerButton, Map<Integer, RegisterCountView> countViews){
            this.allowed = allowed;
            this.permission = permission;
            this.container = container;
            this.registerButton = registerButton;
            this.countViews = countViews;
        }

        public void resetRegisterCounts(){
            for (RegisterCountView r: countViews.values()) {
                if (r.getCountMethod().equals(CountMethod.AUTO)){
                    String count = context.commonrepository(r.table).rawQuery(
                            "SELECT COUNT(1) c FROM "+r.getTable()
                                    +(StringUtils.isNotBlank(r.filter)?(" WHERE "+r.filter):"")
                            ).get(0).get("c");
                    r.setCurrentCount(Integer.parseInt(count));
                    ((TextView)findViewById(r.getViewId())).setText(count+(StringUtils.isNotBlank(r.postFix)?(" "+r.postFix):""));
                }
            }
        }

        public void overrideRegisterCount(int viewId, int count, String postFix){
            countViews.get(viewId).setCurrentCount(count);
            ((TextView)findViewById(viewId)).setText(count+(StringUtils.isNotBlank(postFix)?(" "+postFix):""));
        }

        public String getPermission() {
            return permission;
        }

        public View getContainer() {
            return container;
        }

        public ImageButton getRegisterButton() {
            return registerButton;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public Map<Integer, RegisterCountView> getCountViews() {
            return countViews;
        }

        public RegisterCountView getCountView(int viewId) {
            return countViews.get(viewId);
        }
    }
}
