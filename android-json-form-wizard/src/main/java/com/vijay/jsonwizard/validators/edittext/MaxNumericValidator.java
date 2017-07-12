package com.vijay.jsonwizard.validators.edittext;

import android.support.annotation.NonNull;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by Jason Rogena - jrogena@ona.io on 06/03/2017.
 */

public class MaxNumericValidator extends METValidator {
    private final double maxValue;

    public MaxNumericValidator(@NonNull String errorMessage, @NonNull double maxValue) {
        super(errorMessage);
        this.maxValue = maxValue;
    }

    @Override
    public boolean isValid(@NonNull CharSequence text, boolean isEmpty) {
        if (!isEmpty) {
            try {
                if (Double.parseDouble(text.toString()) > maxValue) return false;
            } catch (Exception e) {
            }
        }
        return true;
    }
}
