package org.smartregister.indonesia.child;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;

import org.smartregister.Context;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.indonesia.R;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.ImageFetcher;
import util.growthChart.GrowthChartGenerator;

//import org.smartregister.gizi.face.camera.SmartShutterActivity;

/**
 * Created by Iq on 26/04/16.
 */
public class ChildDetailActivity extends Activity {
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
  //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    //image retrieving

    public static CommonPersonObjectClient childclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.wfa_chart_layout);
        String DetailStart = timer.format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("start", DetailStart);
                FlurryAgent.logEvent("gizi_detail_view", Detail, true);

        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);
        TextView navBarGrowthChart = (TextView)findViewById(R.id.chart_navbar_growth_chart);
        TextView navBarzScore= (TextView)findViewById(R.id.chart_navbar_z_score);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ChildDetailActivity.this, AnakDetailActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("gizi_detail_view", Detail, true);
            }
        });

        navBarGrowthChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChildGrowthChartActivity.client = childclient;
                startActivity(new Intent(ChildDetailActivity.this, ChildGrowthChartActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        navBarzScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChildZScoreChartActivity.client = childclient;
                startActivity(new Intent(ChildDetailActivity.this, ChildZScoreChartActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DetailsRepository detailsRepository = Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        System.out.println("columnmaps: " + childclient.getColumnmaps().toString());
        System.out.println("details: "+childclient.getDetails().toString());

        AllCommonsRepository childRepository = Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

        String[]data = childclient.getDetails().get("history_berat")!= null ? split(childclient.getDetails().get("history_berat")) : new String[]{"0","0"};
        String berats = data[1];
        String[] history_berat = berats.split(",");
        String tempUmurs = data[0];
        String[] history_umur = tempUmurs.split(",");
        String umurs="";
        for(int i=0;i<history_umur.length;i++){
            umurs = umurs + "," + Integer.toString(Integer.parseInt(history_umur[i])/30);
        }

        Log.logInfo("Berat :" +berats);
        Log.logInfo("umurs :" +umurs.substring(1,umurs.length()));
        GraphView graph = (GraphView) findViewById(R.id.graph);
        new GrowthChartGenerator(
                graph,
                childclient.getDetails().get("gender"),
                (childclient.getDetails().get("tanggalLahirAnak").length() > 10
                    ? childclient.getDetails().get("tanggalLahirAnak").substring(0,10)
                    : childclient.getDetails().get("tanggalLahirAnak")),
                umurs.substring(1,umurs.length()),berats);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return super.formatLabel(value, isValueX )+ " Month";
                else
                    return super.formatLabel(value, isValueX) + " Kg";
            }

        });
    }

    private String[]split(String data){
        if(!data.contains(":"))
            return new String[]{"0","0"};
        String []temp = data.split(",");
        String []result = {"",""};
        for(int i=0;i<temp.length;i++){
            result[0]=result[0]+","+temp[i].split(":")[0];
            result[1]=result[1]+","+temp[i].split(":")[1];
        }
        result[0]=result[0].substring(1,result[0].length());
        result[1]=result[1].substring(1,result[1].length());
        return result;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIAnakSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }
}
