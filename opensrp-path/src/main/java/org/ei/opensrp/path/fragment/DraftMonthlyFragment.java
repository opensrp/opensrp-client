package org.ei.opensrp.path.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.HIA2ReportsActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.MonthlyTally;
import org.ei.opensrp.path.repository.MonthlyTalliesRepository;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.customControls.FontVariant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import util.Utils;

/**
 * Created by coder on 6/7/17.
 */
public class DraftMonthlyFragment extends Fragment {

    private Button startNewReportEnabled;
    private Button startNewReportDisabled;
    private AlertDialog alertDialog;

    public static DraftMonthlyFragment newInstance() {
        DraftMonthlyFragment fragment = new DraftMonthlyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentview = inflater.inflate(R.layout.sent_monthly_fragment, container, false);

        startNewReportEnabled = (Button) fragmentview.findViewById(R.id.start_new_report_enabled);
        startNewReportDisabled = (Button) fragmentview.findViewById(R.id.start_new_report_disabled);
        setupSaveDraftReportsView(fragmentview);

        return fragmentview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            Utils.startAsyncTask(new AsyncTask<Void, Void, List<Date>>() {
                @Override
                protected List<Date> doInBackground(Void... params) {
                    MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication.getInstance().monthlyTalliesRepository();
                    return monthlyTalliesRepository.findAllUnsentMonths();
                }

                @Override
                protected void onPostExecute(List<Date> dates) {
                    updateStartNewReportButton(dates);
                }
            }, null);
        }
    }

    private void updateStartNewReportButton(final List<Date> dates) {

        boolean hia2ReportsReady = dates != null && !dates.isEmpty();
        refreshDraftMonthyTitle(dates == null ? 0 : dates.size());

        startNewReportEnabled.setVisibility(View.GONE);
        startNewReportDisabled.setVisibility(View.GONE);

        if (hia2ReportsReady) {

            startNewReportEnabled.setVisibility(View.VISIBLE);
            startNewReportEnabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateResults(dates, monthClickListener);

                }
            });

        } else {

            startNewReportDisabled.setVisibility(View.VISIBLE);
            startNewReportDisabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show(Snackbar.make(startNewReportDisabled, getString(R.string.no_monthly_ready), Snackbar.LENGTH_SHORT));
                }
            });
        }
    }

    private void setupSaveDraftReportsView(View inflatedView) {
       final ListView listView = (ListView) inflatedView.findViewById(R.id.list);
       final TextView noDraftsText = (TextView) inflatedView.findViewById(R.id.empty_view);
        //hide empty_view
        Utils.startAsyncTask(new AsyncTask<Void, Void, List<MonthlyTally>>() {
            @Override
            protected List<MonthlyTally> doInBackground(Void... params) {
                MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication.getInstance().monthlyTalliesRepository();
                List<MonthlyTally> tallies = monthlyTalliesRepository.findAllEditedUnsentMonths();
                return tallies;
            }

            @Override
            protected void onPostExecute(List<MonthlyTally> dates) {
                if(!dates.isEmpty()) {
                    noDraftsText.setVisibility(View.GONE);
                    updateDraftsReportListView(listView, dates);
                }
            }
        }, null);


    }
    private void updateDraftsReportListView(final ListView listView, final List<MonthlyTally> list){
        final SimpleDateFormat df= new SimpleDateFormat("MMM yyyy");
        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                    convertView = inflater.inflate(R.layout.month_draft_item, null);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                TextView startedAt = (TextView) convertView.findViewById(R.id.month_draft_started_at);
                MonthlyTally date = list.get(position);
                String text = df.format(date.getMonth());
                String startedat = MonthlyTalliesRepository.dfddmmyy.format(date.getCreatedAt());
                String started = getActivity().getString(R.string.started);
                tv.setText(text);
                tv.setTag(text);
                startedAt.setText(started+" "+startedat);

                convertView.setOnClickListener(monthDraftsClickListener);
                convertView.setTag(date.getMonth());

                return convertView;
            }
        };
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(baseAdapter);
    }

    private void updateResults(final List<Date> list, final View.OnClickListener clickListener) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.month_results, null);

        ListView listView = (ListView) view.findViewById(R.id.list_view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.PathDialog);
        builder.setView(view);
        builder.setCancelable(true);

        CustomFontTextView title = new CustomFontTextView(getActivity());
        title.setText(getString(R.string.reports_available));
        title.setGravity(Gravity.LEFT);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        title.setFontVariant(FontVariant.BOLD);
        title.setPadding(getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin), getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin), getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin));

        builder.setCustomTitle(title);

        alertDialog = builder.create();


        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object getItem(int position) {
                return list.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                    convertView = inflater.inflate(R.layout.month_item, null);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                Date date = list.get(position);
                String text = MonthlyTalliesRepository.MONTH_FORMAT.format(date);
                tv.setText(text);
                tv.setTag(date);

                convertView.setOnClickListener(clickListener);
                convertView.setTag(list.get(position));

                return convertView;
            }
        };

        listView.setAdapter(baseAdapter);
        alertDialog.show();

    }

    View.OnClickListener monthClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            alertDialog.dismiss();

            Object tag = v.getTag();
            if (tag != null && tag instanceof Date) {
                startMonthlyReportForm((Date) tag);
            }

        }
    };
    View.OnClickListener monthDraftsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //alertDialog.dismiss();

            Object tag = v.getTag();
            if (tag != null && tag instanceof Date) {
                startMonthlyReportForm((Date) tag);
            }

        }
    };

    private void show(final Snackbar snackbar) {
        if (snackbar == null) {
            return;
        }

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        snackbar.show();

    }

    protected void startMonthlyReportForm(Date date) {
        ((HIA2ReportsActivity) getActivity()).startMonthlyReportForm("hia2_monthly_report", date);
    }

    private void refreshDraftMonthyTitle(int count) {
        ((HIA2ReportsActivity) getActivity()).refreshDraftMonthlyTitle(count);

    }
}

