package org.ei.opensrp.path.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.R;

/**
 * Created by coder on 6/28/17.
 */
public class SendMonthlyDraftDialogFragment extends DialogFragment {
    String date;
    View.OnClickListener onSendClickedListener;

    public static SendMonthlyDraftDialogFragment newInstance(String month, View.OnClickListener onSendClickedListener) {
        SendMonthlyDraftDialogFragment f = new SendMonthlyDraftDialogFragment();
        f.setDate(month);
        f.setOnSendClickedListener(onSendClickedListener);

        return f;
    }

    public SendMonthlyDraftDialogFragment() {
        super();
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setOnSendClickedListener(View.OnClickListener onSendClickedListener) {
        this.onSendClickedListener = onSendClickedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String provider = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        View view = inflater.inflate(
                R.layout.dialog_fragment_send_monthly,
                container, false);
        TextView tvSendMonthlyDraft = (TextView) view.findViewById(R.id.tv_send_monthly_draft);
        tvSendMonthlyDraft.setText(String.format(
                getString(R.string.send_report_warning),
                date,
                provider));

        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMonthlyDraftDialogFragment.this.dismiss();
            }
        });

        Button sendButton = (Button) view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMonthlyDraftDialogFragment.this.dismiss();
                if (onSendClickedListener != null) {
                    onSendClickedListener.onClick(v);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
            }
        });
    }
}
