package dashboard.opensrp.org.jandjdashboard.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dashboard.opensrp.org.jandjdashboard.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class scheduleCardAdapter extends RecyclerView.Adapter<scheduleCardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Drawable> drawableList;
    private List<String> cardtitles;
    private List<String> counts;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public scheduleCardAdapter(Context mContext, List<Drawable> albumList, List<String> cardtitles, List<String> counts) {
        this.mContext = mContext;
        this.drawableList = albumList;
        this.cardtitles = cardtitles;
        this.counts = counts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_schedules_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Drawable album = drawableList.get(position);
        String title = cardtitles.get(position);
        holder.thumbnail.setImageDrawable(album);
        if(counts.get(position).equalsIgnoreCase("N/A")){
            holder.count.setTextColor(mContext.getResources().getColor(R.color.unfocuseddatemonthcolor));
            holder.title.setTextColor(mContext.getResources().getColor(R.color.unfocuseddatemonthcolor));
        }
        holder.count.setText(title);
        holder.title.setText(counts.get(position));
//        holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library



    }




    @Override
    public int getItemCount() {
        return drawableList.size();
    }
}
