package util.KMS;

/**
 * Created by Iq on 27/05/16.
 */
public class KmsCalc {

    public int monthAges(String lastVisitDate,String currentDate){
            if(lastVisitDate.length()<10 || currentDate.length()<10)
                return 0;
            int tahun = Integer.parseInt(currentDate.substring(0, 4)) - Integer.parseInt(lastVisitDate.substring(0, 4));
            int bulan = Integer.parseInt(currentDate.substring(5, 7)) - Integer.parseInt(lastVisitDate.substring(5, 7));
            int hari = Integer.parseInt(currentDate.substring(8)) - Integer.parseInt(lastVisitDate.substring(8));
            return (tahun * 12 + bulan + (int) (hari / 30));
    }

    public String cek2T(KmsPerson bayi){
        boolean status = true;
        String measureDate[] = {bayi.getLastVisitDate(),bayi.getSecondLastVisitDate()};
        double weight[] = {bayi.getWeight(),bayi.getPreviousWeight()};
        status = status && (cekWeightStatus(bayi.isMale(), bayi.getDateOfBirth(), measureDate, weight).toLowerCase().equals("not gaining weight"));
        String measureDate2[] = {bayi.getSecondLastVisitDate(),bayi.getThirdLastVisitDate()};
        double weight2[] = {bayi.getPreviousWeight(),bayi.getSecondLastWeight()};
        status = status && (cekWeightStatus(bayi.isMale(), bayi.getDateOfBirth(), measureDate2, weight2).toLowerCase().equals("not gaining weight"));
        bayi.Tidak2Kali = status;
        return (bayi.Tidak2Kali ? "Yes":"No");
    }



    public String cekWeightStatus(KmsPerson bayi){
        ////System.out.println("check weight status");
        String measureDate[] = {bayi.getLastVisitDate(),bayi.getSecondLastVisitDate()};
        double weight[] = {bayi.getWeight(),bayi.getPreviousWeight()};
        bayi.StatusBeratBadan = cekWeightStatus(bayi.isMale(),bayi.getDateOfBirth(),measureDate,weight);
        return  bayi.StatusBeratBadan;
    }

    public String cekWeightStatus(boolean isMale, String dateOfBirth, String measureDate[], double weight[]){
        if( measureDate[1].equals("0") || measureDate[0].equals("") || measureDate[1].equals(""))
            return "New";
        else {
            System.out.println("check weight status");
            System.out.println("date of birth "+dateOfBirth);
            System.out.println("measure date "+measureDate[0]+", "+measureDate[1]);
            System.out.println("weight "+weight[0]+", "+weight[1]);
            int age = monthAges(dateOfBirth, measureDate[0]);
            int range = monthAges(measureDate[1], measureDate[0]);
            int stagnanIndicator = (isMale ? 12 : 11);
            int index = age > stagnanIndicator ? stagnanIndicator : age;

            return range > 1
                    ? "Not attending previous visit"
                    : ((weight[0] - weight[1] + 0.000000000000004) * 1000)  >= KmsConstants.maleWeightUpIndicator[index]
                        ? "Weight Increase"
                        : "Not gaining weight";
        }
    }

    public String cekBGM(KmsPerson bayi){
        if(bayi.getAge()>60)
            return "No";
        bayi.BGM = bayi.isMale()
                ? KmsConstants.maleBGM[bayi.getAge()]>bayi.getWeight()
                : KmsConstants.femaleBGM[bayi.getAge()]>bayi.getWeight();
        return ""+(bayi.BGM ? "Yes":"No");
    }

    public String cekBawahKuning(KmsPerson bayi){
        if(bayi.getAge()>60)
            return "No";
        bayi.GarisKuning = bayi.isMale()
                ? ((KmsConstants.maleGarisKuning[bayi.getAge()][0]<=bayi.getWeight())
                && (bayi.getWeight()<=KmsConstants.maleGarisKuning[bayi.getAge()][1]))
                : ((KmsConstants.femaleGarisKuning[bayi.getAge()][0]<=bayi.getWeight())
                && (bayi.getWeight()<=KmsConstants.femaleGarisKuning[bayi.getAge()][1]))
        ;
        return ""+(bayi.GarisKuning ? "Yes":"No");
    }

}
