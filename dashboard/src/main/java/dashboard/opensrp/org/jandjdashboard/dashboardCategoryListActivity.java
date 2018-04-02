package dashboard.opensrp.org.jandjdashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;


import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;
import dashboard.opensrp.org.jandjdashboard.fragments.anc_pnc_encc_StatusDetailFragment;
import dashboard.opensrp.org.jandjdashboard.fragments.contraceptiveSupplyStatusDetailFragment;
import dashboard.opensrp.org.jandjdashboard.fragments.delivery_status_detail_Fragment;
import dashboard.opensrp.org.jandjdashboard.fragments.familyPlanningStatusDetailFragment;
import dashboard.opensrp.org.jandjdashboard.fragments.nutritionDetailFragment;
import dashboard.opensrp.org.jandjdashboard.fragments.reproductive_health_service_Fragment;
import dashboard.opensrp.org.jandjdashboard.fragments.upcomingScheduleStatusDetailFragment;
import dashboard.opensrp.org.jandjdashboard.widgets.calendarPoPUpWindow;

import java.util.List;

import static dashboard.opensrp.org.jandjdashboard.dummy.DummyContent.addDrawables;
import static dashboard.opensrp.org.jandjdashboard.dummy.DummyContent.menudrawable;

/**
 * An activity representing a list of dashboardCategories. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link dashboardCategoryDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class dashboardCategoryListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public String fromdate;
    public String todate;
    public static String currentFragmentName = "";
    public static Fragment currentFragment;
    private boolean mTwoPane;
    public static View previousViewSelectedInLIST = null;
    public controllerHolders controllerholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        controllerholder = (controllerHolders) i.getSerializableExtra("controller_holder");
        addDrawables(this);
        setContentView(R.layout.activity_dashboardcategory_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        toolbar.setTitle("");
        getSupportActionBar().setTitle("");

        View recyclerView = findViewById(R.id.dashboardcategory_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.dashboardcategory_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.

                Bundle arguments = new Bundle();
                arguments.putString(upcomingScheduleStatusDetailFragment.ARG_ITEM_ID,"" );
                arguments.putSerializable("controller_holder",controllerholder);
                upcomingScheduleStatusDetailFragment fragment = new upcomingScheduleStatusDetailFragment();
                fragment.setArguments(arguments);
                currentFragmentName = "upcomingScheduleStatusDetailFragment";
                currentFragment = fragment;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.dashboardcategory_detail_container, fragment)
                        .commit();

            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu_dashboard, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_settings) {// User chose the "Settings" item, show the app settings UI...
            return true;
        } else if (i == R.id.date_filter) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            calendarPoPUpWindow calendarPoPUpWindow = new calendarPoPUpWindow(this);
            calendarPoPUpWindow.showAsDropDown(toolbar);

            return true;
        } else {// If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            return false;
        }
    }

    public void refresh(String fromdate,String todate){
        this.fromdate = fromdate;
        this.todate = todate;
        if(currentFragmentName.equalsIgnoreCase("upcomingScheduleStatusDetailFragment")){
            ((upcomingScheduleStatusDetailFragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("anc_pnc_encc_StatusDetailFragment")){
            ((anc_pnc_encc_StatusDetailFragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("reproductive_health_service_Fragment")){
            ((reproductive_health_service_Fragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("delivery_status_detail_Fragment")){
            ((delivery_status_detail_Fragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("nutrition_detail_Fragment")){
            ((nutritionDetailFragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("contraceptive_supply_status_detailFragment")){
            ((contraceptiveSupplyStatusDetailFragment)currentFragment).refresh(fromdate,todate);
        }
        if(currentFragmentName.equalsIgnoreCase("family_planning_StatusDetailFragment")){
            ((familyPlanningStatusDetailFragment)currentFragment).refresh(fromdate,todate);
        }

    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dashboardcategory_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).content);
            holder.mIdView.setImageDrawable(menudrawable.get(position));


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previousViewSelectedInLIST == null){
                        previousViewSelectedInLIST = holder.mView;
                    }else{
                        previousViewSelectedInLIST.setBackgroundColor(getResources().getColor(R.color.mainblue));
                        previousViewSelectedInLIST = holder.mView;
                    }
                    holder.mView.setBackground(getResources().getDrawable(R.drawable.rotatepointer));
                    if (mTwoPane) {
                        switch (position) {
                            case 0: {
                                Bundle arguments = new Bundle();
                                arguments.putString(upcomingScheduleStatusDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                arguments.putSerializable("controller_holder",controllerholder);
                                upcomingScheduleStatusDetailFragment fragment = new upcomingScheduleStatusDetailFragment();
                                fragment.setArguments(arguments);
                                currentFragmentName = "upcomingScheduleStatusDetailFragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 1: {
                                Bundle arguments = new Bundle();
                                arguments.putString(familyPlanningStatusDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                familyPlanningStatusDetailFragment fragment = new familyPlanningStatusDetailFragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "family_planning_StatusDetailFragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 2: {
                                Bundle arguments = new Bundle();
                                arguments.putString(anc_pnc_encc_StatusDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                anc_pnc_encc_StatusDetailFragment fragment = new anc_pnc_encc_StatusDetailFragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "anc_pnc_encc_StatusDetailFragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 3: {
                                Bundle arguments = new Bundle();
                                arguments.putString(reproductive_health_service_Fragment.ARG_ITEM_ID, holder.mItem.id);
                                reproductive_health_service_Fragment fragment = new reproductive_health_service_Fragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "reproductive_health_service_Fragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 4: {
                                Bundle arguments = new Bundle();
                                arguments.putString(reproductive_health_service_Fragment.ARG_ITEM_ID, holder.mItem.id);
                                delivery_status_detail_Fragment fragment = new delivery_status_detail_Fragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "delivery_status_detail_Fragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 7: {
                                Bundle arguments = new Bundle();
                                arguments.putString(reproductive_health_service_Fragment.ARG_ITEM_ID, holder.mItem.id);
                                nutritionDetailFragment fragment = new nutritionDetailFragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "nutrition_detail_Fragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }
                            case 8: {
                                Bundle arguments = new Bundle();
                                arguments.putString(contraceptiveSupplyStatusDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                                contraceptiveSupplyStatusDetailFragment fragment = new contraceptiveSupplyStatusDetailFragment();
                                arguments.putSerializable("controller_holder",controllerholder);
                                fragment.setArguments(arguments);
                                currentFragmentName = "contraceptive_supply_status_detailFragment";
                                currentFragment = fragment;
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.dashboardcategory_detail_container, fragment)
                                        .commit();
                                break;
                            }

                        }
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, dashboardCategoryDetailActivity.class);
                        intent.putExtra(upcomingScheduleStatusDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (ImageView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
