package util.KMS;

/**
 * Created by Iq on 02/05/16.
 */
public class KmsPerson {

    private String name;
    private boolean isMale;
    private int age;
    private String dateOfBirth;
    private double weight;
    private double previousWeight;
    private double secondLastWeight;
    private String lastVisitDate;
    private String secondLastVisitDate;

    //ditanya
    public String StatusBeratBadan;         // value : naik, tidak, ukur pertama
    public boolean BGM;
    public boolean GarisKuning;
    public boolean Tidak2Kali;

    //constructor
    public KmsPerson( boolean isMale, String dateOfBirth,double weight,
                    double previousWeight,String lastVisitDate, double secondLastWeight,
                    String secondLastVisitDate){

        this.isMale=isMale;
        this.dateOfBirth=dateOfBirth;
        this.age=monthAges(dateOfBirth,lastVisitDate);
        this.weight=weight;
        this.previousWeight=previousWeight;
        this.lastVisitDate=lastVisitDate;
        this.secondLastWeight=secondLastWeight;
        this.secondLastVisitDate=secondLastVisitDate;
    }
    private int monthAges(String lastVisitDate,String currentDate){
        int tahun = Integer.parseInt(currentDate.substring(0,4))-Integer.parseInt(lastVisitDate.substring(0,4));
        int bulan = Integer.parseInt(currentDate.substring(5,7))-Integer.parseInt(lastVisitDate.substring(5,7));
        int hari = Integer.parseInt(currentDate.substring(8))-Integer.parseInt(lastVisitDate.substring(8));
        return(tahun*12 + bulan + (int)(hari/30));
    }
    //mutators
 //   public String getName(){return name;}
    public boolean isMale(){return isMale;}
    public int getAge(){return age;}
    public String getDateOfBirth(){return dateOfBirth;}
    public double getWeight(){return weight;}
    public double getPreviousWeight(){return previousWeight;}
    public String getLastVisitDate(){return lastVisitDate;}
    public double getSecondLastWeight(){return secondLastWeight;}
    public String getSecondLastVisitDate(){return secondLastVisitDate;}
}
