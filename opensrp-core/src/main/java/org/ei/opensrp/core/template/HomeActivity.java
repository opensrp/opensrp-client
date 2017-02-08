package org.ei.opensrp.core.template;

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
import org.ei.opensrp.core.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.json.JSONException;
import org.ei.opensrp.core.db.handler.RegisterCountLoaderHandler;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public abstract class HomeActivity extends SecuredActivity {
    private Map<Integer, Register> registers = new HashMap<>();
    private Activity activity = this;

    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private Listener<Boolean> onSyncStartListener;
    private Listener<Boolean> onSyncCompleteListener;
    private Listener<String> onFormSubmittedListener;
    private RegisterCountLoaderHandler countLoaderHandler;
    // NOT NEEDED private Listener<String> updateANMDetailsListener;

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

    public abstract int smartRegistersHomeLayout();

    @Override
    protected void onCreation() {
        Log.i(getClass().getName(), "Creating Home Activity Views:");

        setContentView(smartRegistersHomeLayout());
        setupViewsAndListeners();
        initialize();
    }

    public abstract void setupViewsAndListeners();

    protected abstract Integer getHeaderLogo();
    protected abstract Integer getHeaderIcon();

    protected void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        onSyncStartListener = onSyncStartListener();
        onSyncCompleteListener = onSyncCompleteListener();
        onFormSubmittedListener = onFormSubmittedListener();

        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);

        getSupportActionBar().setTitle("OpenSRP Immunization");
        if(getHeaderLogo() != null)
            getSupportActionBar().setIcon(getHeaderIcon());
        if(getHeaderLogo() != null)
            getSupportActionBar().setLogo(getHeaderLogo());

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
        for(Register r: registers.values()){
            if (r.isAllowed() && r.getCountViews() != null && r.getCountViews().size() >0) {
                for (RegisterCountView rv : r.getCountViews().values()) {
                    rv.reExecuteCount();
                }
            }
        }
    }

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

    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
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

    protected Register setupRegister(String authority, int containerId, int registerButtonId, View.OnClickListener registerClickListener,
                RegisterCountView[] registerCountViews) {
        boolean allowRegister = false;
        // if any permission or role specified user must have that otherwise ignore permission or role
        if(StringUtils.isNotBlank(authority)) {
            try {
                if((Utils.providerDetails().has("permissions")
                            && Utils.providerDetails().getJSONArray("permissions").toString().toLowerCase().contains(authority.toLowerCase()))
                        ||(Utils.providerDetails().has("roles")
                            && Utils.providerDetails().getJSONArray("roles").toString().toLowerCase().contains(authority.toLowerCase()))) {
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
            reg = new Register(true, authority, containerId, registerButton, countViews);
        }
        else {
            View container = findViewById(containerId);
            container.setVisibility(View.GONE);

            reg = new Register(false, authority, containerId, null, null);
        }

        registers.put(containerId, reg);
        return reg;
    }
    
    public enum CountMethod {
        AUTO, MANUAL, NONE
    }

    public interface CustomCounterHandler{
        int executeCounter();
    }

    public class RegisterCountView {
        private final CountMethod countMethod;
        private final String filter;
        private final int viewId;
        private final CustomCounterHandler customCounterHandler;
        private String postFix;
        private String table;
        private int currentCount;

        public RegisterCountView(int viewId, final String table, String filter, String postFix, CountMethod countMethod, CustomCounterHandler customCounterHandler) {
            this.viewId = viewId;
            this.table = table;
            this.filter = filter;
            this.postFix = postFix;
            this.countMethod = countMethod;
            if (countMethod.equals(CountMethod.AUTO)) {
                customCounterHandler = new CustomCounterHandler() {
                    @Override
                    public int executeCounter() {
                        return (int) Context.getInstance().commonrepository(table).count();
                    }
                };
            }
            this.customCounterHandler = customCounterHandler;
        }

        public RegisterCountView(int viewId, String table, String filter, String postFix) {
            this(viewId, table, filter, postFix, CountMethod.AUTO, null);
        }

        public TextView getContainerView(){
            return (TextView) findViewById(viewId);
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

        public String getPostFix() {
            return postFix;
        }

        public void reExecuteCount(){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (countMethod.equals(CountMethod.AUTO)){
                        getContainerView().setText("0000");
                        int c = customCounterHandler.executeCounter();
                        overrideCount(c);
                    }
                    else if (countMethod.equals(CountMethod.MANUAL)){
                        getContainerView().setText("0000");
                        int c = customCounterHandler.executeCounter();
                        overrideCount(c);
                    }
                }
            });
        }

        public void overrideCount(int count){
            currentCount = count;
            ((TextView)findViewById(viewId)).setText(count+(StringUtils.isNotBlank(postFix)?(" "+postFix):""));
        }

        public int getCurrentCount() {
            return currentCount;
        }
    }

    public class Register {
        private final boolean allowed;
        private final String authority;
        private final int container;
        private final ImageButton registerButton;
        private final Map<Integer, RegisterCountView> countViews;

        Register(boolean allowed, String authority, int container, ImageButton registerButton, Map<Integer, RegisterCountView> countViews){
            this.allowed = allowed;
            this.authority = authority;
            this.container = container;
            this.registerButton = registerButton;
            this.countViews = countViews;
        }

        public String getAuthority() {
            return authority;
        }

        public int getContainer() {
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
