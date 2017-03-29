package util.growthChart;

import android.graphics.Color;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.ei.opensrp.gizi.gizi.ChildDetailActivity;

import java.util.ArrayList;

public class GrowthChartGenerator {

    public GrowthChartGenerator(GraphView graph,String gender,String dateOfBirth, String xValue,String yValue){
        this.graph = graph;
        this.xValue = xValue;
        this.yValue = yValue;
        this.dateOfBirth=dateOfBirth;
        this.gender = gender;
        //System.out.println("XValue: "+xValue);
        //System.out.println("YValue: "+yValue);
        //System.out.println("DOB : "+dateOfBirth);
        //System.out.println("gender: "+gender);
        a = new LineGraphSeries[3][];
        graphLine = this.gender.toLowerCase().contains("em") ? GraphConstant.girlWeightChart : GraphConstant.boyWeightChart;
        buildGraphTemplate();
    }

    public GrowthChartGenerator(GraphView graph,int graphStyle, String gender,String dateOfBirth, String xValue,String yValue){
        this.graph = graph;
        this.dateOfBirth=dateOfBirth;
        this.gender = gender;
        a = new LineGraphSeries[3][];
        switch(graphStyle){
            case GraphConstant.WFA_CHART : initWeightForAgeChart(xValue, yValue);
                break;
            case GraphConstant.HFA_CHART : initHeightForAgeChart(xValue, yValue);
                break;
            case GraphConstant.LFA_CHART : initLengthForAgeChart(xValue, yValue);
                break;
            case GraphConstant.Z_SCORE_CHART : initzScoreChart(xValue, yValue);
                break;
        }
    }

    private void initStage(){
        graph.setBackgroundColor(Color.rgb(215, 215, 215));
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalable(true);
    }

    public void initLengthForAgeChart(String xValue, String yValue){
        this.xValue = xValue;
        this.yValue = yValue;
        graphLine = gender.toLowerCase().contains("em") ? GraphConstant.girlLengthChart : GraphConstant.boyLengthChart;
        buildGraphTemplate();
    }

    public void initHeightForAgeChart(String xValue, String yValue){
        this.ageShift = 24;
        this.xValue = xValue;
        this.yValue = yValue;
        graphLine = gender.toLowerCase().contains("em") ? GraphConstant.girlHeightChart : GraphConstant.boyHeightChart;
        buildGraphTemplate();
    }

    public void initWeightForAgeChart(String xValue, String yValue){
        this.xValue = xValue;
        this.yValue = yValue;
        graphLine = gender.toLowerCase().contains("em") ? GraphConstant.girlWeightChart : GraphConstant.boyWeightChart;
        buildGraphTemplate();
    }

    public void initzScoreChart(String xValue, String yValue){
        this.xValue = xValue;
        this.yValue = yValue;
        graphLine = GraphConstant.zScoreBackground();
        buildGraphTemplate();
    }

    private void buildGraphTemplate() {

        initStage();

        initSeries(series1, Color.argb(255,215,215,215), red, 5);
        initSeries(series2, Color.argb(192, 255, 255, 0), yellow, 5);
        initSeries(series3, Color.argb(128, 128, 255, 128), green, 5);
        initSeries(series4, Color.argb(30, 0, 135, 0), green, 5);
        initSeries(series5, Color.argb(30, 0, 40, 0), green, 5);
        initSeries(series6, Color.argb(128, 0, 255, 0), green, 5);
        initSeries(series7, Color.argb(128, 255, 255, 0), yellow, 5);

//        initSeries(seriesMain, Color.argb(0, 0, 0, 0), Color.BLUE, 3, "weight", true);

        for(int i=0;i<graphLine.length;i++){
            series1.appendData(new DataPoint(i+ageShift,graphLine[i][0]),false,62);
            series2.appendData(new DataPoint(i+ageShift,graphLine[i][1]),false,62);
            series3.appendData(new DataPoint(i+ageShift,graphLine[i][2]),false,62);
            series4.appendData(new DataPoint(i+ageShift,graphLine[i][3]),false,62);
            series5.appendData(new DataPoint(i+ageShift,graphLine[i][4]),false,62);
            series6.appendData(new DataPoint(i+ageShift,graphLine[i][5]),false,62);
            series7.appendData(new DataPoint(i+ageShift,graphLine[i][6]),false,62);
        }

        graph.addSeries(series7);
        graph.addSeries(series6);
        graph.addSeries(series5);
        graph.addSeries(series4);
        graph.addSeries(series3);
        graph.addSeries(series2);
        graph.addSeries(series1);

        if(xValue.contains("@"))
            createLineChart(graph,dateOfBirth,xValue.split("@"),yValue.split("@"));
        else
            createLineChart(graph,dateOfBirth,xValue,yValue);

//        //System.out.println("length of a = "+a[0].length + ","+a[1].length + ","+a[2].length);

    }
//
//    private void initSeries(LineGraphSeries<DataPoint> series, int backGround, int color, int thick,String title, boolean putStroke){
//        series.setTitle(title);
//        this.initSeries(series, backGround, color, thick);
//        if(putStroke){
//            series.setDrawDataPoints(true);
//            series.setDataPointsRadius(5);
//        }
//    }

    private void initSeries(LineGraphSeries<DataPoint> series, int backGround, int color, int thick){
        series.setDrawBackground(true);
        series.setBackgroundColor(backGround);
        series.setColor(color);
        series.setThickness(thick);
    }

    private LineGraphSeries<DataPoint>createDataSeries(String []age,String []weight){
        LineGraphSeries<DataPoint>series=new LineGraphSeries<>();
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(dataPointRadius);
        series.setColor(zScoreChartColor[index]);
        if(age[0]!=null)
            series.appendData(new DataPoint(Double.parseDouble(age[0]), Double.parseDouble(weight[0])), false, 70);
        for(int i=0;i<age.length;i++){
            series.appendData(new DataPoint(Double.parseDouble(age[i]), Double.parseDouble(weight[i])), false, 70);
        }
        return series;
    }

    private void createLineChart(GraphView graph, String dateOfBirth, String date,String weight){
        int counter = 0;
        if(date.equals("")||date.equals(" "))
            return;
        int[]dateInt = calculateAgesFrom(dateOfBirth, date.split(","));
        String []weightDouble = weight.split(",");
        int length = countAgeSeries(dateInt);
        String []axis = new String[length];
        String[] series = new String[length];
        if(!"0".equals(weightDouble[0])){
            series[0]=weightDouble[0];
            axis[0]=Integer.toString(dateInt[0]);
        }
        for(int i=1;i<dateInt.length;i++){
            if(dateInt[i]-dateInt[i-1]>1)
                counter++;
            if(series[counter]==null){
                series[counter]=weightDouble[i];
                axis[counter]=Integer.toString(dateInt[i]);
            }
            else{
                series[counter] = series[counter]+","+weightDouble[i];
                axis[counter]=axis[counter]+","+Integer.toString(dateInt[i]);
            }
        }
        //System.out.println("index of line 172 = "+index);
        ArrayList<LineGraphSeries>list = new ArrayList<>();
        LineGraphSeries<DataPoint>temp = new LineGraphSeries<>();
        for(int i=0;i<series.length;i++){
            if(series[i]==null)
                continue;
            try {
                temp = createDataSeries(axis[i].split(","), series[i].split(","));
                graph.addSeries(temp);
                list.add(temp);
            }catch(Exception e){
                continue;
            }
        }
        a[index]=new LineGraphSeries[list.size()];
        for(int i=0;i<list.size();i++){
            a[index][i]=list.get(i);
        }
    }

    private void createLineChart(GraphView graph, String dateOfBirth, String[]date, String[]value){
        //System.out.println("date length = "+date.length);
        for(int i=0;i<date.length;i++){
            this.index=i;
//            System.out.println("Z Score line chart : "+i);
//            System.out.println("date "+i+" : "+date[i]);
//            System.out.println("value "+i+" : "+value[i]);
            if(i>=date.length || i>=value.length)       // prevent null data on chart
                break;
            createLineChart(graph, dateOfBirth, date[i], value[i]);
        }
    }

    private int[]calculateAgesFrom(String dateOfBirth,String []data){
        int[]result=new int[data.length];
        if(data[0].length()>5) {
            for (int i = 0; i < data.length; i++) {
                result[i] = getMonthAge(dateOfBirth, data[i]);
            }
        }else{
            for (int i=0;i<data.length;i++){
                result[i]=Integer.parseInt(data[i]);
            }
        }
        return result;
    }

    private int getMonthAge(String start,String end){
        if(start.length()<7 || end.length()<7)
            return 0;
        return ((Integer.parseInt(end.substring(0,4)) - Integer.parseInt(start.substring(0,4)))*12 +
                (Integer.parseInt(end.substring(5,7)) - Integer.parseInt(start.substring(5,7))));
    }

    private int countAgeSeries(int[]data){
        int counter=data[0]==0? 0:1;
        for(int i=1;i<data.length-1;i++){
            if(data[i]-data[i-1]==0 && data[i+1]-data[i]==2)
                data[i]++;
            else if(data[i]-data[i-1]==2 && data[i+1]-data[i]==0)
                data[i]--;
        }
        for(int i=0;i<data.length-1;i++){
            if(data[i+1]-data[i]>1)
                counter++;
        }
        return counter+1;
    }

    public void putSeriesOfIndex(int idx){
        index = idx;
        if(!chartDisplay[index] && a[index]!=null){
            for(int i=0;i<a[index].length;i++){
                graph.addSeries(a[index][i]);
            }
            chartDisplay[index]=true;
        }
    }

    public void removeSeriesOfIndex(int idx){
        index=idx;
        if(chartDisplay[index] && a[index]!=null){
            for(int i=0;i<a[index].length;i++) {
                graph.removeSeries(a[index][i]);
            }
            chartDisplay[index]=false;
        }
    }

    GraphView graph;
    LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series2 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series3 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series4 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series5 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series6 = new LineGraphSeries<DataPoint>();
    LineGraphSeries<DataPoint> series7 = new LineGraphSeries<DataPoint>();
//    LineGraphSeries<DataPoint> seriesMain = new LineGraphSeries<DataPoint>();

    private double [][]graphLine;
    private String xValue;
    private String yValue;
    private String dateOfBirth;
    private String gender;
    LineGraphSeries [][]a;

    /**
     * age shift used to shift X-Axis value so the axis value will start from (0 + ageShift) value,
     * currently used when generating height for age chart.
     */
    private int ageShift = 0;
    private int index = 0;
    private boolean[]chartDisplay = {true,true,true};

    private final int red = Color.rgb(255,0,0);
    private final int yellow = Color.rgb(255,255,0);
    private final int green = Color.rgb(0,255,0);
    private final int blue = Color.rgb(0,32,255);
    private final int purple = Color.rgb(239,0,255);
    private final int orange = Color.rgb(255,111,0);
    private final int []zScoreChartColor = {blue,purple,orange};
    private final int dataPointRadius = 7;
}
