package layout.face.sidface.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wildan on 12/18/16.
 */
public class Storage {

    public int getNumId(Context context) {
        if (context == null)
            throw new RuntimeException ("Context is null, what are you doing?");
        SharedPreferences settings = context.getSharedPreferences(AppConstant.HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());
        return hash.size();

    }
}
