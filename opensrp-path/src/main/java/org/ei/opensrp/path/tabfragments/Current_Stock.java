package org.ei.opensrp.path.tabfragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.activity.StockControlActivity;
import org.ei.opensrp.path.adapter.StockPaginatedCursorAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.fragment.CursorAdapterFragment;
import org.ei.opensrp.path.provider.ChildSmartClientsProvider;
import org.ei.opensrp.path.provider.StockRowSmartClientsProvider;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import util.JsonFormUtils;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Current_Stock#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Current_Stock extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String KEY = "key";
    private static final String VALUE ="value" ;
    public static final String STEP1 = "step1";
    public static final String FIELDS = "fields";
    public static final SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");

    ///////////////////////////////////////block for list///////////////////
    public static final String DIALOG_TAG = "dialog";
    private boolean refreshList;
    public static final List<? extends DialogOption> DEFAULT_FILTER_OPTIONS = asList(new AllClientsFilter());
    public ListView clientsView;
    public ProgressBar clientsProgressView;
    public static int totalcount = 0;
    public static int currentlimit = 20;
    public static int currentoffset = 0;
    public String mainSelect;
    public String filters = "";
    public String mainCondition = "";
    public String Sortqueries;
    private String currentquery;
    public String tablename;
    public String countSelect;
    public String joinTable="";

    protected static final int LOADER_ID = 0;
    private static final String INIT_LOADER = "init";
    private StockRepository stockRepository;


    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }


    public StockPaginatedCursorAdapter getClientsCursorAdapter() {
        return clientAdapter;
    }

    public void setClientsAdapter(StockPaginatedCursorAdapter clientsAdapter) {
        this.clientAdapter = clientsAdapter;
    }

    public StockPaginatedCursorAdapter clientAdapter;


    public View mView;



    private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();

    ///////////////////////////////////////////////////////////////////////



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Current_Stock() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Current_Stock.
     */
    // TODO: Rename and change types and number of parameters
    public static Current_Stock newInstance(String param1, String param2) {
        Current_Stock fragment = new Current_Stock();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current__stock, container, false);
        mView = view;
        clientsProgressView = (ProgressBar) view.findViewById(R.id.client_list_progress);
        clientsView = (ListView) view.findViewById(R.id.list);
        paginationViewHandler.addPagination(clientsView);

        stockRepository = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context().alertService());



        Button received = (Button)view.findViewById(R.id.received);
        Button issued = (Button)view.findViewById(R.id.issued);
        Button adjustment = (Button)view.findViewById(R.id.loss_adj);

        TextView vaccine_name = (TextView)view.findViewById(R.id.name);
        vaccine_name.setText(((StockControlActivity)getActivity()).vaccine_type.getName() + " Stock: ");

        received.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchReceivedForm();
            }
        });
        issued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchIssuedForm();
            }
        });
        adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAdjustmentForm();
            }
        });

        onInitialization();
        refresh();
        return view;
    }
    private void getValueForStock(View view) {
        TextView stockvalue = (TextView)view.findViewById(R.id.vials);
        stockvalue.setText(""+((StockControlActivity)getActivity()).getcurrentVialNumber()+ " vials");

    }


    private void onInitialization() {
        String tableName = "Stocks";
//        String parentTableName = "ec_mother";


        StockRowSmartClientsProvider hhscp = new StockRowSmartClientsProvider(getActivity(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, context().alertService(),stockRepository );
        clientAdapter = new StockPaginatedCursorAdapter(getActivity(), null, hhscp, stockRepository);
        clientsView.setAdapter(clientAdapter);
        clientAdapter.notifyDataSetChanged();
        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        countSelect = countqueryBUilder.mainCondition("Stocks."+StockRepository.VACCINE_TYPE_ID+" = "+ ((StockControlActivity)getActivity()).vaccine_type.getId());
        mainCondition = "";
        CountExecute();
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.setSelectquery("Select * FROM Stocks Where Stocks."+StockRepository.VACCINE_TYPE_ID+" = "+ ((StockControlActivity)getActivity()).vaccine_type.getId());
        queryBUilder.orderbyCondition(stockRepository.DATE_CREATED + " DESC, "+stockRepository.DATE_UPDATED + " DESC");
//        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
//                tableName + "._id",
//                tableName + ".vaccine_type_id",
//                tableName + ".transaction_type",
//                tableName + ".providerid",
//                tableName + ".value",
//                tableName + ".date_created",
//                tableName + ".to_from",
//                tableName + ".date_updated"
//        });
//        queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
        mainSelect = queryBUilder.mainCondition("");
        Sortqueries = "";

        currentlimit = 20;
        currentoffset = 0;

        filterandSortInInitializeQueries();

        refresh();
    }

    private void launchReceivedForm() {
        Context context = Context.getInstance();
        Intent intent = new Intent(getActivity().getApplicationContext(), PathJsonFormActivity.class);
        try {
            JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("stock_received_form");
            String vaccine_name = ((StockControlActivity)getActivity()).vaccine_type.getName();
            String formmetadata = form.toString().replace("[vaccine]",vaccine_name);
            intent.putExtra("json", formmetadata);
            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void launchAdjustmentForm() {
        Context context = Context.getInstance();
        Intent intent = new Intent(getActivity().getApplicationContext(), PathJsonFormActivity.class);
        try {
            JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("stock_adjustment_form");
            String vaccine_name = ((StockControlActivity)getActivity()).vaccine_type.getName();
            String formmetadata = form.toString().replace("[vaccine]",vaccine_name);
            intent.putExtra("json", formmetadata);
            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void launchIssuedForm() {
        Context context = Context.getInstance();
        Intent intent = new Intent(getActivity().getApplicationContext(), PathJsonFormActivity.class);
        try {
            JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("stock_issued_form");
            String vaccine_name = ((StockControlActivity)getActivity()).vaccine_type.getName();
            String formmetadata = form.toString().replace("[vaccine]",vaccine_name);
            intent.putExtra("json", formmetadata);
            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CountExecute();
        filterandSortInInitializeQueries();
        refresh();
        getValueForStock(mView);
    }
    public void returnfromform(){
        ((StockControlActivity)getActivity()).planning_stock_fragment.loatDataView(((StockControlActivity)getActivity()).planning_stock_fragment.mainview);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        {
            if (resultCode == getActivity().RESULT_OK) {

                try {
                    String jsonString = data.getStringExtra("json");
                    Log.d("JSONResult", jsonString);
                    JSONObject jsonForm = new JSONObject(jsonString);
                    JSONObject step = jsonForm.getJSONObject("step1");
                    String FormTitle = step.getString("title");
                    if(FormTitle.contains("Stock Issued")){
                        processStockIssued(jsonString);
                    }
                    if(FormTitle.contains("Stock Received")) {
                        processStockReceived(jsonString);
                    }
                    if(FormTitle.contains("Stock Loss/Adjustment")) {
                        processStockLossAdjustment(jsonString);
                    }

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
    }

    private void processStockLossAdjustment(String jsonString) {
        JSONObject jsonForm = null;
        try {
            Context context = Context.getInstance();
            jsonForm = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonForm);
            String Date_Stock_Received = JsonFormUtils.getFieldValue(fields, "Date_Stock_loss_adjustment");
            String Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Reason_for_adjustment");
            String vials_received = JsonFormUtils.getFieldValue(fields, "Vials_Adjustment");
            if(Received_Stock_From.equalsIgnoreCase("Other")){
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "adjusted_Stock_TO_Other");
            }else{
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Reason_for_adjustment");
            }

            StockRepository str = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(Date_Stock_Received)) {
                Date dateTime = JsonFormUtils.formatDate(Date_Stock_Received, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }
            Stock stock = new Stock(null,Stock.loss_adjustment,allSharedPreferences.fetchRegisteredANM(),Integer.parseInt(vials_received),encounterDate.getTime(),Received_Stock_From,StockRepository.TYPE_Unsynced,System.currentTimeMillis(),""+((StockControlActivity)(getActivity())).vaccine_type.getId());
            str.add(stock);
            returnfromform();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processStockReceived(String jsonString) {
        JSONObject jsonForm = null;
        try {
            Context context = Context.getInstance();
            jsonForm = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonForm);
            String Date_Stock_Received = JsonFormUtils.getFieldValue(fields, "Date_Stock_Received");
            String Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Received_Stock_From");
            if(Received_Stock_From.equalsIgnoreCase("DHO")){
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Received_Stock_From");
            }else{
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Received_Stock_From_Other");
            }
            String vials_received = JsonFormUtils.getFieldValue(fields, "Vials_Received");

            StockRepository str = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(Date_Stock_Received)) {
                Date dateTime = JsonFormUtils.formatDate(Date_Stock_Received, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }
            Stock stock = new Stock(null,Stock.received,allSharedPreferences.fetchRegisteredANM(),Integer.parseInt(vials_received),encounterDate.getTime(),Received_Stock_From,StockRepository.TYPE_Unsynced,System.currentTimeMillis(),""+((StockControlActivity)(getActivity())).vaccine_type.getId());
            str.add(stock);
            returnfromform();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void processStockIssued(String jsonString) {
        JSONObject jsonForm = null;
        try {
            Context context = Context.getInstance();
            jsonForm = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonForm);
            String Date_Stock_Received = JsonFormUtils.getFieldValue(fields, "Date_Stock_Issued");
            String Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Issued_Stock_To");
            if(Received_Stock_From.equalsIgnoreCase("Other")){
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Issued_Stock_TO_Other");
            }else{
                Received_Stock_From = JsonFormUtils.getFieldValue(fields, "Issued_Stock_To");
            }
            String vials_received = JsonFormUtils.getFieldValue(fields, "Vials_Issued");
            String vials_wasted = JsonFormUtils.getFieldValue(fields, "Vials_Wasted");
            if(StringUtils.isNotBlank(vials_wasted)){
                vials_received = ""+(Integer.parseInt(vials_received)+Integer.parseInt(vials_wasted));
            }

            StockRepository str = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(Date_Stock_Received)) {
                Date dateTime = JsonFormUtils.formatDate(Date_Stock_Received, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }
            Stock stock = new Stock(null,Stock.issued,allSharedPreferences.fetchRegisteredANM(),-1*Integer.parseInt(vials_received),encounterDate.getTime(),Received_Stock_From,StockRepository.TYPE_Unsynced,System.currentTimeMillis(),""+((StockControlActivity)(getActivity())).vaccine_type.getId());
            str.add(stock);
            returnfromform();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private TextView pageInfoView;
    private Button nextPageView;
    private Button previousPageView;

    private class PaginationViewHandler implements View.OnClickListener {



        private void addPagination(ListView clientsView) {
            LinearLayout toaddNote = new LinearLayout(getActivity());
            toaddNote.setOrientation(LinearLayout.VERTICAL);
            ViewGroup footerView = getPaginationView();
            nextPageView = (Button) footerView.findViewById(R.id.btn_next_page);
            previousPageView = (Button) footerView.findViewById(R.id.btn_previous_page);
            pageInfoView = (TextView) footerView.findViewById(R.id.txt_page_info);

            nextPageView.setOnClickListener(this);
            previousPageView.setOnClickListener(this);

            footerView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.pagination_bar_height)));
            toaddNote.addView(footerView);


            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            toaddNote.addView((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.current_stock_note, null),p);

            clientsView.addFooterView(toaddNote);
            refresh();
        }

        private ViewGroup getPaginationView() {

            return (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.smart_register_pagination, null);
        }

        public void refreshListView(){
            setRefreshList(true);
            setRefreshList(false);
        }






        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_next_page) {
                gotoNextPage();

            } else if (i == R.id.btn_previous_page) {
                goBackToPreviousPage();

            }
        }

    }
    private int getCurrentPageCount() {
        if(currentoffset != 0) {
            if((currentoffset/currentlimit) != 0) {
                return  ((currentoffset / currentlimit)+1);
            }else {
                return 1;
            }
        }else{
            return 1;
        }
    }
    private int getTotalcount(){
        if(totalcount%currentlimit == 0){
            return (totalcount/currentlimit);
        }else {
            return ((totalcount / currentlimit)+1);
        }
    }
    public void refresh() {
        pageInfoView.setText(
                format(getResources().getString(R.string.str_page_info),
                        (getCurrentPageCount()),
                        getTotalcount()));
        paginationViewHandler.getPaginationView().setVisibility(hasNextPage() ? VISIBLE : INVISIBLE);
        pageInfoView.setVisibility(hasNextPage() ? VISIBLE : INVISIBLE);
        nextPageView.setVisibility(hasNextPage() ? VISIBLE : INVISIBLE);
        previousPageView.setVisibility(hasPreviousPage() ? VISIBLE : INVISIBLE);
    }

    private boolean hasNextPage() {

        return ((totalcount>(currentoffset+currentlimit)));
    }

    private boolean hasPreviousPage() {
        return currentoffset!=0;
    }

    public void gotoNextPage() {
        if(!(currentoffset+currentlimit>totalcount)){
            currentoffset = currentoffset+currentlimit;
            filterandSortExecute();
        }
    }

    public void goBackToPreviousPage() {
        if(currentoffset>0){
            currentoffset = currentoffset-currentlimit;
            filterandSortExecute();
        }
    }

    public void filterandSortInInitializeQueries(){
        if(isPausedOrRefreshList()){
            this.showProgressView();
            this.filterandSortExecute();
        } else {
            this.initialFilterandSortExecute();
        }
    }


    public void initialFilterandSortExecute() {
        Loader<Cursor> loader = getLoaderManager().getLoader(LOADER_ID);
        showProgressView();
        if(loader != null) {
            filterandSortExecute();
        }else {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    public void filterandSortExecute() {
        refresh();

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void showProgressView(){
        if(clientsProgressView.getVisibility() == INVISIBLE) {
            clientsProgressView.setVisibility(View.VISIBLE);
        }

        if(clientsView.getVisibility() == VISIBLE) {
            clientsView.setVisibility(View.INVISIBLE);
        }
    }

    public void hideProgressView(){
        if(clientsProgressView.getVisibility() == VISIBLE) {
            clientsProgressView.setVisibility(INVISIBLE);
        }
        if(clientsView.getVisibility() == INVISIBLE) {
            clientsView.setVisibility(VISIBLE);
        }
    }

    private String filterandSortQuery(){
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try{
            if(isValidFilterForFts(commonRepository())){
                String sql = sqb.searchQueryFts(tablename, joinTable, mainCondition, filters, Sortqueries, currentlimit, currentoffset);
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename + "." + CommonRepository.ID_COLUMN, Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query,currentlimit,currentoffset));

            }
        }catch (Exception e){
            Log.e(getClass().getName(), e.toString(), e);
        }

        return query;
    }

    public void CountExecute(){
        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, joinTable, mainCondition, filters);
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename + "." + CommonRepository.ID_COLUMN);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);
            }

            Log.i(getClass().getName(), query);
            c = VaccinatorApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query, null);
//            c = commonRepository().RawCustomQueryForAdapter(query);
            c.moveToFirst();
            totalcount = c.getInt(0);
            Log.v("total count here", "" + totalcount);
            currentlimit = 20;
            currentoffset = 0;

        }catch (Exception e){
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if(c != null) {
                c.close();
            }
        }
    }

    protected boolean isValidFilterForFts(CommonRepository commonRepository){
        return false;
//        return commonRepository.isFts() && filters != null
//                && !StringUtils.containsIgnoreCase(filters, "like")
//                && !StringUtils.startsWithIgnoreCase(filters.trim(), "and ");
    }





    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()){
                    @Override
                    public Cursor loadInBackground() {
                        String query = filterandSortQuery();
                        Cursor cursor = VaccinatorApplication.getInstance().getRepository().getReadableDatabase().rawQuery(query,null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgressView();
                            };
                        });

                        return cursor;
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        clientAdapter.swapCursor(cursor);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clientAdapter.swapCursor(null);
    }

    public CommonRepository commonRepository(){
        return new CommonRepository(VaccinatorApplication.createCommonFtsObject(),StockRepository.stock_TABLE_NAME,StockRepository.stock_TABLE_COLUMNS);
    }

    public boolean isPausedOrRefreshList(){
        return isPaused() || isRefreshList();
    }
    public boolean isRefreshList() {
        return refreshList;
    }
    protected Context context() {
        return Context.getInstance().updateApplicationContext(this.getActivity().getApplicationContext());
    }
    public void setRefreshList(boolean refreshList) {
        this.refreshList = refreshList;
    }
    public boolean isPaused() {
        return isPaused;
    }
    private boolean isPaused;





}
