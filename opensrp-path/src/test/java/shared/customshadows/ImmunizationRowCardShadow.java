package shared.customshadows;

import android.content.Context;
import android.widget.Button;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.view.ImmunizationRowCard;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowLinearLayout;

import java.util.Date;

import shared.VaccinatorApplicationTestVersion;

/**
 * Created by coder on 06/07/2017.
 */

@Implements(ImmunizationRowCard.class)
public class ImmunizationRowCardShadow extends ShadowLinearLayout {


    public static enum State {
        DONE_CAN_BE_UNDONE,
        DONE_CAN_NOT_BE_UNDONE,
        DUE,
        NOT_DUE,
        OVERDUE,
        EXPIRED
    }


    @Implementation
    private void init(Context context) {
    }

    @Implementation
    public void setVaccineWrapper(VaccineWrapper vaccineWrapper) {
    }

    @Implementation
    public VaccineWrapper getVaccineWrapper() {
        return null;
    }

    @Implementation
    public void updateState() {
    }

    @Implementation
    public void setOnVaccineStateChangeListener(OnVaccineStateChangeListener onVaccineStateChangeListener) {
    }

    @Implementation
    public ImmunizationRowCard.State getState() {
        return ImmunizationRowCard.State.DUE;
    }

    @Implementation
    private void updateStateUi() {
    }

    @Implementation
    private String getVaccineName() {
        return null;
    }

    @Implementation
    private Date getDateDue() {
        return null;
    }

    @Implementation
    private Date getDateDone() {
        return null;
    }

    @Implementation
    private boolean isSynced() {
        return false;
    }

    @Implementation
    private Alert getAlert() {
        return null;
    }

    @Implementation
    private String getStatus() {
        return null;
    }

    public static interface OnVaccineStateChangeListener {
        void onStateChanged(final State newState);
    }

    @Implementation
    public Button getUndoB() {
        return new Button(VaccinatorApplicationTestVersion.getInstance().getApplicationContext());
    }
}
