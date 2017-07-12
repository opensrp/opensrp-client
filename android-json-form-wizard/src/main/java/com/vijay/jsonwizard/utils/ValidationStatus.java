package com.vijay.jsonwizard.utils;

import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.views.JsonFormFragmentView;

/**
 * Created by vijay.rawat01 on 7/21/15.
 */
public class ValidationStatus {
    boolean isValid;
    String errorMessage;
    JsonFormFragmentView formFragmentView;
    View view;

    public ValidationStatus(boolean isValid, String errorMessage,
                            JsonFormFragmentView formFragmentView, View view) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
        this.formFragmentView = formFragmentView;
        this.view = view;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void requestAttention() {
        if(this.view != null && formFragmentView != null) {
            formFragmentView.scrollToView(view);
        }
    }
}
