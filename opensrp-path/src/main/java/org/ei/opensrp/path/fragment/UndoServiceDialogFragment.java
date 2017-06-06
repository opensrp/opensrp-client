package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.ServiceWrapper;
import org.ei.opensrp.path.listener.ServiceActionListener;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;

import java.io.Serializable;

import util.ImageUtils;

@SuppressLint("ValidFragment")
public class UndoServiceDialogFragment extends DialogFragment {
    private ServiceWrapper tag;
    private ServiceActionListener listener;
    public static final String DIALOG_TAG = "UndoServiceDialogFragment";
    public static final String WRAPPER_TAG = "tag";

    public static UndoServiceDialogFragment newInstance(
            ServiceWrapper tag) {

        UndoServiceDialogFragment undoServiceDialogFragment = new UndoServiceDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(WRAPPER_TAG, tag);
        undoServiceDialogFragment.setArguments(args);

        return undoServiceDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Serializable serializable = bundle.getSerializable(WRAPPER_TAG);
        if (serializable != null && serializable instanceof ServiceWrapper) {
            tag = (ServiceWrapper) serializable;
        }

        if (tag == null) {
            return null;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.undo_vaccination_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tag.getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tag.getPatientNumber());

        TextView textView = (TextView) dialogView.findViewById(R.id.vaccine);
        textView.setText(tag.getName());


        if (tag.getId() != null) {
            ImageView mImageView = (ImageView) dialogView.findViewById(R.id.child_profilepic);
            if (tag.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(org.ei.opensrp.R.id.entity_id, tag.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(tag.getId(), OpenSRPImageLoader.getStaticImageListener((ImageView) mImageView, ImageUtils.profileImageResourceByGender(tag.getGender()), ImageUtils.profileImageResourceByGender(tag.getGender())));
            }
        }

        TextView undoText = (TextView) dialogView.findViewById(R.id.undo_text);
        undoText.setText(getString(R.string.undo_service));

        Button serviceToday = (Button) dialogView.findViewById(R.id.yes_undo);
        serviceToday.setText(getString(R.string.yes_undo_service));

        serviceToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                listener.onUndoService(tag, view);
            }
        });

        Button cancel = (Button) dialogView.findViewById(R.id.no_go_back);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialogView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ServiceActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceActionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = getDialog().getWindow();
                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                int width = size.x;

                window.setLayout((int) (width * 0.7), FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
            }
        });

    }
}
