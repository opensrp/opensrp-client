package org.ei.opensrp.domain;

import android.util.Log;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;

import static java.text.MessageFormat.format;

public class ProfileImage {
    private String imageid;
    private String anmId;
    private String entityID;
    private String contenttype;
    private String filepath;
    private String syncStatus;
    private String filecategory;
    private String vectorid;
    private String filevector;
    private String fFaceVectorApi;

    public ProfileImage(String imageid, String anmId, String entityID, String contenttype, String filepath, String syncStatus, String filecategory, String filevector) {
        this.imageid = imageid;
        this.entityID = entityID;
        this.anmId = anmId;
        this.contenttype = contenttype;
        this.filepath = filepath;
        this.syncStatus = syncStatus;
        this.filecategory = filecategory;
        this.filevector = filevector;
    }

    public ProfileImage(String imageid, String anmId, String entityID, String contenttype, String filepath, String syncStatus, String filecategory) {
        this.imageid = imageid;
        this.entityID = entityID;
        this.anmId = anmId;
        this.contenttype = contenttype;
        this.filepath = filepath;
        this.syncStatus = syncStatus;
        this.filecategory = filecategory;
    }

    public ProfileImage(String vectorid,String entityID, String syncStatus) {
        this.vectorid = vectorid;
        this.entityID = entityID;
        this.syncStatus = syncStatus;
    }


    public ProfileImage(){}

    public String getFilecategory() {
        return filecategory;
    }

    public void setFilecategory(String filecategory) {
        this.filecategory = filecategory;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getImageUrl() {
        String url = format("{0}/{1}/{2}",
                Context.getInstance().allSharedPreferences().fetchBaseURL(""),
                AllConstants.PROFILE_IMAGES_DOWNLOAD_PATH, entityID);
        return url;
    }


    public String getFilevector() {
//        Log.e("ProfileImage", "getFilevector: "+filevector );
//        String[] splitStringArray = filevector.substring(1,
//                filevector.length() - 1).split(", ");
//
//        byte[] tempFileVector = new byte[splitStringArray.length];
//        Log.e("TAG", "parseSavedVector: Parsing Data from DB"+ splitStringArray.length );
//        for (int i = 0; i < splitStringArray.length; i++) {
//            tempFileVector[i] = Byte.parseByte(splitStringArray[i]);
//        }

        return filevector;
    }

    public void setFilevector(String filevector) {
        this.filevector = filevector;
    }

    public String getfFaceVectorApi(Context context, String entityId) {

        String  DRISTHI_BASE_URL = context.configuration().dristhiBaseURL().replace("opensrp","openmrs");
        String api_url = DRISTHI_BASE_URL+ "/multimedia-file?anm-id=user28";



        fFaceVectorApi = api_url;

        return fFaceVectorApi;
    }
}
