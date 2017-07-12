package org.ei.opensrp.path.db;

import java.util.ArrayList;

public class VaccineRepo {
    public enum Vaccine {
        bcg("BCG", null, 366, 0, 0, "child"),
        opv0("OPV 0", null, 13, 0, 0, "child"),

        opv1("OPV 1", null, 1768, 42, 0, "child"),
        penta1("PENTA 1", null, 1768, 42, 0, "child"),
        pcv1("PCV 1", null, 1768, 42, 0, "child"),
        rota1("ROTA 1", null, 213, 42, 0, "child"),

        opv2("OPV 2", opv1, 1799, 70, 28, "child"),
        penta2("PENTA 2", penta1, 1799, 70, 28, "child"),
        pcv2("PCV 2", pcv1, 1799, 70, 28, "child"),
        rota2("ROTA 2", rota1, 244, 70, 28, "child"),

        opv3("OPV 3", opv2, 1830, 98, 28, "child"),
        penta3("PENTA 3", penta2, 1830, 98, 28, "child"),
        pcv3("PCV 3", pcv2, 1830, 98, 28, "child"),

        measles1("MEASLES 1", null, -1, 274, 0, "child"),
        mr1("MR 1", null, -1, 274, 0, "child"),
        opv4("OPV 4", null, 1830, 274, 28, "child"),

        measles2("MEASLES 2", measles1, -1, 548, 274, "child"),
        mr2("MR 2", mr1, -1, 548, 274, "child"),

        bcg2("BCG 2", null, 366, 0, 84, "child"),
        ipv("IPV", opv3, 1830, 274, 28, "child"),

        tt1("TT 1", null, 0, 0, 0, "woman"),
        tt2("TT 2", tt1, 366, 0, 28, "woman"),
        tt3("TT 3", tt2, 366, 0, 26 * 7, "woman"),
        tt4("TT 4", tt3, 366, 0, 52 * 7, "woman"),
        tt5("TT 5", tt4, 1830, 0, 52 * 7, "woman"),;

        private String display;
        private Vaccine prerequisite;
        private int expiryDays;
        private int milestoneGapDays;
        private int prerequisiteGapDays;
        private String category;

        public String display() {
            return display;
        }

        public Vaccine prerequisite() {
            return prerequisite;
        }

        public int expiryDays() {
            return expiryDays;
        }

        public int milestoneGapDays() {
            return milestoneGapDays;
        }

        public int prerequisiteGapDays() {
            return prerequisiteGapDays;
        }

        public String category() {
            return category;
        }

        Vaccine(String display, Vaccine prerequisite, int expiryDays,
                int milestoneGapDays, int prerequisiteGapDays, String category) {
            this.display = display;
            this.prerequisite = prerequisite;
            this.expiryDays = expiryDays;
            this.milestoneGapDays = milestoneGapDays;
            this.prerequisiteGapDays = prerequisiteGapDays;
            this.category = category;
        }

    }

    public static ArrayList<Vaccine> getVaccines(String category) {
        ArrayList<Vaccine> vl = new ArrayList<>();
        for (Vaccine v : Vaccine.values()) {
            if (v.category().equalsIgnoreCase(category.trim())) {
                vl.add(v);
            }
        }
        return vl;
    }

    public static Vaccine getVaccine(String name, String category) {
        for (Vaccine curVaccine : Vaccine.values()) {
            if (curVaccine.display.equalsIgnoreCase(name)
                    && curVaccine.category.equalsIgnoreCase(category)) {
                return curVaccine;
            }
        }

        return null;
    }

    public static ArrayList<Vaccine> nextVaccines(String vaccine) {
        ArrayList<Vaccine> vl = new ArrayList<>();
        for (Vaccine v : Vaccine.values()) {
            if (v.prerequisite().name().equalsIgnoreCase(vaccine.trim())) {
                vl.add(v);
            }
        }
        return vl;
    }
}
