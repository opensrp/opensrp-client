package com.example.opensrp_stock.field;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.opensrp_stock.R;
import com.example.opensrp_stock.field.util.common.ReportFilterOption;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.handler.RegisterDataCursorLoaderHandler;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
import org.ei.opensrp.core.db.utils.RegisterQuery;
import org.ei.opensrp.core.template.CommonSortingOption;
import org.ei.opensrp.core.template.DefaultOptionsProvider;
import org.ei.opensrp.core.template.FilterOption;
import org.ei.opensrp.core.template.NavBarOptionsProvider;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.template.SearchType;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.template.SortingOption;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.DialogOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by Safwan on 2/15/2016.
 */
public class FieldMonitorRegisterFragment extends RegisterDataGridFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private RegisterDataCursorLoaderHandler loaderHandler;

    public FieldMonitorRegisterFragment(){
        super(null);
    }

    @SuppressLint("ValidFragment")
    public FieldMonitorRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    public String bindType() {
        return "stock";
    }

    public void setNavBar(){
        View view= getView();
        LinearLayout navBar= (LinearLayout) view.findViewById(R.id.register_nav_bar_container);
        navBar.removeAllViews();
        View C=getLayoutInflater(null).inflate(getResources().getLayout(R.layout.stock_navbar),null);
        navBar.addView(C);
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public SearchFilterOption searchFilterOption() {
                return new ReportFilterOption("");
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new StockMonthlyServiceModeOption(null);
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortingOption sortOption(){ return new CommonSortingOption("Reporting Period", "date DESC"); }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.stock_register_title);
            }

            @Override
            public SearchType searchType() {
                return SearchType.PASSIVE;
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        //new StockDailyServiceModeOption(null),
                        new StockMonthlyServiceModeOption(null)
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonSortingOption("Reporting Period", "date DESC")};
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
        LinearLayout cl = (LinearLayout) mView.findViewById(org.ei.opensrp.core.R.id.clients_upper_header_layout);
        if (isDailyRegister()){
            cl.getLayoutParams().height = 50;
            cl.setVisibility(View.VISIBLE);
            cl.removeAllViewsInLayout();

            cl.addView(addHeaderItem("", 2));
            cl.addView(addHeaderItem("Vaccine Stock Used or Consumed over the Period", 7));
            cl.addView(addHeaderItem("TOTAL", 2));
        }
        else {
            cl.setVisibility(View.GONE);
            cl.removeAllViewsInLayout();
        }
    }

    private CustomFontTextView addHeaderItem(String text, int weight){
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, org.ei.opensrp.core.R.style.CustomFontTextViewStyle_ListView_Medium);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(3,0,0,2);
        header.setLayoutParams(lp);
        header.setGravity(TEXT_ALIGNMENT_CENTER);
        header.setText(text);
        header.setPadding(weight*3,0,0,4);
        header.setTextSize(20);
        header.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        header.setBackgroundColor(Color.LTGRAY);

        return header;
    }

    protected RegisterClientsProvider clientsProvider() {
        return new FieldMonitorSmartClientsProvider(
                getActivity().getApplicationContext(), clientActionHandler, context.alertService(), FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth);
    }

    @Override
    protected void onInitialization() {

        setNavBar();
        mView.findViewById(org.ei.opensrp.core.R.id.filter_selection).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.label_village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.sort_selection).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.sorted_by).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.label_sorted_by).setVisibility(View.GONE);
    }

    @Override
    protected void startRegistration() {
        HashMap<String, String> overrides = new HashMap<>();
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }


    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "vaccine_stock_position";
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();

        add2ndColumnHeaderView();
    }

    private List<String> customColumnsMonthly = new ArrayList<String>(){{
        add("(select count(id) c from pkwoman where SUBSTR(tt1,1,7) = SUBSTR(stock.date,1,7)) tt1");
        add("(select count(id) c from pkwoman where SUBSTR(tt2,1,7) = SUBSTR(stock.date,1,7)) tt2");
        add("(select count(id) c from pkwoman where SUBSTR(tt3,1,7) = SUBSTR(stock.date,1,7)) tt3");
        add("(select count(id) c from pkwoman where SUBSTR(tt4,1,7) = SUBSTR(stock.date,1,7)) tt4");
        add("(select count(id) c from pkwoman where SUBSTR(tt5,1,7) = SUBSTR(stock.date,1,7)) tt5");
        add("(select count(id) c from pkchild where SUBSTR(bcg,1,7) = SUBSTR(stock.date,1,7)) bcg");
        add("(select count(id) c from pkchild where SUBSTR(opv0,1,7) = SUBSTR(stock.date,1,7)) opv0");
        add("(select count(id) c from pkchild where SUBSTR(opv1,1,7) = SUBSTR(stock.date,1,7)) opv1");
        add("(select count(id) c from pkchild where SUBSTR(opv2,1,7) = SUBSTR(stock.date,1,7)) opv2");
        add("(select count(id) c from pkchild where SUBSTR(opv3,1,7) = SUBSTR(stock.date,1,7)) opv3");
        add("(select count(id) c from pkchild where SUBSTR(ipv,1,7) = SUBSTR(stock.date,1,7)) ipv");
        add("(select count(id) c from pkchild where SUBSTR(pcv1,1,7) = SUBSTR(stock.date,1,7)) pcv1");
        add("(select count(id) c from pkchild where SUBSTR(pcv2,1,7) = SUBSTR(stock.date,1,7)) pcv2");
        add("(select count(id) c from pkchild where SUBSTR(pcv3,1,7) = SUBSTR(stock.date,1,7)) pcv3");
        add("(select count(id) c from pkchild where SUBSTR(measles1,1,7) = SUBSTR(stock.date,1,7)) measles1");
        add("(select count(id) c from pkchild where SUBSTR(measles2,1,7) = SUBSTR(stock.date,1,7)) measles2");
        add("(select count(id) c from pkchild where SUBSTR(penta1,1,7) = SUBSTR(stock.date,1,7)) penta1");
        add("(select count(id) c from pkchild where SUBSTR(penta2,1,7) = SUBSTR(stock.date,1,7)) penta2");
        add("(select count(id) c from pkchild where SUBSTR(penta3,1,7) = SUBSTR(stock.date,1,7)) penta3");
    }};

    @Override
    public RegisterDataLoaderHandler loaderHandler() {
        if (loaderHandler == null){
            loaderHandler = new RegisterDataCursorLoaderHandler(getActivity(),
                    new RegisterQuery("stock", "id", customColumnsMonthly, "report='monthly'").limitAndOffset(10,0),
                    new RegisterCursorAdapter(getActivity(), clientsProvider()));
        }
        return loaderHandler;
    }

    private boolean isDailyRegister(){
        return getCurrentServiceModeOption() == null || getCurrentServiceModeOption().name().toLowerCase().contains("dail");
    }

    @Override
    protected void onCreation() {    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.stock_detail_holder) {
                ((RegisterActivity) getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), false, null);

            }
        }
    }
}
