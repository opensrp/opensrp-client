package org.ei.opensrp.path.wrapper;

import android.database.Cursor;
import android.view.View;

import org.ei.opensrp.view.contract.SmartRegisterClient;

/**
 * Created by onaio on 14/09/2017.
 */

public class BaseViewRecordUpdateWrapper {

    private View convertView;
    private String inactive;
    private SmartRegisterClient client;
    private Cursor cursor;

    private String lostToFollowUp;

    public View getConvertView() {
        return convertView;
    }

    public void setConvertView(View convertView) {
        this.convertView = convertView;
    }

    public String getLostToFollowUp() {
        return lostToFollowUp;
    }

    public void setLostToFollowUp(String lostToFollowUp) {
        this.lostToFollowUp = lostToFollowUp;
    }

    public String getInactive() {
        return inactive;
    }

    public void setInactive(String inactive) {
        this.inactive = inactive;
    }

    public SmartRegisterClient getClient() {
        return client;
    }

    public void setClient(SmartRegisterClient client) {
        this.client = client;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
