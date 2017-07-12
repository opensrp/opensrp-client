package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by Jason Rogena - jrogena@ona.io on 06/03/2017.
 */

public class MinNumericValidator extends METValidator {
    private final double minValue;

    public MinNumericValidator(@NonNull String errorMessage, @NonNull double minValue) {
        super(errorMessage);
        this.minValue = minValue;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        if (!isEmpty) {
            try {
                if (Double.parseDouble(text.toString()) < minValue) return false;
            } catch (Exception e) {
            }
        }
        return true;
    }
}
