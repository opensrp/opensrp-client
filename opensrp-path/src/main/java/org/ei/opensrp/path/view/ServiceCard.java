package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.ServiceWrapper;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import util.DisplayUtils;

/**
 * Created by keyman on 21/02/2017.
 */

public class ServiceCard extends LinearLayout {
    private static final String TAG = "ServiceCard";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("dd/MM");
    private Context context;
    private ImageView statusIV;
    private TextView nameTV;
    private Button undoB;
    private State state;
    private OnServiceStateChangeListener onServiceStateChangeListener;
    private ServiceWrapper serviceWrapper;

    public static enum State {
        DONE_CAN_BE_UNDONE,
        DONE_CAN_NOT_BE_UNDONE,
        DUE,
        NOT_DUE,
        OVERDUE,
        EXPIRED
    }

    public ServiceCard(Context context) {
        super(context);
        init(context);
    }

    public ServiceCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ServiceCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ServiceCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_vaccination_card, this, true);
        statusIV = (ImageView) findViewById(R.id.status_iv);
        nameTV = (TextView) findViewById(R.id.name_tv);
        undoB = (Button) findViewById(R.id.undo_b);
    }

    public void setServiceWrapper(ServiceWrapper serviceWrapper) {
        this.serviceWrapper = serviceWrapper;
        updateState();
    }

    public ServiceWrapper getServiceWrapper() {
        return this.serviceWrapper;
    }

    public void updateState() {
        this.state = State.NOT_DUE;
        if (serviceWrapper != null) {
            Date dateDone = getDateDone();
            boolean isSynced = isSynced();
            String status = getStatus();

            if (dateDone != null) {// Vaccination was done
                if (isSynced) {
                    this.state = State.DONE_CAN_NOT_BE_UNDONE;
                } else {
                    this.state = State.DONE_CAN_BE_UNDONE;
                }
            } else {// Vaccination has not been done
                if (status != null) {
                    if (status.equalsIgnoreCase("due")) {
                        Alert alert = getAlert();
                        if (alert == null) {
                            //state = State.NO_ALERT;
                        } else if (alert.status().value().equalsIgnoreCase("normal")) {
                            state = State.DUE;
                        } else if (alert.status().value().equalsIgnoreCase("upcoming")) {
                            //state = State.UPCOMING;
                        } else if (alert.status().value().equalsIgnoreCase("urgent")) {
                            state = State.OVERDUE;
                        } else if (alert.status().value().equalsIgnoreCase("expired")) {
                            state = State.EXPIRED;
                        }
                    } else if (serviceWrapper.getStatus().equalsIgnoreCase("expired")) {
                        state = State.EXPIRED;
                    }
                }

                /*
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                if (dateDue.getTime() > (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))) {
                    // Vaccination due more than one day from today
                    this.state = State.NOT_DUE;
                } else if (dateDue.getTime() < (today.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))) {
                    // Vaccination overdue
                    this.state = State.OVERDUE;
                } else {
                    this.state = State.DUE;
                } */
            }
        }

        updateStateUi();
    }

    public void setOnServiceStateChangeListener(OnServiceStateChangeListener onServiceStateChangeListener) {
        this.onServiceStateChangeListener = onServiceStateChangeListener;
    }

    public State getState() {
        updateState();
        return this.state;
    }

    private void updateStateUi() {
        setVisibility(VISIBLE);
        switch (state) {
            case NOT_DUE:
                setBackgroundResource(R.drawable.vaccine_card_background_white);
                statusIV.setVisibility(GONE);
                undoB.setVisibility(GONE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(R.color.silver));
                nameTV.setText(getServiceName());
                setClickable(false);
                setVisibility(GONE);
                break;
            case DUE:
                setBackgroundResource(R.drawable.vaccine_card_background_blue);
                statusIV.setVisibility(GONE);
                undoB.setVisibility(GONE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(android.R.color.white));
                nameTV.setText(String.format(context.getString(R.string.record_), getServiceName()));
                setClickable(true);
                break;
            case DONE_CAN_BE_UNDONE:
                setBackgroundResource(R.drawable.vaccine_card_background_white);
                statusIV.setVisibility(VISIBLE);
                undoB.setVisibility(VISIBLE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(R.color.silver));

                SimpleDateFormat dateFormatToUse = SHORT_DATE_FORMAT;
                if (DisplayUtils.getScreenSize((Activity) context) > 7.2) {
                    dateFormatToUse = DATE_FORMAT;
                }

                nameTV.setText(getServiceName() + " - " + dateFormatToUse.format(getDateDone()));
                setClickable(false);
                break;
            case DONE_CAN_NOT_BE_UNDONE:
                setBackgroundResource(R.drawable.vaccine_card_background_white);
                statusIV.setVisibility(VISIBLE);
                undoB.setVisibility(GONE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(R.color.silver));
                nameTV.setText(getServiceName() + " - " + DATE_FORMAT.format(getDateDone()));
                setClickable(false);
                break;
            case OVERDUE:
                setBackgroundResource(R.drawable.vaccine_card_background_red);
                statusIV.setVisibility(GONE);
                undoB.setVisibility(GONE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(android.R.color.white));
                String serviceName = getServiceName();
                if (getDateDue() != null) {
                    nameTV.setText(String.format(context.getString(R.string.record_due_),
                            serviceName, DATE_FORMAT.format(getDateDue())));
                } else {
                    nameTV.setText(String.format(context.getString(R.string.record_), serviceName));
                }
                setClickable(true);
                break;
            case EXPIRED:
                setBackgroundResource(R.drawable.vaccine_card_background_white);
                statusIV.setVisibility(GONE);
                undoB.setVisibility(GONE);
                nameTV.setVisibility(VISIBLE);
                nameTV.setTextColor(context.getResources().getColor(R.color.silver));
                nameTV.setText("Expired: " + getServiceName());
                setClickable(false);
                break;
        }
    }

    private String getServiceName() {
        if (serviceWrapper != null) {
            return serviceWrapper.getName();
        }
        return null;
    }

    private Date getDateDue() {
        if (serviceWrapper != null) {
            DateTime vaccineDate = serviceWrapper.getVaccineDate();
            if (vaccineDate != null) return vaccineDate.toDate();
        }
        return null;
    }

    private Date getDateDone() {
        if (serviceWrapper != null) {
            DateTime dateDone = serviceWrapper.getUpdatedVaccineDate();
            if (dateDone != null) return dateDone.toDate();
        }

        return null;
    }

    private boolean isSynced() {
        if (serviceWrapper != null) {
            return serviceWrapper.isSynced();
        }
        return false;
    }

    private Alert getAlert() {
        if (serviceWrapper != null) {
            return serviceWrapper.getAlert();
        }
        return null;
    }

    private String getStatus() {
        if (serviceWrapper != null) {
            return serviceWrapper.getStatus();
        }
        return null;
    }

    public static interface OnServiceStateChangeListener {
        void onStateChanged(final State newState);

    }

    public Button getUndoB() {
        return undoB;
    }
}
