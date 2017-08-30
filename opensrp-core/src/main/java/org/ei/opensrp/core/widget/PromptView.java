package org.ei.opensrp.core.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.R;
import org.ei.opensrp.core.utils.Utils;

/**
 * Created by Maimoona on 1/12/2017.
 */

public class PromptView {
    private final AlertDialog dialog;
    private final EditText confirm;
    private final AlertDialog.Builder builder;
    private final EditText input;

    public String inputValue(){
        return input.getText().toString();
    }

    public String confirmValue(){
        return confirm.getText().toString();
    }

    private Button getPositiveButton(){
        return dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    }

    public PromptView(@NonNull Context context, String label, String goButtonText, String cancelButtonText, final String validatorRegex, boolean confirmValue, DialogInterface.OnClickListener goButtonListener) {
        builder = new AlertDialog.Builder(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(label);

        layout.addView(input);

        confirm = new EditText(context);
        confirm.setInputType(InputType.TYPE_CLASS_TEXT);
        confirm.setHint("Re-enter to confirm value");

        layout.addView(confirm);

        builder.setView(layout);

        builder.setPositiveButton(goButtonText, goButtonListener);

        builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();

        TextWatcher tw = createTextWatcher(validatorRegex);
        input.addTextChangedListener(tw);
        confirm.addTextChangedListener(tw);
    }

    public void show() {
        dialog.show();
        input.setText("");
        confirm.setText("");
        getPositiveButton().setEnabled(false);
    }

    private TextWatcher createTextWatcher(final String validatorRegex){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetViewColors(validatorRegex);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                resetViewColors(validatorRegex);
            }
        };
    }

    private void resetViewColors(String validatorRegex){
        if (StringUtils.isBlank(inputValue()) || !inputValue().matches(validatorRegex)){
            inputValue().length();
            getPositiveButton().setEnabled(false);
            input.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_red_light));
            confirm.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_white));
        }
        else if(!inputValue().equalsIgnoreCase(confirmValue())){
            getPositiveButton().setEnabled(false);
            input.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_white));
            confirm.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_red_light));
        }
        else {
            getPositiveButton().setEnabled(true);
            input.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_white));
            confirm.setBackgroundColor(Utils.getColor(dialog.getContext(), R.color.dull_white));
        }
    }
}
