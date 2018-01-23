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
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.indonesia.R;

import util.growthChart.GraphConstant;
import util.growthChart.GrowthChartGenerator;

/**
 * Created by Null on 2016-10-25.
 */
public class ChildGrowthChartActivity extends Activity{

    public static CommonPersonObjectClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = Context.getInstance();
        setContentView(R.layout.lfa_chart_layout);
        FlurryAgent.logEvent("Growth_chart_view");

        GraphView lfaGraph = (GraphView)findViewById(R.id.lfa_chart);
        GraphView hfaGraph = (GraphView)findViewById(R.id.hfa_chart);

        TextView layoutName = (TextView)findViewById(R.id.chart_label);
        TextView lfaChartLabel = (TextView)findViewById(R.id.lfa_chart_name);
        TextView hfaChartLabel = (TextView)findViewById(R.id.hfa_chart_name);

        TextView navBarDetails = (TextView)findViewById(R.id.chart_navbar_details);
        TextView navBarZScore = (TextView)findViewById(R.id.chart_navbar_z_score);

        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);

        navBarDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChildDetailActivity.childclient = client;
                startActivity(new Intent(ChildGrowthChartActivity.this, ChildDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        navBarZScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChildZScoreChartActivity.client = client;
                startActivity(new Intent(ChildGrowthChartActivity.this, ChildZScoreChartActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ChildGrowthChartActivity.this, AnakDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        layoutName.setText(context.getStringResource(R.string.chart_title));
        lfaChartLabel.setText(context.getStringResource(R.string.lfa_string));
        hfaChartLabel.setText(context.getStringResource(R.string.hfa_string));

        String []series=initiateSeries();

//        System.out.println("gender : "+client.getDetails().get("gender"));
//        System.out.println("DOB : "+client.getDetails().get("tanggalLahir"));

        new GrowthChartGenerator(lfaGraph, GraphConstant.LFA_CHART,client.getDetails().get("gender"),client.getDetails().get("tanggalLahir"),series[0],series[1]);
        lfaGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.x_axis_label);
                else
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.length_unit);
            }
        });

        new GrowthChartGenerator(hfaGraph,GraphConstant.HFA_CHART,client.getDetails().get("gender"),client.getDetails().get("tanggalLahir"),series[2],series[3]);
        hfaGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.x_axis_label);
                else
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.length_unit);
            }
        });
    }

    private String []initiateSeries(){
        String series[]=new String[4];
        String result = createSeries();
        String []data = result.split("#");
        if(!result.contains(";")){
            if(Integer.parseInt(data[0].split(",")[0]) < 24){
                series[0] = data[0];
                series[1] = data[1];
                series[2] = series[3] = "";
            }
            else{
                series[2] = data[0];
                series[3] = data[1];
                series[0] = series[1] = "";
            }
        }else{
            series[0] = data[0].split(";")[0];
            series[1] = data[1].split(";")[0];
            series[2] = data[0].split(";")[1];
            series[3] = data[1].split(";")[1];
        }
        return series;
    }

    private String createSeries(){
        if(client.getDetails().get("history_tinggi")==null) {
            return "0#0";
        }
        else if(client.getDetails().get("history_tinggi").length()<3) {
            return "0#0";
        }
        String ageSeries="";
        String lengthSeries="";
        String dateOfBirth = client.getDetails().get("tanggalLahirAnak").substring(0,10);
        String data = client.getDetails().get("history_tinggi");
        String []array = data.substring(4,data.length()).split(",");
        String [][]array2 = new String[array.length][];
        int []age = new int[array.length];
        for(int i=0;i<array.length;i++){
            if(array[i].charAt(array[i].length()-1) == ':')
                continue;
            array2[i]=array[i].split(":");
            age[i]=monthAge(dateOfBirth, Integer.toString(Integer.parseInt(array2[i][0])/30));
            ageSeries = ageSeries + "," + Integer.toString(age[i]);
            lengthSeries = lengthSeries + "," + array2[i][1];
            if(i>0){
                if(age[i]>=24 && age[i-1]<24){
                    ageSeries=ageSeries + ";" +Integer.toString(age[i]);
                    lengthSeries = lengthSeries + ";" + array2[i][1];
                }
            }
        }
        if (ageSeries.length()+lengthSeries.length()<2)
            return "0#0";
        return ageSeries.substring(1,ageSeries.length())+"#"+lengthSeries.substring(1,lengthSeries.length());
    }

    private int monthAge(String dateFrom, String dateTo){
        if(dateFrom==null || dateTo==null)
            return 0;
        else if(dateFrom.length()<6 || dateTo.equals("") || dateTo.equals(" "))
            return 0;
        else if(dateTo.length()>0 && dateTo.length()<3)
            return Integer.parseInt(dateTo);
        return ((Integer.parseInt(dateTo.substring(0,4))-Integer.parseInt(dateFrom.substring(0,4)))*12) +
                (Integer.parseInt(dateTo.substring(5,7))-Integer.parseInt(dateFrom.substring(5,7)));
    }
}