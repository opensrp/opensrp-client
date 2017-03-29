package org.ei.opensrp.gizi.face.camera.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.gizi.LoginActivity;
import org.ei.opensrp.gizi.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sid on 2/2/17.
 */
public class DebugApp {

    private static final String TAG = DebugApp.class.getSimpleName();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void debugApp(LayoutInflater layoutInflater, Context context){
        View view = layoutInflater.inflate(R.layout.login, null);

        Log.e(TAG, "debugApp: "+context );
        LoginActivity l = new LoginActivity();
        Class[] aArg = new Class[1];
        String[] value = {"ec_bidan", "Satu2345"};
        Method mLocal = null, mRemote = null;

//        ProgressDialog pd = new ProgressDialog(l);
        try {
            Field p = LoginActivity.class.getDeclaredField("progressDialog");
            Field f = LoginActivity.class.getDeclaredField("context");
            mLocal = LoginActivity.class.getDeclaredMethod("localLogin", View.class, String.class, String.class);
            mRemote = LoginActivity.class.getDeclaredMethod("remoteLogin", View.class, String.class, String.class);
            mLocal.setAccessible(true);
            mRemote.setAccessible(true);
            f.setAccessible(true);
            p.setAccessible(true);

            if (context.userService().hasARegisteredUser()){
                Log.e(TAG, "debugApp: " + "mLocal");
                f.set(l, context);
//                p.set(l, pd);
                mLocal.invoke(l, view, "ec_bidan", "Satu2345");
            } else {
                f.set(l, context);
//                p.set(l, pd);
                Log.e(TAG, "debugApp: "+"mRemote" );
                mRemote.invoke(l, view, "ec_bidan", "Satu2345");
            }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
