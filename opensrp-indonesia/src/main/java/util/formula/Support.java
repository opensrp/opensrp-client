package util.formula;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Log;

import java.io.File;

/**
 * Created by al on 30/05/2017.
 */
public class Support {
    public static boolean ONSYNC = false;

    public static String[]replace(String[]data,String target,String replacement){
        for(int i=0;i<data.length;i++){
            if(data[i].equals(target))
                data[i]=replacement;
        }
        return data;
    }

    public static String[]split(String data){
        if(data == null)
            data="";
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

    public static String getColumnmaps(CommonPersonObjectClient person, String values){
        if(person.getColumnmaps().get(values)!=null){
            if(person.getColumnmaps().get(values).length()>0)
                return person.getColumnmaps().get(values);
        }
        return "-";
    }

    public static String getColumnmaps(CommonPersonObject person, String values){
        if(person.getColumnmaps().get(values)!=null){
            if(person.getColumnmaps().get(values).length()>0)
                return person.getColumnmaps().get(values);
        }
        return "-";
    }

    public static String getDetails(CommonPersonObjectClient person, String values){
        if(person.getDetails().get(values)!=null){
            if(person.getDetails().get(values).length()>0)
                return person.getDetails().get(values);
        }
        return "-";
    }

    public static String getDetails(CommonPersonObject person, String values){
        if(person.getDetails().get(values)!=null){
            if(person.getDetails().get(values).length()>0)
                return person.getDetails().get(values);
        }
        return "-";
    }

    public static String[]insertionSort(String data){
        String[]temp = data.split(",");
        for(int i=0;i<temp.length;i++){
            for(int j=temp.length-1;j>i;j--){
                if(getAge(temp[j])<getAge(temp[j-1])){
                    String a = temp[j];
                    temp[j]=temp[j-1];
                    temp[j-1]=a;
                }
            }
        }

        return temp;
    }

    public static int getAge(String data){
        if(data.contains(":"))
            return Integer.parseInt(data.split(":")[0]);
        return 0;
    }

    public static String combine(String[]data, String separator){
        String result="";
        for(int i=0;i<data.length;i++){
            result=result+separator+data[i];
        }
        return result.substring(1,result.length());
    }

    public static String fixHistory(String data){
        if(data == null)
            return null;
        return combine(insertionSort(data), ",");
    }

    public static String findDate(String startDate, int dayAge){
        int[]dayLength = {31,28,31,30,31,30,31,31,30,31,30,31};
        int startYear = Integer.parseInt(startDate.substring(0,4));
        int startMonth = Integer.parseInt(startDate.substring(5,7));
        int startDay = Integer.parseInt(startDate.substring(8, 10));

        dayLength[1] = startYear % 4 == 0 ? 29 : 28;
        while(dayAge>dayLength[startMonth-1]){
            dayAge = dayAge - dayLength[startMonth-1];
            startMonth++;
            if(startMonth>12){
                startYear++;
                startMonth = 1;
                dayLength[1] = startYear % 4 == 0 ? 29 : 28;
            }
        }
        startDay+=dayAge;
        if(startDay > dayLength[startMonth-1]) {
            startDay=startDay - dayLength[startMonth-1];
            startMonth++;
        }
        if(startMonth>12) {
            startYear++;
            startMonth = 1;
        }

        String m = "" + (startMonth<10 ? "0"+startMonth : Integer.toString(startMonth));
        String d = "" + (startDay<10 ? "0"+startDay : Integer.toString(startDay));
        return Integer.toString(startYear)+"-"+m+"-"+d;
    }

    public static void setImagetoHolderFromUri(Activity activity, String file, ImageView view, int placeholder) {
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        if (!externalFile.exists()) {
            externalFile = new File(file.replace(".JPEG", ".jpg"));
        }
        if (externalFile.exists()) {
            Uri external = Uri.fromFile(externalFile);
            view.setImageURI(external);
        } else {
            Log.logError(Support.class.getSimpleName(), String.format("image %s doesn't exist",file));
        }
    }

    public static int monthAges(String lastVisitDate,String currentDate){
        int tahun = Integer.parseInt(currentDate.substring(0,4))-Integer.parseInt(lastVisitDate.substring(0,4));
        int bulan = Integer.parseInt(currentDate.substring(5,7))-Integer.parseInt(lastVisitDate.substring(5,7));
        int hari = Integer.parseInt(currentDate.substring(8))-Integer.parseInt(lastVisitDate.substring(8));
        return(tahun*12 + bulan + (int)(hari/30));
    }
}
