package org.ei.opensrp.path.fragment;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.DetailActivity;
import org.ei.opensrp.path.activity.FieldMonitorDailyDetailActivity;
import org.ei.opensrp.path.activity.FieldMonitorMonthlyDetailActivity;
import org.ei.opensrp.path.activity.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.path.option.DateSort;
import org.ei.opensrp.path.provider.FieldMonitorSmartClientsProvider;
import org.ei.opensrp.path.servicemode.StockDailyServiceModeOption;
import org.ei.opensrp.path.servicemode.StockMonthlyServiceModeOption;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.VaccinatorUtils;

import static android.view.View.INVISIBLE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Safwan on 2/15/2016.
 */
public class FieldMonitorRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            // FIXME path_conflict
            //@Override
            public FilterOption searchFilterOption() {
                return null;
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new StockDailyServiceModeOption(null);
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new DateSort("Reporting Period", "date DESC");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.stock_register_title);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        new StockDailyServiceModeOption(null),
                        new StockMonthlyServiceModeOption(null)
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new DateSort("Reporting Period", "date DESC")};
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.str_field_search_hint);
            }
        };
    }

    @Override
    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        super.onServiceModeSelection(serviceModeOption, view);
        add2ndColumnHeaderView();
    }

    protected void add2ndColumnHeaderView() {
        LinearLayout cl = (LinearLayout) mView.findViewById(org.ei.opensrp.R.id.clients_upper_header_layout);
        if (isDailyRegister()) {
            cl.getLayoutParams().height = 50;
            cl.setVisibility(View.VISIBLE);
            cl.removeAllViewsInLayout();

            cl.addView(addHeaderItem("", 2));
            cl.addView(addHeaderItem("Vaccine Stock Used or Consumed over the Period", 7));
            cl.addView(addHeaderItem("TOTAL", 2));
        } else {
            cl.setVisibility(View.GONE);
            cl.removeAllViewsInLayout();
        }
    }

    private CustomFontTextView addHeaderItem(String text, int weight) {
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, org.ei.opensrp.R.style.CustomFontTextViewStyle_ListView_Medium);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(3, 0, 0, 2);
        header.setLayoutParams(lp);
        header.setGravity(TEXT_ALIGNMENT_CENTER);
        header.setText(text);
        header.setPadding(weight * 3, 0, 0, 4);
        header.setTextSize(20);
        header.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        header.setBackgroundColor(Color.LTGRAY);

        return header;
    }

    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

        mView.findViewById(org.ei.opensrp.R.id.filter_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sort_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sorted_by).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.label_sorted_by).setVisibility(View.GONE);
    }

    @Override
    protected void startRegistration() {
        HashMap<String, String> overrides = new HashMap<>();
        overrides.putAll(VaccinatorUtils.providerDetails());
        FieldOverrides fieldOverrides = new FieldOverrides(overrides.toString());
        ((FieldMonitorSmartRegisterActivity) getActivity()).startFormActivity("vaccine_stock_position", null, fieldOverrides.toString());
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();

        add2ndColumnHeaderView();
    }//end of method

    private List<String> customColumnsDaily = new ArrayList<String>() {{
        add("(select count(*) c from ec_mother where tt1 between t.date and t.date) tt1");
        add("(select count(*) c from ec_mother where tt2 between t.date and t.date) tt2");
        add("(select count(*) c from ec_mother where tt3 between t.date and t.date) tt3");
        add("(select count(*) c from ec_mother where tt4 between t.date and t.date) tt4");
        add("(select count(*) c from ec_mother where tt5 between t.date and t.date) tt5");
        add("(select count(*) c from ec_child where bcg between t.date and t.date) bcg");
        add("(select count(*) c from ec_child where opv0 between t.date and t.date) opv0");
        add("(select count(*) c from ec_child where opv1 between t.date and t.date) opv1");
        add("(select count(*) c from ec_child where opv2 between t.date and t.date) opv2");
        add("(select count(*) c from ec_child where opv3 between t.date and t.date) opv3");
        add("(select count(*) c from ec_child where ipv between t.date and t.date) ipv");
        add("(select count(*) c from ec_child where pcv1 between t.date and t.date) pcv1");
        add("(select count(*) c from ec_child where pcv2 between t.date and t.date) pcv2");
        add("(select count(*) c from ec_child where pcv3 between t.date and t.date) pcv3");
        add("(select count(*) c from ec_child where measles1 between t.date and t.date) measles1");
        add("(select count(*) c from ec_child where measles2 between t.date and t.date) measles2");
        add("(select count(*) c from ec_child where penta1 between t.date and t.date) penta1");
        add("(select count(*) c from ec_child where penta2 between t.date and t.date) penta2");
        add("(select count(*) c from ec_child where penta3 between t.date and t.date) penta3");
    }};

    private List<String> customColumnsMonthly = new ArrayList<String>() {{
        add("(select count(*) c from ec_mother where SUBSTR(tt1,1,7) = SUBSTR(t.date,1,7)) tt1");
        add("(select count(*) c from ec_mother where SUBSTR(tt2,1,7) = SUBSTR(t.date,1,7)) tt2");
        add("(select count(*) c from ec_mother where SUBSTR(tt3,1,7) = SUBSTR(t.date,1,7)) tt3");
        add("(select count(*) c from ec_mother where SUBSTR(tt4,1,7) = SUBSTR(t.date,1,7)) tt4");
        add("(select count(*) c from ec_mother where SUBSTR(tt5,1,7) = SUBSTR(t.date,1,7)) tt5");
        add("(select count(*) c from ec_child where SUBSTR(bcg,1,7) = SUBSTR(t.date,1,7)) bcg");
        add("(select count(*) c from ec_child where SUBSTR(opv0,1,7) = SUBSTR(t.date,1,7)) opv0");
        add("(select count(*) c from ec_child where SUBSTR(opv1,1,7) = SUBSTR(t.date,1,7)) opv1");
        add("(select count(*) c from ec_child where SUBSTR(opv2,1,7) = SUBSTR(t.date,1,7)) opv2");
        add("(select count(*) c from ec_child where SUBSTR(opv3,1,7) = SUBSTR(t.date,1,7)) opv3");
        add("(select count(*) c from ec_child where SUBSTR(ipv,1,7) = SUBSTR(t.date,1,7)) ipv");
        add("(select count(*) c from ec_child where SUBSTR(pcv1,1,7) = SUBSTR(t.date,1,7)) pcv1");
        add("(select count(*) c from ec_child where SUBSTR(pcv2,1,7) = SUBSTR(t.date,1,7)) pcv2");
        add("(select count(*) c from ec_child where SUBSTR(pcv3,1,7) = SUBSTR(t.date,1,7)) pcv3");
        add("(select count(*) c from ec_child where SUBSTR(measles1,1,7) = SUBSTR(t.date,1,7)) measles1");
        add("(select count(*) c from ec_child where SUBSTR(measles2,1,7) = SUBSTR(t.date,1,7)) measles2");
        add("(select count(*) c from ec_child where SUBSTR(penta1,1,7) = SUBSTR(t.date,1,7)) penta1");
        add("(select count(*) c from ec_child where SUBSTR(penta2,1,7) = SUBSTR(t.date,1,7)) penta2");
        add("(select count(*) c from ec_child where SUBSTR(penta3,1,7) = SUBSTR(t.date,1,7)) penta3");
        add("(select sum(total_wasted) c from stock where report='daily' and SUBSTR(date,1,7)=SUBSTR(t.date,1,7)) total_monthly_wasted");
    }};

    private boolean isDailyRegister() {
        return getCurrentServiceModeOption() == null || getCurrentServiceModeOption().name().toLowerCase().contains("dail");
    }

    @Override
    protected void onCreation() {
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
    }

    public void initializeQueries() {
        String tableName = "stock";

        FieldMonitorSmartClientsProvider hhscp = new FieldMonitorSmartClientsProvider(getActivity(),
                clientActionHandler, context().alertService(),
                isDailyRegister() ? FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay : FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth);
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, Context.getInstance().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        mainCondition = isDailyRegister() ? "report='daily'" : "report='monthly'";
        countSelect = countqueryBUilder.mainCondition(mainCondition);
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{"relationalid", "details", "total_wasted", "total_received", "total_balanceInHand", "total_used", "date", "report", "provider_uc", "provider_town", "provider_id", "provider_location_id", "Target_assigned_for_vaccination_at_each_month", "bcg_balance_in_hand", "bcg_received", "bcg_wasted", "opv_balance_in_hand", "opv_received", "opv_wasted", "ipv_balance_in_hand", "ipv_received", "ipv_wasted", "pcv_balance_in_hand", "pcv_received", "pcv_wasted", "penta_balance_in_hand", "penta_received", "penta_wasted", "measles_balance_in_hand", "measles_received", "measles_wasted", "tt_balance_in_hand", "tt_received", "tt_wasted", "dilutants_balance_in_hand", "dilutants_received", "dilutants_wasted", "syringes_balance_in_hand", "syringes_received", "syringes_wasted", "safety_boxes_balance_in_hand", "safety_boxes_received", "safety_boxes_wasted"});
        mainSelect = queryBUilder.mainCondition(mainCondition);
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        updateSearchView();
        refresh();
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.stock_detail_holder:
                    if (isDailyRegister()) {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorDailyDetailActivity.class);
                    } else {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorMonthlyDetailActivity.class);
                    }

                    getActivity().finish();

                    break;
            }
        }
    }

    public void updateSearchView() {
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                if (cs.toString().equalsIgnoreCase("")) {
                    filters = "";
                } else {
                    filters = cs.toString();
                }
                joinTable = "";
                mainCondition = isDailyRegister() ? "report='daily'" : "report='monthly'";
                ;
                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
                filterandSortExecute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
