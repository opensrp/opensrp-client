package org.smartregister.indonesia.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by sid on 4/25/17.
 */
public class Receiver extends BroadcastReceiver {

    public Receiver(){

    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Action Broadcast"+ intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
