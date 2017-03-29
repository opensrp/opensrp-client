package org.ei.opensrp.gizi.face.camera.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.gizi.R;

/**
 * Created by wildan on 1/16/17.
 */
public class ClientAdapter extends BaseAdapter {
    private static final String TAG = ClientAdapter.class.getCanonicalName();
    private Context mContext;
    String[] mNames;
    private Activity context;

    public ClientAdapter(Context context, String[] names) {
        mContext = context;
        mNames = names;
    }

    @Override
    public int getCount() {
        return mNames.length;
    }

    @Override
    public Object getItem(int position) {
        return mNames[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridview;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            gridview = new View(mContext);
            gridview = inflater.inflate(R.layout.fr_base_id_clients, null);
        } else{
            gridview = convertView;
        }

        TextView tv = (TextView) gridview.findViewById(R.id.tv_baseid);
        tv.setBackgroundColor(Color.BLACK);
        tv.setText(" " + (position + 1) + ". " + mNames[position]);

        ImageView delete = (ImageView) gridview.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove?");
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return gridview;
    }
}
