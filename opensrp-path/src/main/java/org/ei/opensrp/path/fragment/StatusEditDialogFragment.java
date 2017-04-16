package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.WeightWrapper;
import org.ei.opensrp.path.listener.StatusChangeListener;
import org.ei.opensrp.path.listener.WeightActionListener;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import util.ImageUtils;

@SuppressLint("ValidFragment")
public class StatusEditDialogFragment extends DialogFragment {
    private final Context context;
    private WeightActionListener listener;
    public static Map<String, String> details;
    public static final String DIALOG_TAG = "EditWeightDialogFragment";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private LinearLayout activeLayout;
    private LinearLayout inactiveLayout;
    private LinearLayout LostToFollowUpLayout;
    private static final String inactive="inactive";
    private static final String lostToFollowUp="lost_to_follow_up";
    private ImageView activeImageView;
    private ImageView inactiveImageView;
    private ImageView lostToFollowUpImageView;

    private StatusEditDialogFragment(Context context, Map<String, String> details) {
        this.context = context;
        this.details = details;
    }

    public static StatusEditDialogFragment newInstance(
            Context context,Map<String, String> details) {

        return new StatusEditDialogFragment(context,details);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.status_edit_dialog_view, container, false);
        activeLayout = (LinearLayout)dialogView.findViewById(R.id.activelayout);
        inactiveLayout = (LinearLayout)dialogView.findViewById(R.id.inactivelayout);
        LostToFollowUpLayout = (LinearLayout)dialogView.findViewById(R.id.losttofollowuplayout);
        activeImageView = (ImageView)dialogView.findViewById(R.id.active_check);
        inactiveImageView = (ImageView)dialogView.findViewById(R.id.inactive_check);
        lostToFollowUpImageView = (ImageView)dialogView.findViewById(R.id.lost_to_followup_check);

        activeImageView.setVisibility(View.INVISIBLE);
        inactiveImageView.setVisibility(View.INVISIBLE);
        lostToFollowUpImageView.setVisibility(View.INVISIBLE);

        if((!details.containsKey(inactive))||(details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(""))){
            activeImageView.setVisibility(View.VISIBLE);
            inactiveImageView.setVisibility(View.INVISIBLE);
            lostToFollowUpImageView.setVisibility(View.INVISIBLE);
        }
        if(details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.FALSE.toString())){
            activeImageView.setVisibility(View.VISIBLE);
            inactiveImageView.setVisibility(View.INVISIBLE);
            lostToFollowUpImageView.setVisibility(View.INVISIBLE);

        }
        if(details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.TRUE.toString())){
            inactiveImageView.setVisibility(View.VISIBLE);
            activeImageView.setVisibility(View.INVISIBLE);
            lostToFollowUpImageView.setVisibility(View.INVISIBLE);

        }
        if(details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString())){
             lostToFollowUpImageView.setVisibility(View.VISIBLE);
             inactiveImageView.setVisibility(View.INVISIBLE);
             activeImageView.setVisibility(View.INVISIBLE);

        }





        activeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.TRUE.toString()))||(details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString()))) {
                    ((StatusChangeListener)context).updateClientAttribute(inactive, false);
                    if (details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, false);
                    }
                    activeImageView.setVisibility(View.VISIBLE);
                    inactiveImageView.setVisibility(View.INVISIBLE);
                    lostToFollowUpImageView.setVisibility(View.INVISIBLE);

                } else if(details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString())) {
                    ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, false);
                    ((StatusChangeListener)context).updateClientAttribute(inactive, false);

                    activeImageView.setVisibility(View.VISIBLE);
                inactiveImageView.setVisibility(View.INVISIBLE);
                lostToFollowUpImageView.setVisibility(View.INVISIBLE);

                }else{

                }
                ((StatusChangeListener)context).updateStatus();
                dismiss();
            }
        });


        inactiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.FALSE.toString()))||(details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(""))) {
                    ((StatusChangeListener)context).updateClientAttribute(inactive, true);
                    if (details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, false);
                    }
                    activeImageView.setVisibility(View.INVISIBLE);
                    inactiveImageView.setVisibility(View.VISIBLE);
                    lostToFollowUpImageView.setVisibility(View.INVISIBLE);

                } else if (!details.containsKey(inactive)){
                    ((StatusChangeListener)context).updateClientAttribute(inactive, true);
                    if (details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, false);
                    }
                    activeImageView.setVisibility(View.INVISIBLE);
                    inactiveImageView.setVisibility(View.VISIBLE);
                    lostToFollowUpImageView.setVisibility(View.INVISIBLE);

                }else{

                }
                ((StatusChangeListener)context).updateStatus();
                dismiss();
            }
        });
        LostToFollowUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(Boolean.FALSE.toString()))||(details.containsKey(lostToFollowUp) && details.get(lostToFollowUp).equalsIgnoreCase(""))) {
                    ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, true);
                    if (details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        ((StatusChangeListener)context).updateClientAttribute(inactive, false);
                    }
                    activeImageView.setVisibility(View.INVISIBLE);
                    inactiveImageView.setVisibility(View.INVISIBLE);
                    lostToFollowUpImageView.setVisibility(View.VISIBLE);

                } else if (!details.containsKey(lostToFollowUp)){
                    ((StatusChangeListener)context).updateClientAttribute(lostToFollowUp, true);
                    if (details.containsKey(inactive) && details.get(inactive).equalsIgnoreCase(Boolean.TRUE.toString())) {
                        ((StatusChangeListener)context).updateClientAttribute(inactive, false);
                    }
                    activeImageView.setVisibility(View.INVISIBLE);
                    inactiveImageView.setVisibility(View.INVISIBLE);
                    lostToFollowUpImageView.setVisibility(View.VISIBLE);

                }else{

                }
                ((StatusChangeListener)context).updateStatus();
                dismiss();
            }
        });


        return dialogView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface

    }



    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            }
        });

    }
}
