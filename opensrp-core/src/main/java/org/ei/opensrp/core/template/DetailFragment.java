package org.ei.opensrp.core.template;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.core.R;
import org.ei.opensrp.core.db.repository.RegisterRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.ei.opensrp.core.utils.Utils.*;

public abstract class DetailFragment extends Fragment {
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;
    static File currentPhoto;
    static ImageView mImageView;

    protected CommonPersonObjectClient client;

    protected abstract int layoutResId();

    protected abstract String pageTitle();

    protected abstract String titleBarId();

    protected abstract void generateView();

    protected abstract Integer profilePicContainerId();

    protected abstract Integer defaultProfilePicResId();

    protected abstract String bindType();

    protected abstract boolean allowImageCapture();

    protected View currentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting view
        View view = inflater.inflate(layoutResId(), container, false);

        currentView = view;

        return view;
    }

    public void resetView(CommonPersonObjectClient client){
        this.client = client;

        onResumeFragmentView();

        displayPicture();

        ((TextView)currentView.findViewById(R.id.detail_heading)).setText(pageTitle());

        ((TextView)currentView.findViewById(R.id.details_id_label)).setText(titleBarId());

        ((TextView)currentView.findViewById(R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        generateView();

        ImageButton back = (ImageButton)currentView.findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });//todo
    }

    public void onResumeFragmentView(){

    }

    public void displayPicture() {
        if (profilePicContainerId() != null) {
            mImageView = (ImageView) currentView.findViewById(profilePicContainerId());
            if (allowImageCapture()) {
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent(mImageView);
                    }
                });


                ProfileImage photo = RegisterRepository.findImageByEntityId(bindType(), client.entityId(), "dp");

                if (photo != null) {
                    setProfiePicFromPath(getActivity(), mImageView, photo.getFilepath(), R.drawable.pencil);
                } else {
                    setProfiePic(getActivity(), mImageView, defaultProfilePicResId(), R.drawable.pencil);
                }
            } else {
                setProfiePic(getActivity(), mImageView, defaultProfilePicResId(), null);
            }
        }
    }

    //todo refactor
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = bindType()+ "_"+timeStamp + "_"+client.entityId();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void saveImageReference(String bindobject, String entityid, Map<String, String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid, details);
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),
                Context.getInstance().anmService().fetchDetails().name(), entityid,
                "Image",details.get("profilepic"), ImageRepository.TYPE_Unsynced,"dp");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
    }

    private void dispatchTakePictureIntent(ImageView imageView) {
        this.mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                currentPhoto = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (currentPhoto != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhoto));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == getActivity().RESULT_OK) {
            HashMap<String,String> details = new HashMap<String,String>();
            details.put("profilepic", currentPhoto.getAbsolutePath());
            saveImageReference(bindType(), client.entityId(), details);
            setProfiePicFromPath(getActivity(), mImageView, currentPhoto.getAbsolutePath(), R.drawable.pencil);
        }
    }
}
