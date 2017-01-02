package org.ei.opensrp.repository.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VaccineRepo {
    public enum Vaccine {
        bcg("BCG", 1, null, 366, 0, 0, 4, 366,  "child"),
        penta1("Penta 1", 3, null, 366, 42, 0, 4, 366,  "child"),
        penta2("Penta 2", 6, penta1, 366, 70, 28, 4, 366,  "child"),
        penta3("Penta 3", 9, penta2, 366, 98, 28, 4, 366,  "child"),
        opv0("OPV 0", 2, null, 1830, 0, 0, 4, 1830,  "child"),
        opv1("OPV 1", 4, null, 1830, 42, 0, 4, 1830,  "child"),
        opv2("OPV 2", 7, opv1, 1830, 70, 28, 4, 1830,  "child"),
        opv3("OPV 3", 10, opv2, 1830, 98, 28, 4, 1830,  "child"),
        ipv("IPV", 11, opv2, 1830, 98, 28, 4, 1830,  "child"),
        pcv1("PCV 1", 5, null, 1830, 42, 0, 4, 1830,  "child"),
        pcv2("PCV 2", 8, pcv1, 1830, 70, 28, 4, 1830,  "child"),
        pcv3("PCV 3", 12, pcv2, 1830, 98, 28, 4, 1830,  "child"),
        measles1("Measles 1", 13, null, 1830, 273, 0, 14, 1830,  "child"),
        measles2("Measles 2", 14, measles1, 1830, 458, 28, 70, 1830,  "child"),

        tt1("TT 1", 1, null, 0, 0, 0, 4, 366,  "woman"),
        tt2("TT 2", 2, tt1, 366, 0, 28, 4, 366,  "woman"),
        tt3("TT 3", 3, tt2, 366, 0, 26*7, 4, 366,  "woman"),
        tt4("TT 4", 4, tt3, 366, 0, 52*7, 4, 366,  "woman"),
        tt5("TT 5", 5, tt4, 1830, 0, 52*7, 4, 1830,  "woman"), ;

        private String display;
        private int orderAs;
        private Vaccine prerequisite;
        private int expiryDays;
        private int milestoneGapDays;
        private int prerequisiteGapDays;
        private int minGracePeriodDays;
        private int maxGracePeriodDays;
        private String category;

        public String display(){return display;}
        public int orderAs() {return orderAs;};
        public Vaccine prerequisite(){return prerequisite;}
        public int expiryDays(){return expiryDays;}
        public int milestoneGapDays() {return milestoneGapDays;}
        public int prerequisiteGapDays() {return prerequisiteGapDays;}
        public int minGracePeriodDays(){return minGracePeriodDays;}
        public int maxGracePeriodDays(){return maxGracePeriodDays;}
        public String category(){return category;}

        Vaccine(String display, int orderAs, Vaccine prerequisite, int expiryDays,
                 int milestoneGapDays, int prerequisiteGapDays, int minGracePeriodDays, int maxGracePeriodDays, String category) {
            this.display = display;
            this.orderAs = orderAs;
            this.prerequisite = prerequisite;
            this.expiryDays = expiryDays;
            this.milestoneGapDays = milestoneGapDays;
            this.prerequisiteGapDays = prerequisiteGapDays;
            this.minGracePeriodDays = minGracePeriodDays;
            this.maxGracePeriodDays = maxGracePeriodDays;
            this.category = category;
        }

    }

    public static ArrayList<Vaccine> getVaccines(String category){
        ArrayList<Vaccine> vl = new ArrayList<>();
        for (Vaccine v : Vaccine.values()) {
            if(v.category().equalsIgnoreCase(category.trim())){
                vl.add(v);
            }
        }
        return vl;
    }
    
    public static ArrayList<Vaccine> nextVaccines(String vaccine){
        ArrayList<Vaccine> vl = new ArrayList<>();
        for (Vaccine v: Vaccine.values()) {
            if (v.prerequisite().name().equalsIgnoreCase(vaccine.trim())){
                vl.add(v);
            }
        }
        return vl;
    }

    public List<Vaccine> vaccineOfMilestone(int dueMilestoneDays, String category){
        ArrayList<Vaccine> vl = getVaccines(category);

        List<Vaccine> rl = new ArrayList<>();
        for (Vaccine v: vl) {
            if (v.milestoneGapDays() == dueMilestoneDays){
                rl.add(v);
            }
        }

        return rl;
    }

    public static final Comparator<Vaccine> VACCINE_COMPARATOR = new Comparator<Vaccine>() {
        public int compare(Vaccine v1, Vaccine v2) {
            if (v1.milestoneGapDays() == v2.milestoneGapDays()) return 0;
            if (v1.milestoneGapDays() > v2.milestoneGapDays()) return 1;

            return -1;
        }
    };
}
