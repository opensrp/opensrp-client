package layout.face;

import android.view.View;

import org.ei.opensrp.indonesia.LoginActivity;

import java.lang.reflect.Method;

/**
 * Created by wildan on 1/9/17.
 */
public class MDebug {
    public static void localLogin(View view) throws Exception {
        LoginActivity mLoginActivity = new LoginActivity();
        Class[] argTypes = new Class[]{String[].class};
        Method m = mLoginActivity.getClass().getDeclaredMethod("localLogin", argTypes);
        String[] mainArgs = {"ec_bidan","Satu2345"};
        m.setAccessible(true);
        m.invoke(mLoginActivity, view, mainArgs);
    }
    public static void remoteLogin(View view) throws Exception {
        LoginActivity mLoginActivity = new LoginActivity();
        Class[] argTypes = new Class[]{Object[].class};
        Method m = mLoginActivity.getClass().getDeclaredMethod("remoteLogin", argTypes);
        String[] mainArgs = {"ec_bidan","Satu2345"};
//        Method[] ms = mLoginActivity.getClass().getDeclaredMethods();
//        for (Method m : ms) {
//            Log.e("TAG", "remoteLogin: "+m.getName() );
//        }
        m.setAccessible(true);
        m.invoke(mLoginActivity, view, mainArgs);
    }
}
