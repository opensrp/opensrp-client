package org.ei.drishti.view.viewHolder;

import android.graphics.drawable.Drawable;
import org.ei.drishti.view.contract.ChildSmartRegisterClient;
import org.ei.drishti.view.contract.SmartRegisterClient;

import static org.ei.drishti.AllConstants.FEMALE_GENDER;

public class ChildRegisterProfilePhotoLoader implements ProfilePhotoLoader {
    private final Drawable maleInfantDrawable;
    private final Drawable femaleInfantDrawable;

    public ChildRegisterProfilePhotoLoader(Drawable maleInfantDrawable, Drawable femaleInfantDrawable) {
        this.maleInfantDrawable = maleInfantDrawable;
        this.femaleInfantDrawable = femaleInfantDrawable;
    }

    public Drawable get(SmartRegisterClient client) {
        return FEMALE_GENDER.equalsIgnoreCase(((ChildSmartRegisterClient) client).gender())
                ? femaleInfantDrawable
                : maleInfantDrawable;
    }
}
