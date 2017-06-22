package org.ei.opensrp.core.template;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectFilterOption;
import org.ei.opensrp.core.R;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
import org.ei.opensrp.core.widget.CustomFontTextView;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.customControls.FontVariant;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Maimoona on 10/12/16.
 */
public abstract class RegisterDataGridFragment extends SecuredFragment {
    public final static int REGISTER_DATA_LOADER_ID = 786;

    private ListView clientsView;
    private ProgressBar clientsProgressView;
    private TextView serviceModeView;
    private TextView appliedVillageFilterView;
    private TextView appliedSortView;
    private EditText searchView;
    private View searchCancelView;
    private SearchType currentSearchType;
    private View searchTypeButton;
    private TextView titleLabelView;
    private View searchButton;
    private FormController formController;

    public RegisterDataGridFragment(FormController formController){
        this.formController = formController;
    }

    public abstract String bindType();

    public abstract RegisterDataLoaderHandler loaderHandler();

    public FormController getFormController(){return formController;}

    public SearchType getCurrentSearchType(){return currentSearchType;}

    public FilterOption getCurrentVillageFilter() {
        return currentVillageFilter;
    }

    public SearchFilterOption getCurrentSearchFilter() {
        return currentSearchFilter;
    }

    public SortingOption getCurrentSortOption() {
        return currentSortOption;
    }

    public ServiceModeOption getCurrentServiceModeOption() {
        return currentServiceModeOption;
    }

    private FilterOption currentVillageFilter;
    private SortingOption currentSortOption;
    private SearchFilterOption currentSearchFilter;
    private ServiceModeOption currentServiceModeOption;

    public View mView;

    public void setCurrentSearchFilter(SearchFilterOption currentSearchFilter) {
        this.currentSearchFilter = currentSearchFilter;
    }

    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();
    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();
    private final SearchCancelHandler searchCancelHandler = new SearchCancelHandler();
    private final SearchTextChangeHandler searchTextChangeHandler = new SearchTextChangeHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      //  getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = inflater.inflate(R.layout.register_activity, container, false);
        mView = view;
        onInitialization();
        setupViews(view);
        setupLoader();
        return view;
    }

    protected void setupViews(View view) {
        setupNavBarViews(view);
        populateClientListHeaderView(getDefaultOptionsProvider().serviceMode().getHeaderProvider(), view);

        clientsProgressView = (ProgressBar) view.findViewById(R.id.client_list_progress);
        clientsView = (ListView) view.findViewById(R.id.list);

        setupStatusBarViews(view);
        paginationViewHandler.addPagination(clientsView);

        clientsView.setAdapter(loaderHandler().adapter());

        updateDefaultOptions();
    }

    private void setupLoader() {
        getActivity().getSupportLoaderManager().initLoader(REGISTER_DATA_LOADER_ID, createQueryBundle(0), loaderHandler());

        loaderHandler().setLoadListener(new RegisterDataLoaderHandler.LoadListener() {
            @Override
            public void before() {
                clientsProgressView.setVisibility(VISIBLE);
                clientsView.setVisibility(INVISIBLE);
            }

            @Override
            public void after() {
                paginationViewHandler.refresh();
                clientsProgressView.setVisibility(View.GONE);
                clientsView.setVisibility(VISIBLE);
            }
        });
    }

    private void setupStatusBarViews(View view) {
        appliedSortView = (TextView) view.findViewById(R.id.sorted_by);
        appliedVillageFilterView = (TextView) view.findViewById(R.id.village);
        ((TextView)mView.findViewById(R.id.statusbar_today)).setText(DateTime.now().toString("dd-MM-yyyy"));
    }

    private void setupNavBarViews(View view) {
        view.findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);

        setupTitleView(view);

        View villageFilterView = view.findViewById(R.id.filter_selection);
        villageFilterView.setOnClickListener(navBarActionsHandler);

        View sortView = view.findViewById(R.id.sort_selection);
        sortView.setOnClickListener(navBarActionsHandler);

        serviceModeView = (TextView)view.findViewById(R.id.service_mode_selection);
        serviceModeView.setOnClickListener(navBarActionsHandler);

        view.findViewById(R.id.register_client).setOnClickListener(navBarActionsHandler);

        setupSearchView(view);
    }

    protected void setServiceModeViewDrawableRight(Drawable drawable) {
        serviceModeView.setCompoundDrawables(null, null, drawable, null);
    }

    private void setupTitleView(View view) {
        ViewGroup titleLayout = (ViewGroup) view.findViewById(R.id.title_layout);
        titleLayout.setOnClickListener(navBarActionsHandler);

        titleLabelView = (TextView) view.findViewById(R.id.txt_title_label);
    }

    private void resetSearchTypeView(){
        Log.i(getClass().getName(), "Resetting SearchTypeView");
        if (currentSearchType == null){
            currentSearchType = getDefaultOptionsProvider().searchType();
        }

        if (searchCancelView.getVisibility() == INVISIBLE || searchCancelView.getVisibility() == View.GONE){
            searchTypeButton.setVisibility(VISIBLE);
        }
        else {
            searchTypeButton.setVisibility(View.GONE);
        }

        Log.i(getClass().getName(), "SearchType is "+currentSearchType);

        if(currentSearchType.equals(SearchType.ACTIVE)){
            ((Button) searchTypeButton).setTextColor(getResources().getColor(R.color.accent_material_light));

            searchButton.setVisibility(View.INVISIBLE);
        }
        else {
            ((Button) searchTypeButton).setTextColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));

            searchButton.setVisibility(View.VISIBLE);
        }
    }

    public void setupSearchView(View view) {
        searchButton = view.findViewById(R.id.btn_search_go);
        searchButton.setOnClickListener(searchTextChangeHandler);

        searchCancelView = view.findViewById(R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);

        searchTypeButton = view.findViewById(R.id.btn_search_autocomplete);
        searchTypeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSearchType == null){
                    currentSearchType = getDefaultOptionsProvider().searchType();
                }
                else if (currentSearchType.equals(SearchType.ACTIVE)){
                    currentSearchType = SearchType.PASSIVE;
                }
                else {
                    currentSearchType = SearchType.ACTIVE;
                }

                resetSearchTypeView();
            }
        });

        searchView = (EditText) view.findViewById(R.id.edt_search);
        searchView.setHint(getNavBarOptionsProvider().searchHint());
        searchView.addTextChangedListener(searchTextChangeHandler);

        resetSearchTypeView();
    }

    private void updateDefaultOptions() {
        currentServiceModeOption = getDefaultOptionsProvider().serviceMode();
        currentSearchFilter = getDefaultOptionsProvider().searchFilterOption();
        currentVillageFilter = getDefaultOptionsProvider().villageFilter();
        currentSortOption = getDefaultOptionsProvider().sortOption();

        appliedSortView.setText(currentSortOption.name());
        appliedVillageFilterView.setText(currentVillageFilter==null?"":currentVillageFilter.name());
        serviceModeView.setText(currentServiceModeOption.name());
        titleLabelView.setText(getDefaultOptionsProvider().nameInShortFormForTitle());
    }

    protected void populateClientListHeaderView(HeaderProvider headerProvider, View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(R.id.clients_header_layout);
        clientsHeaderLayout.removeAllViewsInLayout();
        int columnCount = headerProvider.count();
        int[] weights = headerProvider.weights();
        int[] headerTxtResIds = headerProvider.headerTextResourceIds();
        clientsHeaderLayout.setWeightSum(headerProvider.weightSum());

        for (int i = 0; i < columnCount; i++) {
            clientsHeaderLayout.addView(getColumnHeaderView(i, weights, headerTxtResIds));
        }
    }

    protected View getColumnHeaderView(int i, int[] weights, int[] headerTxtResIds) {
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, R.style.CustomFontTextViewStyle_Header_Black);
        header.setFontVariant(FontVariant.BLACK);
        header.setTextSize(16);
        header.setTextColor(getResources().getColor(R.color.client_list_header_text_color));
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        weights[i]);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        header.setGravity(TEXT_ALIGNMENT_CENTER);

        header.setLayoutParams(lp);
        header.setText(headerTxtResIds[i]);
        return header;
    }

    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        currentServiceModeOption = serviceModeOption;
        serviceModeView.setText(serviceModeOption.name());

        refreshDataList(0);

        populateClientListHeaderView(serviceModeOption.getHeaderProvider(), view);
    }

    public void onSortSelection(SortingOption sortBy) {
        Log.v("he pressed this",sortBy.name());
        currentSortOption = sortBy;
        appliedSortView.setText(sortBy.name());

        refreshDataList(0);
    }

    public void onFilterSelection(FilterOption filter) {
        currentVillageFilter = filter;
        appliedVillageFilterView.setText(filter.name());

        refreshDataList(0);
    }

    public void onFilterManual(String filter) {
        currentSearchFilter.setFilter(filter);
        searchView.setText(filter);
        //todo check if on active search it would call it twice

        refreshDataList(0);
    }

    //todo how to handle it??
    protected void onEditSelection(EditOption editOption, SmartRegisterClient client) {
        editOption.doEdit(client);
    }

    private void goBack() {
        getActivity().finish();
    }

    void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    protected void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        ((RegisterActivity)getActivity()).showFragmentDialog(dialogOptionModel, tag);
    }

    protected abstract DefaultOptionsProvider getDefaultOptionsProvider();

    protected abstract NavBarOptionsProvider getNavBarOptionsProvider();

    protected abstract RegisterClientsProvider clientsProvider();

    protected abstract void onInitialization();

    protected abstract void startRegistration();

    public EditText getSearchView() {
        return searchView;
    }

    public View getView(){
        return mView;
    }

    private class FilterDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().filterOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onFilterSelection((FilterOption) option);
        }
    }

    private class SortDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().sortingOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onSortSelection((SortingOption) option);
        }
    }

    protected class ServiceModeDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getNavBarOptionsProvider().serviceModeOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onServiceModeSelection((ServiceModeOption) option, mView);
        }
    }

    protected class PaginationViewHandler implements View.OnClickListener {
        private Button nextPageView;
        private Button previousPageView;
        private TextView pageInfoView;

        private void addPagination(ListView clientsView) {
            ViewGroup footerView = getPaginationView();
            nextPageView = (Button) footerView.findViewById(R.id.btn_next_page);
            previousPageView = (Button) footerView.findViewById(R.id.btn_previous_page);
            pageInfoView = (TextView) footerView.findViewById(R.id.txt_page_info);

            nextPageView.setOnClickListener(this);
            previousPageView.setOnClickListener(this);

            footerView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.pagination_bar_height)));

            clientsView.addFooterView(footerView);
        }

        private ViewGroup getPaginationView() {
            return (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.smart_register_pagination, null);
        }

        public void refresh() {
            pageInfoView.setText(
                    format(getResources().getString(R.string.str_page_info),
                            loaderHandler().pager().currentPage(),
                            loaderHandler().pager().pageCount()));
            nextPageView.setVisibility(loaderHandler().pager().hasNextPage() ? VISIBLE : INVISIBLE);
            previousPageView.setVisibility(loaderHandler().pager().hasPreviousPage() ? VISIBLE : INVISIBLE);
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_next_page) {
                refreshDataList(loaderHandler().pager().nextPageOffset());

            } else if (i == R.id.btn_previous_page) {
                refreshDataList(loaderHandler().pager().previousPageOffset());
            }
        }
    }

    public class NavBarActionsHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.homestacklayout || i == R.id.btn_back_to_home) {
                goBack();
            } else if (i == R.id.register_client) {
                startRegistration();
            } else if (i == R.id.filter_selection) {
                showFragmentDialog(new FilterDialogOptionModel());
            } else if (i == R.id.sort_selection) {
                showFragmentDialog(new SortDialogOptionModel());
            } else if (i == R.id.service_mode_selection) {
                showFragmentDialog(new ServiceModeDialogOptionModel());
            }
        }
    }

    public class SearchCancelHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            clearSearchText();
        }

        private void clearSearchText() {
            onFilterManual("");
        }
    }

    public class SearchTextChangeHandler implements TextWatcher, View.OnClickListener {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before, int count) {
            searchCancelView.setVisibility(isEmpty(cs) ? View.GONE : VISIBLE);
            searchTypeButton.setVisibility(isEmpty(cs) ? VISIBLE : View.GONE);

            if (currentSearchType.equals(SearchType.PASSIVE)){
                return;
            }
            triggerSearch(cs);
        }

        private void triggerSearch(CharSequence cs){
            currentSearchFilter.setFilter(cs.toString());// todo would it throw NPE ever??

            refreshDataList(0);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

        @Override
        public void onClick(View view) {
            Log.i(getClass().getName(), "Button clicked to search");
            triggerSearch(searchView.getText());
        }
    }

    private void refreshDataList(int offset){
        getActivity().getSupportLoaderManager().restartLoader(REGISTER_DATA_LOADER_ID, createQueryBundle(offset), loaderHandler());
    }

    private Bundle createQueryBundle(int offset){
        Bundle b = new Bundle();
        JSONObject map = new JSONObject();
        try {// todo change the way it is sent to loader
            map.put("village", currentVillageFilter==null?null:currentVillageFilter.getCriteria());
            // map.put("service", currentServiceModeOption == null?null:Utils.getLongDateAwareGson().toJson(currentServiceModeOption));
            map.put("search", currentSearchFilter == null||StringUtils.isBlank(currentSearchFilter.getFilter())?null:currentSearchFilter.getCriteria());
            map.put("sort", currentSortOption==null?null:currentSortOption.sort());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        b.putString("params", map.toString());
        b.putInt("offset", offset);
        return b;
    }

    protected void startForm(String formName, String entityId, HashMap<String, String> overrideStringmap) {
        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            overrideStringmap = new HashMap<>();
        }

        String fieldOverrides =  new FieldOverrides(new JSONObject(overrideStringmap).toString()).getJSONString();
        org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides);
        formController.startFormActivity(formName, entityId, fieldOverrides);
    }

    protected void showMessageDialog(String message, DialogInterface.OnClickListener ok) {
        AlertDialog dialog = new AlertDialog.Builder(Context.getInstance().applicationContext())
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", ok)
                .create();

        dialog.show();
    }

    protected void showMessageDialog(String message, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog dialog = new AlertDialog.Builder(Context.getInstance().applicationContext())
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", ok)
                .setNegativeButton("Cancel", cancel)
                .create();

        dialog.show();
    }
    //This would be used in displaying location dialog box in anm location selector
    public String getLocationNameByAttribute(LocationTree locationTree, String tag) {
        //locationTree.
        Map<String, TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();
        Collection<TreeNode<String, Location>> collection = locationMap.values();
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            TreeNode<String, Location> treeNode = (TreeNode<String, Location>) iterator.next();
            Location location = treeNode.getNode();

            for (String s : location.getTags()) {

                if (s.equalsIgnoreCase(tag)) {
                    return location.getName();
                }
            }
        }
        Log.d("Amn Locations", "No location found");
        return null;
    }

    public void addToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addToList(dialogOptionslist, entry.getValue().getChildren());
            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ", "_"), "location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails, name));
            }
        }
    }
}