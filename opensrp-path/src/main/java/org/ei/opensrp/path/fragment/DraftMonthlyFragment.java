package org.ei.opensrp.path.fragment;

import android.app.Activity;
import android.graphics.Typeface;
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
import org.ei.opensrp.path.receiver.Hia2ServiceBroadcastReceiver;
import org.ei.opensrp.path.repository.MonthlyTalliesRepository;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.customControls.FontVariant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import util.Utils;

/**
 * Created by coder on 6/7/17.
 */
public class DraftMonthlyFragment extends Fragment
        implements Hia2ServiceBroadcastReceiver.Hia2ServiceListener {
    private Button startNewReportEnabled;
    private Button startNewReportDisabled;
    private AlertDialog alertDialog;
    private DraftsAdapter draftsAdapter;
    private ListView listView;
    private View noDraftsView;

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

        listView = (ListView) fragmentview.findViewById(R.id.list);
        noDraftsView =  fragmentview.findViewById(R.id.empty_view);
        startNewReportEnabled = (Button) fragmentview.findViewById(R.id.start_new_report_enabled);
        startNewReportDisabled = (Button) fragmentview.findViewById(R.id.start_new_report_disabled);

        return fragmentview;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupEditedDraftsView();
        setupUneditedDraftsView();
        Hia2ServiceBroadcastReceiver.getInstance().addHia2ServiceListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Hia2ServiceBroadcastReceiver.getInstance().removeHia2ServiceListener(this);
    }

    private void updateStartNewReportButton(final List<Date> dates) {
        boolean hia2ReportsReady = dates != null && !dates.isEmpty();

        startNewReportEnabled.setVisibility(View.GONE);
        startNewReportDisabled.setVisibility(View.GONE);

        if (hia2ReportsReady) {
            Collections.sort(dates, new Comparator<Date>() {
                @Override
                public int compare(Date lhs, Date rhs) {
                    return rhs.compareTo(lhs);
                }
            });
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

    private void setupUneditedDraftsView() {
        Utils.startAsyncTask(new AsyncTask<Void, Void, List<Date>>() {
            @Override
            protected List<Date> doInBackground(Void... params) {
                MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication
                        .getInstance().monthlyTalliesRepository();
                Calendar startDate = Calendar.getInstance();
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);

                Calendar endDate = Calendar.getInstance();
                endDate.set(Calendar.DAY_OF_MONTH, 1);// Set date to first day of this month
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                endDate.add(Calendar.DATE, -1);// Move the date to last day of last month

                return monthlyTalliesRepository.findUneditedDraftMonths(startDate.getTime(),
                        endDate.getTime());
            }

            @Override
            protected void onPostExecute(List<Date> dates) {
                updateStartNewReportButton(dates);
            }
        }, null);
    }

    private void setupEditedDraftsView() {
        ((HIA2ReportsActivity) getActivity()).refreshDraftMonthlyTitle();

        Utils.startAsyncTask(new HIA2ReportsActivity.FetchEditedMonthlyTalliesTask(new HIA2ReportsActivity.FetchEditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(List<MonthlyTally> monthlyTallies) {
                updateDraftsReportListView(monthlyTallies);
            }
        }), null);
    }

    private void updateDraftsReportListView(final List<MonthlyTally> monthlyTallies) {
        if (monthlyTallies != null && !monthlyTallies.isEmpty()) {
            noDraftsView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            if (draftsAdapter == null) {
                draftsAdapter = new DraftsAdapter(monthlyTallies);
                listView.setAdapter(draftsAdapter);
            } else {
                draftsAdapter.setList(monthlyTallies);
                draftsAdapter.notifyDataSetChanged();
            }
        } else {
            noDraftsView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
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
                String text = MonthlyTalliesRepository.DF_YYYYMM.format(date);
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
                startMonthlyReportForm((Date) tag, true);
            }

        }
    };
    View.OnClickListener monthDraftsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Date) {
                startMonthlyReportForm((Date) tag, false);
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

    protected void startMonthlyReportForm(Date date, boolean firstTimeEdit) {
        ((HIA2ReportsActivity) getActivity()).startMonthlyReportForm("hia2_monthly_report", date, firstTimeEdit);
    }

    @Override
    public void onServiceFinish(String actionType) {
        if (Hia2ServiceBroadcastReceiver.TYPE_GENERATE_MONTHLY_REPORT.equals(actionType)) {
            setupEditedDraftsView();
            setupUneditedDraftsView();
        }
    }

    private class DraftsAdapter extends BaseAdapter {
        private List<MonthlyTally> list;

        public DraftsAdapter(List<MonthlyTally> list) {
            setList(list);
        }

        public void setList(List<MonthlyTally> list) {
            this.list = list;
            if (this.list != null) {
                Collections.sort(list, new Comparator<MonthlyTally>() {
                    @Override
                    public int compare(MonthlyTally lhs, MonthlyTally rhs) {
                        return rhs.getMonth().compareTo(lhs.getMonth());
                    }
                });
            }
        }

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
            SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.month_draft_item, null);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.tv);
            TextView startedAt = (TextView) convertView.findViewById(R.id.month_draft_started_at);
            MonthlyTally date = list.get(position);
            String text = df.format(date.getMonth());
            String startedat = MonthlyTalliesRepository.DF_DDMMYY.format(date.getCreatedAt());
            String started = getActivity().getString(R.string.started);
            tv.setText(text);
            tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            tv.setTag(text);
            startedAt.setText(started + " " + startedat);

            convertView.setOnClickListener(monthDraftsClickListener);
            convertView.setTag(date.getMonth());

            return convertView;
        }
    }
}

