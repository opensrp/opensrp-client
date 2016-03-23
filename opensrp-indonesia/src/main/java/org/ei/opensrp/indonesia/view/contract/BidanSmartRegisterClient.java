package org.ei.opensrp.indonesia.view.contract;

import com.google.common.base.Strings;

import org.ei.opensrp.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.List;

import static org.ei.opensrp.indonesia.util.StringUtil.getValueFromNumber;
import static org.ei.opensrp.indonesia.util.StringUtil.humanize;

/**
 * Created by Dimas Ciputra on 6/11/15.
 */
public abstract class BidanSmartRegisterClient implements SmartRegisterClient {

    private boolean isHighRiskLabour = false;
    private boolean isHighRiskFromANC = false;
    private boolean isHighRiskPregnancy = false;

    private boolean isPregnant = false;
    private boolean isInPNCorANC = false;

    // High Risk Factors
    private String rLila;
    private String rHbLevels;
    private String rBloodSugar;
    private String rTdSistolik;
    private String rTdDiastolik;
    private String rPartus;
    private String rDeliveryInterval;
    private String chronicDisease;

    // High Risk Pregnancy Factors
    private String rAbortus;

    // High Risk Labour Factors
    private String rHeight;
    private String rPelvicDeformity;
    private String rPregnancyComplications;
    private String rFetusNumber;
    private String rFetusSize;
    private String rFetusPosition;

    private String rDeliveryMethod;
    private String laborComplication;
    private String postPartumHypertention;
    private String postPartumComplication;

    public String getPostPartumComplication() {
        return postPartumComplication;
    }

    public void setPostPartumComplication(String postPartumComplication) {
        this.postPartumComplication = postPartumComplication;
    }

    public String getrDeliveryMethod() {
        return rDeliveryMethod;
    }

    public void setrDeliveryMethod(String rDeliveryMethod) {
        this.rDeliveryMethod = rDeliveryMethod;
    }

    public String getLaborComplication() {
        return this.laborComplication;
    }

    public void setLaborComplication(String laborComplication) {
        this.laborComplication = laborComplication;
    }

    public String getPostPartumHypertention() {
        return postPartumHypertention;
    }

    public void setPostPartumHypertention(String postPartumHypertention) {
        this.postPartumHypertention = postPartumHypertention;
    }

    public void setrLila(String lila) {
        this.rLila = lila;
    }

    public String rLila() {
        return getValueFromNumber(rLila);
    }

    public void setrDeliveryInterval(String deliveryInterval) {
        this.rDeliveryInterval = deliveryInterval;
    }

    public String rDeliveryInterval() {
        return getValueFromNumber(rDeliveryInterval);
    }

    public void setrPartus(String partus) {
        this.rPartus = partus;
    }

    public String rPartus() {
        return getValueFromNumber(rPartus);
    }

    public void setrHbLevels(String hbLevels) {
        this.rHbLevels = hbLevels;
    }

    public String rHbLevels() {
        return getValueFromNumber(rHbLevels);
    }

    public String getChronicDisease() {
        return chronicDisease;
    }

    public void setChronicDisease(String chronicDisease) {
        this.chronicDisease = chronicDisease;
    }

    public String getrBloodSugar() {
        return getValueFromNumber(rBloodSugar);
    }

    public void setrBloodSugar(String rBloodSugar) {
        this.rBloodSugar = rBloodSugar;
    }

    public String getrTdDiastolik() {
        return getValueFromNumber(rTdDiastolik);
    }

    public void setrTdDiastolik(String rTdDiastolik) {
        this.rTdDiastolik = rTdDiastolik;
    }

    public String getrTdSistolik() {
        return getValueFromNumber(rTdSistolik);
    }

    public void setrTdSistolik(String rTdSistolik) {
        this.rTdSistolik = rTdSistolik;
    }

    public String getrAbortus() {
        return getValueFromNumber(rAbortus);
    }

    public void setrAbortus(String rAbortus) {
        this.rAbortus = rAbortus;
    }

    public void setIsHighRiskLabour(boolean isHighRiskLabour) {
        this.isHighRiskLabour = isHighRiskLabour;
    }

    public void setIsHighRiskPregnancy(boolean isHighRiskPregnancy) {
        this.isHighRiskPregnancy = isHighRiskPregnancy;
    }

    public void setIsHighRiskFromANC(boolean isHighRiskFromANC) {
        this.isHighRiskFromANC = isHighRiskFromANC;
    }

    public String getrFetusPosition() {
        return rFetusPosition;
    }

    public void setrFetusPosition(String rFetusPosition) {
        this.rFetusPosition = rFetusPosition;
    }

    public String getrHeight() {
        return rHeight;
    }

    public void setrHeight(String rHeight) {
        this.rHeight = rHeight;
    }

    public String getrPelvicDeformity() {
        return rPelvicDeformity;
    }

    public void setrPelvicDeformity(String rPelvicDeformity) {
        this.rPelvicDeformity = rPelvicDeformity;
    }

    public String getrPregnancyComplications() {
        return rPregnancyComplications;
    }

    public void setrPregnancyComplications(String rPregnancyComplications) {
        this.rPregnancyComplications = rPregnancyComplications;
    }

    public String getrFetusNumber() {
        return rFetusNumber;
    }

    public void setrFetusNumber(String rFetusNumber) {
        this.rFetusNumber = rFetusNumber;
    }

    public String getrFetusSize() {
        return rFetusSize;
    }

    public void setrFetusSize(String rFetusSize) {
        this.rFetusSize = rFetusSize;
    }

    public void setIsPregnant(boolean isPregnant) { this.isPregnant = isPregnant; }

    public void setIsInPNCorANC(boolean isInPNCorANC) { this.isInPNCorANC = isInPNCorANC; }

    public boolean isPregnant() { return isPregnant; }

    public boolean isInPNCorANC() {return isInPNCorANC; }

    public boolean isHighRiskFromANC() { return isInPNCorANC() && !Strings.isNullOrEmpty(getChronicDisease()); }

    public boolean isMotherTooYoung() { return isPregnant() && age() < 20;}

    public boolean isMotherTooOld() { return isPregnant() && age() > 35; }

    public boolean isTooManyChildren() { return isPregnant() && Integer.parseInt(rPartus()) > 3; }

    public boolean isDeliveryIntervalsTooShort() { return isPregnant() && Integer.parseInt(rDeliveryInterval()) <= 2 && Integer.parseInt(rDeliveryInterval()) > 0; }

    public boolean isProteinEnergyMalnutrition() { return isInPNCorANC() && Float.parseFloat(rLila()) < 23.5 && Float.parseFloat(rLila()) > 0; }

    public boolean isAnemia() { return isInPNCorANC() && Float.parseFloat(rHbLevels()) >= 7 && Float.parseFloat(rHbLevels()) < 11 && Float.parseFloat(rHbLevels()) > 0; }

    public boolean isSevereAnemia() { return isInPNCorANC() && Float.parseFloat(rHbLevels()) < 7 &&  Float.parseFloat(rHbLevels()) > 0; }

    public boolean isHypertension() {
        Float tdSistolik = Float.parseFloat(getrTdSistolik());
        Float tdDiastolik = Float.parseFloat(getrTdDiastolik());
        if(tdSistolik == 0 || tdDiastolik == 0) {
            return false;
        }
        return isInPNCorANC() && (tdSistolik >= 140 || tdDiastolik >= 90);
    }

    public boolean isWomanHasSmallStature() {
        if(Strings.isNullOrEmpty(getrHeight())) return false;
        return isPregnant() && Float.parseFloat(getrHeight()) <= 145;
    }

    public boolean isWomanHasPelvicdeformity() {
        if(Strings.isNullOrEmpty(getrPelvicDeformity())) return false;
        return isPregnant() && getrPelvicDeformity().equalsIgnoreCase("ya");
    }

    public boolean hasComplicationHistory() {
        return isPregnant() && !Strings.isNullOrEmpty(getrPregnancyComplications());
    }

    public boolean hasAbnormalityFetusNumber() {
        if(Strings.isNullOrEmpty(getrFetusNumber())) return false;
        return isPregnant() && getrFetusNumber().equalsIgnoreCase("ganda");
    }

    public boolean hasAbnormalityFetusSize() {
        if(Strings.isNullOrEmpty(getrFetusSize())) return false;
        if(getrFetusSize().equalsIgnoreCase("nan")) return false;
        return isPregnant() && Integer.parseInt(getrFetusSize()) > 4000;
    }

    public boolean hasAbnormalityFetusPosition() {
        if(Strings.isNullOrEmpty(getrFetusPosition())) return false;
        return isPregnant() && (getrFetusPosition().equalsIgnoreCase("bokong___sungsang") || getrFetusPosition().equalsIgnoreCase("letak_lintang___obligue"));
    }

    public boolean isDiabetes() {
        return isPregnant() && getrBloodSugar().equalsIgnoreCase(">_140_mg_dl");
    }

    public boolean hasAbortionHistory() {
        Integer abortion = Integer.parseInt(getrAbortus());
        return isPregnant() && abortion > 3;
    }

    public boolean isAbnormalDelivery() {
        if(Strings.isNullOrEmpty(getrDeliveryMethod())) return false;
        return isInPNCorANC() && (getrDeliveryMethod().equalsIgnoreCase("vacum") ||
                getrDeliveryMethod().equalsIgnoreCase("forceps") ||
                getrDeliveryMethod().equalsIgnoreCase("sectio_caesaria"));
    }

    public boolean hasLaborComplication() {
        if(Strings.isNullOrEmpty(getLaborComplication())) return false;
        return isInPNCorANC() && !getLaborComplication().equalsIgnoreCase("tidak_ada_komplikasi");
    }

    public int riskFlagsCount() {
        int i = 0;
        i += isHighRiskPostPartum() ? 3 : 0;
        i += isHighRiskLabour() ? 3 : 0;
        i += isHighRiskPregnancy() ? 4 : 0;
        i += isHighRisk() ? 2 : 0;
        return i;
    }

    public List<String> highRiskReason() {
        List<String> reason = new ArrayList<>();

        // Age factors
        if(isMotherTooYoung()) {
            reason.add("Ibu terlalu muda");
        } else if(isMotherTooOld()) {
            reason.add("Ibu terlalu tua");
        }

        // Delivery interval factors
        if(isDeliveryIntervalsTooShort()) {
            reason.add("Jarak persalinan terlalu dekat");
        }

        if(isTooManyChildren()) {
            reason.add("Anak lebih dari 3");
        }

        if(isHighRiskFromANC()) {
            String[] chronicDisease = getChronicDisease().split("\\s+");
            for(int i=0; i < chronicDisease.length; i++) {
                reason.add(chronicDisease[i]);
            }
        }
        return reason;
    }

    public List<String> highPregnancyReason() {
        List<String> reason = new ArrayList<>();

        if(isProteinEnergyMalnutrition()) {
            reason.add("Kekurangan Energi Kronis");
        }

        if(isAnemia()) {
            reason.add("Anemia");
        } else if(isSevereAnemia()) {
            reason.add("Anemia akut");
        }

        if(isHypertension()) {
            reason.add("Hipertensi/PIH");
        }

        if(isDiabetes()) {
            reason.add("Diabetes");
        }

        if(hasAbortionHistory()) {
            reason.add("Abortus lebih dari 3");
        }

        return reason;
    }

    public List<String> highRiskLabourReason() {
        List<String> reason = new ArrayList<>();

        if(isHypertension()) {
            reason.add("Hipertensi/PIH");
        }

        if(isWomanHasSmallStature()) {
            reason.add("Tinggi badan kurang");
        }

        if(isWomanHasPelvicdeformity()) {
            reason.add("Kelainan bentuk panggul");
        }

        if(hasComplicationHistory()) {
            String[] pregnancComplication = getrPregnancyComplications().split("\\s+");
            for(int i=0; i < pregnancComplication.length; i++) {
                reason.add(pregnancComplication[i]);
            }
        }

        if(hasAbnormalityFetusNumber()) {
            reason.add("Kelainan jumlah janin");
        }

        if(hasAbnormalityFetusSize()) {
            reason.add("Kelainin berat janin");
        }

        if(hasAbnormalityFetusPosition()) {
            reason.add("Malpresentasi janin");
        }

        return reason;
    }

    public List<String> highRiskPostPartumReason() {
        List<String> reason = new ArrayList<>();

        if(isAbnormalDelivery()) {
            reason.add("Persalinan abnormal = " + humanize(getrDeliveryMethod()));
        }

        if(hasLaborComplication()) {
            reason.add(humanize(laborComplication));
        }

        return reason;
    }

    @Override
    public boolean isHighRisk() {
        return  isMotherTooYoung() ||
                isMotherTooOld() ||
                isDeliveryIntervalsTooShort() ||
                isTooManyChildren() ||
                isHighRiskFromANC();
    }

    public boolean isHighRiskPregnancy() {
        return  isProteinEnergyMalnutrition() ||
                isAnemia() ||
                isSevereAnemia() ||
                isHypertension() ||
                isDiabetes() ||
                hasAbortionHistory();
    }

    public boolean isHighRiskLabour() {
        return  isHypertension() ||
                isWomanHasPelvicdeformity() ||
                isWomanHasSmallStature() ||
                hasComplicationHistory() ||
                hasAbnormalityFetusNumber() ||
                hasAbnormalityFetusPosition() ||
                hasAbnormalityFetusSize();
    }

    public boolean isHighRiskPostPartum() {
        return isAbnormalDelivery() ||
                hasLaborComplication();
    }
}
