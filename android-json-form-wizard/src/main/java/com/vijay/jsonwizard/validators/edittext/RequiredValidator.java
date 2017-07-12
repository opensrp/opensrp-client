package com.vijay.jsonwizard.validators.edittext;

import android.util.Log;

import com.rengwuxian.materialedittext.validation.METValidator;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class RequiredValidator extends METValidator {

    public RequiredValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(CharSequence charSequence, boolean isEmpty) {
        if(errorMessage.equals("Enter the date that the child was first seen at a health facility for immunization services")) {
            Log.d("RequiredValidation", "Charsequence is " + charSequence.toString());
            Log.d("RequiredValidation", "isEmpty is " + isEmpty);
        }
        return !isEmpty;
    }
}
