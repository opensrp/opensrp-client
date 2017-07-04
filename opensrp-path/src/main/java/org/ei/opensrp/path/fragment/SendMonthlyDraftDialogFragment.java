package org.ei.opensrp.path.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import org.ei.opensrp.path.R;

/**
 * Created by coder on 6/28/17.
 */
public class SendMonthlyDraftDialogFragment extends DialogFragment {
    String month;


    public static SendMonthlyDraftDialogFragment newInstance(String month) {
        SendMonthlyDraftDialogFragment f = new SendMonthlyDraftDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("month", month);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
  //                           Bundle savedInstanceState) {
 //       View v = inflater.inflate(R.layout.dialog_fragment_send_monthly, container, false);
//        View tv = v.findViewById(R.id.text);
//        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
//                + getNameForNum(mNum));
//
//        // Watch for button clicks.
//        Button button = (Button)v.findViewById(R.id.show);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // When button is clicked, call up to owning activity.
//                ((FragmentDialog)getActivity()).showDialog();
//            }
//        });

//        return v;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        AlertDialog.Builder b=  new  AlertDialog.Builder(getActivity())
                .setPositiveButton("Send",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // do something...
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater i = getActivity().getLayoutInflater();

        View v = i.inflate(R.layout.dialog_fragment_send_monthly,null);
        b.setView(v);
        return dialog;
    }
}
