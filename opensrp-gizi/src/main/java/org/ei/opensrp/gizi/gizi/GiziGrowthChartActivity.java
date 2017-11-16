package org.ei.opensrp.gizi.gizi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.gizi.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.formula.Support;
import util.growthChart.GraphConstant;
import util.growthChart.GrowthChartGenerator;

/**
 * Created by Null on 2016-10-25.
 */
public class GiziGrowthChartActivity extends Activity{

    public static CommonPersonObjectClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = Context.getInstance();
        setContentView(R.layout.gizi_chart_activity);
        FlurryAgent.logEvent("Growth_chart_view");

        GraphView lfaGraph = (GraphView)findViewById(R.id.lfa_chart);
        GraphView hfaGraph = (GraphView)findViewById(R.id.hfa_chart);

        TextView layoutName = (TextView)findViewById(R.id.chart_label);
        TextView lfaChartLabel = (TextView)findViewById(R.id.lfa_chart_name);
        TextView hfaChartLabel = (TextView)findViewById(R.id.hfa_chart_name);

        TextView navBarDetails = (TextView)findViewById(R.id.chart_navbar_details);
        TextView navBarZScore = (TextView)findViewById(R.id.chart_navbar_z_score);
        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(GiziGrowthChartActivity.this, GiziSmartRegisterActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = new SimpleDateFormat("hh:mm:ss").format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("gizi_detail_view", Detail, true);
            }
        });

        navBarDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                GiziDetailActivity.childclient = client;
                startActivity(new Intent(GiziGrowthChartActivity.this, GiziDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        navBarZScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                GiziZScoreChartActivity.client = client;
                startActivity(new Intent(GiziGrowthChartActivity.this, GiziZScoreChartActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        layoutName.setText(context.getStringResource(R.string.chart_title));
        lfaChartLabel.setText(context.getStringResource(R.string.lfa_string));
        hfaChartLabel.setText(context.getStringResource(R.string.hfa_string));

        String []series=initiateSeries();

        System.out.println("gender : "+Support.getDetails(client,"gender"));
        System.out.println("DOB : "+Support.getDetails(client,"tanggalLahir"));

        new GrowthChartGenerator(lfaGraph, GraphConstant.LFA_CHART,Support.getDetails(client,"gender"),Support.getDetails(client,"tanggalLahir"),series[0],series[1]);
        lfaGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX)
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.x_axis_label);
                else
                    return super.formatLabel(value, isValueX) + " " + context.getStringResource(R.string.length_unit);
            }
        });

        new GrowthChartGenerator(hfaGraph,GraphConstant.HFA_CHART,Support.getDetails(client,"gender"),Support.getDetails(client,"tanggalLahir"),series[2],series[3]);
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
        if(Support.getDetails(client,"history_tinggi")==null) {
            return "0#0";
        }
        else if(Support.getDetails(client,"history_tinggi").length()<3) {
            return "0#0";
        }
        String ageSeries="";
        String lengthSeries="";
        String dateOfBirth = Support.getDetails(client,"tanggalLahirAnak").substring(0,10);
        String data = Support.fixHistory(Support.getDetails(client,"history_tinggi"));
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