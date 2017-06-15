package org.ei.opensrp.path.fragment;

import android.app.Activity;
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
import android.widget.Toast;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.path.activity.HIA2ReportsActivity;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.customControls.FontVariant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
        return fragmentview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateStartNewReportButton();
        }
    }

    private void updateStartNewReportButton() {
        final boolean hia2ReportsReady = true; //(new Random()).nextBoolean();

        startNewReportEnabled.setVisibility(View.GONE);
        startNewReportDisabled.setVisibility(View.GONE);

        if (hia2ReportsReady) {

            startNewReportEnabled.setVisibility(View.VISIBLE);
            startNewReportEnabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateResults(dummyData(), monthClickListener);

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

    private void updateResults(final List<String> list, final View.OnClickListener clickListener) {
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
                String text = list.get(position);
                tv.setText(text);

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

            Toast.makeText(getActivity(), v.getTag().toString(), Toast.LENGTH_SHORT).show();
            startMonthlyReportForm();

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

    protected void startMonthlyReportForm() {
        ((HIA2ReportsActivity) getActivity()).startFormActivity("hia2_monthly_report", null, null);
    }

    //TODO REMOVE
    private List<String> dummyData() {
        List<String> list = new ArrayList<String>();

        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.MONTH, false);
        String one = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        list.add(one);

        cal.roll(Calendar.MONTH, false);
        String two = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        list.add(two);


        cal.roll(Calendar.MONTH, false);
        String three = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        list.add(three);

        return list;
    }
}

