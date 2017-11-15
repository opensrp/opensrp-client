package org.ei.opensrp.service;

import android.content.res.AssetManager;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raihan Ahmed on 10/14/15.
 */
public class ImageUploadSyncService {


    private  ImageRepository imageRepository;



    public ImageUploadSyncService(ImageRepository imageRepository) {

        this.imageRepository = imageRepository;
        ImageSync((ArrayList<ProfileImage>) imageRepository.allProfileImages(),imageRepository);
    }
    public void ImageSync(ArrayList<ProfileImage> profileimages,ImageRepository imagerepository){
        for(int i = 0;i<profileimages.size();i++){
            String response = Context.getInstance().getHttpAgent().httpImagePost(Context.getInstance().configuration().dristhiBaseURL()+"/multimedia-file",profileimages.get(i));
            imagerepository.close(profileimages.get(i).getImageid());
        }
    }



}