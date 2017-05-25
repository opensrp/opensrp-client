package com.vijay.jsonwizard.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;
import com.vijay.jsonwizard.widgets.DatePickerFactory;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.dpToPixels;

/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>> {
    private static final String TAG = "FormFragmentPresenter";
    private static final int RESULT_LOAD_IMG = 1;
    private String mStepName;
    private JSONObject mStepDetails;
    private String mCurrentKey;
    private String mCurrentPhotoPath;
    private JsonFormInteractor mJsonFormInteractor;
    private final JsonFormFragment formFragment;

    public JsonFormFragmentPresenter(JsonFormFragment formFragment) {
        this.formFragment = formFragment;
        mJsonFormInteractor = JsonFormInteractor.getInstance();
    }

    public JsonFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        this(formFragment);
        mJsonFormInteractor = jsonFormInteractor;
    }

    public void addFormElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<View> views = mJsonFormInteractor.fetchFormElements(mStepName, formFragment, mStepDetails,
                getView().getCommonListener());
        getView().addFormElements(views);

    }

    @SuppressLint("ResourceAsColor")
    public void setUpToolBar() {
        if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
            getView().setUpBackButton();
        }
        getView().setActionBarTitle(mStepDetails.optString("title"));
        if (mStepDetails.has("next")) {
            getView().updateVisibilityOfNextAndSave(true, false);
        } else {
            getView().updateVisibilityOfNextAndSave(false, true);
        }
        getView().setToolbarTitleColor(R.color.white);
    }

    public void onBackClick() {
        getView().hideKeyBoard();
        getView().backClick();
    }

    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = JsonFormFragment.getFormFragment(mStepDetails.optString("next"));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            validationStatus.requestAttention();
            getView().showToast(validationStatus.getErrorMessage());
        }
    }

    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) {
        ValidationStatus firstError = null;
        for (View childAt : formFragment.getJsonApi().getFormDataViews()) {
            String key = (String) childAt.getTag(R.id.key);
            String openMrsEntityParent = (String) childAt.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) childAt.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) childAt.getTag(R.id.openmrs_entity_id);

            ValidationStatus validationStatus = validate(getView(), childAt, firstError == null);
            if (firstError == null && !validationStatus.isValid()) {
                firstError = validationStatus;
            }

            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;

                String rawValue = (String) editText.getTag(R.id.raw_value);
                if (rawValue == null) {
                    rawValue = editText.getText().toString();
                }

                getView().writeValue(mStepName, key, rawValue,
                        openMrsEntityParent, openMrsEntity, openMrsEntityId);
            } else if (childAt instanceof ImageView) {
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getView().writeValue(mStepName, key, (String) path, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()), openMrsEntityParent,
                        openMrsEntity, openMrsEntityId);
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                            openMrsEntity, openMrsEntityId);
                }
            } else if (childAt instanceof MaterialSpinner) {
            }
        }

        if (firstError == null) {
            return new ValidationStatus(true, null, null, null);
        } else {
            return firstError;
        }
    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView, View childAt, boolean requestFocus) {
        if (childAt instanceof MaterialEditText) {
            MaterialEditText editText = (MaterialEditText) childAt;
            ValidationStatus validationStatus = EditTextFactory.validate(formFragmentView, editText);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                return validationStatus;
            }
        } else if (childAt instanceof ImageView) {
            ValidationStatus validationStatus = ImagePickerFactory.validate(formFragmentView,
                    (ImageView) childAt);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                return validationStatus;
            }
        } else if (childAt instanceof MaterialSpinner) {
            MaterialSpinner spinner = (MaterialSpinner) childAt;
            ValidationStatus validationStatus = SpinnerFactory.validate(formFragmentView, spinner);
            if (!validationStatus.isValid()) {
                if (requestFocus) validationStatus.requestAttention();
                spinner.setError(validationStatus.getErrorMessage());
                return validationStatus;
            } else {
                spinner.setError(null);
            }
        }

        return new ValidationStatus(true, null, null, null);
    }

    public void onSaveClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            Intent returnIntent = new Intent();
            getView().onFormFinish();
            returnIntent.putExtra("json", getView().getCurrentJsonState());
            getView().finishWithResult(returnIntent);
        } else {
            Toast.makeText(getView().getContext(), validationStatus.getErrorMessage(), Toast.LENGTH_LONG);
            validationStatus.requestAttention();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            String imagePath = mCurrentPhotoPath;
            getView().updateRelevantImageView(ImageUtils.loadBitmapFromFile(getView().getContext(), imagePath, ImageUtils.getDeviceWidth(getView().getContext()), dpToPixels(getView().getContext(), 200)), imagePath, mCurrentKey);
            //cursor.close();
        }
    }

    public void onClick(View v) {
        String key = (String) v.getTag(R.id.key);
        String type = (String) v.getTag(R.id.type);
        if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
            getView().hideKeyBoard();
            mCurrentKey = key;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getView().getContext().getPackageManager()) != null) {
                File imageFile = null;
                try {
                    imageFile = createImageFile();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }

                if (imageFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getView().getContext(),
                            "com.vijay.jsonwizard.fileprovider",
                            imageFile);

                    // Grant permission to the default camera app
                    PackageManager packageManager = getView().getContext().getPackageManager();
                    Context applicationContext = getView().getContext().getApplicationContext();

                    applicationContext.grantUriPermission(
                            takePictureIntent.resolveActivity(packageManager).getPackageName(),
                            photoURI,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    applicationContext.grantUriPermission(
                            "com.vijay.jsonwizard",
                            photoURI,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    getView().startActivityForResult(takePictureIntent, RESULT_LOAD_IMG);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getView().getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged called");
        if (compoundButton instanceof CheckBox) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String openMrsEntityParent = (String) compoundButton.getTag(R.id.openmrs_entity_parent);
            String openMrsEntity = (String) compoundButton.getTag(R.id.openmrs_entity);
            String openMrsEntityId = (String) compoundButton.getTag(R.id.openmrs_entity_id);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                    String.valueOf(((CheckBox) compoundButton).isChecked()), openMrsEntityParent,
                    openMrsEntity, openMrsEntityId);
        } else if (compoundButton instanceof RadioButton) {
            if (isChecked) {
                String parentKey = (String) compoundButton.getTag(R.id.key);
                String openMrsEntityParent = (String) compoundButton.getTag(R.id.openmrs_entity_parent);
                String openMrsEntity = (String) compoundButton.getTag(R.id.openmrs_entity);
                String openMrsEntityId = (String) compoundButton.getTag(R.id.openmrs_entity_id);
                String childKey = (String) compoundButton.getTag(R.id.childKey);
                getView().unCheckAllExcept(parentKey, childKey);
                getView().writeValue(mStepName, parentKey, childKey, openMrsEntityParent,
                        openMrsEntity, openMrsEntityId);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        String openMrsEntityParent = (String) parent.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) parent.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) parent.getTag(R.id.openmrs_entity_id);
        if (position >= 0) {
            String value = (String) parent.getItemAtPosition(position);
            getView().writeValue(mStepName, parentKey, value, openMrsEntityParent, openMrsEntity,
                    openMrsEntityId);
        }
    }
}
