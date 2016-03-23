package org.ei.opensrp.indonesia.view.contract;

import com.google.common.base.Strings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.opensrp.indonesia.util.StringUtil;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.opensrp.AllConstants.ECRegistrationFields.BPL_VALUE;
import static org.ei.opensrp.AllConstants.FEMALE_GENDER;
import static org.ei.opensrp.AllConstants.IN_AREA;
import static org.ei.opensrp.AllConstants.OUT_OF_AREA;
import static org.ei.opensrp.util.DateUtil.formatDate;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by Dimas Ciputra on 4/8/15.
 */
public class AnakClient extends BidanSmartRegisterClient{

    private final String entityId;
    private Map<String, String> details;
    private String gender;
    private String birthWeight;
    private String currentWeight;
    private String name;
    private String motherName;
    private String dob;
    private String motherAge;
    private String fatherName;
    private String village;
    private String locationStatus;
    private String economicStatus;
    private String isHighRisk;
    private String photo_path;
    private String kiNumber;
    private String birthCondition;
    private String serviceAtBirth;
    private String kpspResult;
    private String hb07;
    private String bcgPol1;
    private String dptHb1Pol2;
    private String dptHb2Pol3;
    private String dptHb3Pol4;
    private String campak;
    private String birthPlace;
    private String hbGiven;
    private String visitDate;
    private String babyNo;
    private String pregnancyAge;

    public AnakClient(String entityId, String gender, String weight, Map<String, String> details) {
        this.entityId = entityId;
        this.gender = gender;
        this.birthWeight = weight;
        this.details = details;
    }

    public String getPregnancyAge() {
        return pregnancyAge;
    }

    public void setPregnancyAge(String pregnancyAge) {
        this.pregnancyAge = pregnancyAge;
    }

    public String getBabyNo() {
        return Strings.isNullOrEmpty(babyNo) ? "-" : babyNo;
    }

    public void setBabyNo(String babyNo) {
        this.babyNo = babyNo;
}

    public String motherName() {
        return StringUtil.humanize(motherName);
    }

    public String getHbGiven() {
        return Strings.isNullOrEmpty(hbGiven) ? "-" : hbGiven;
    }

    public void setHbGiven(String hbGiven) {
        this.hbGiven = hbGiven;
    }

    public String getVisitDate() {
        return Strings.isNullOrEmpty(visitDate) ? "-" : formatDate(visitDate);
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String birthPlace() { return Strings.isNullOrEmpty(birthPlace) ? "-" : StringUtil.humanize(birthPlace); }

    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String fatherName() {
        return StringUtil.humanize(fatherName);
    }

    public String gender() { return StringUtil.humanize(gender); }

    public String birthWeight() {
        return Strings.isNullOrEmpty(birthWeight) ? "-" : birthWeight ;
    }

    public String currentWeight() { return Strings.isNullOrEmpty(currentWeight) ? "-" : currentWeight; }

    public void setCurrentWeight(String currentWeight) { this.currentWeight = currentWeight; }

    public String motherKiNumber() {
        return kiNumber;
    }

    public String dateOfBirth() {
        return dob == null || isBlank(dob) ? "-" : formatDate(dob);
    }

    public String dobString() {
        return dob;
    }

    public String getBirthCondition() { return Strings.isNullOrEmpty(birthCondition) ? "-" : StringUtil.humanize(birthCondition); }

    public String getServiceAtBirth() { return serviceAtBirth; }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return isBlank(name) ? "B/o " + motherName() : StringUtil.humanize(name);
    }

    @Override
    public String displayName() {
        return isBlank(name) ? "B/o " + motherName() : StringUtil.humanize(name);
    }

    @Override
    public String village() {
        return Strings.isNullOrEmpty(village) ? "" : humanize(village);
    }

    @Override
    public int age() {
        return isBlank(dob) ? 0 : Years.yearsBetween(LocalDate.parse(dob), LocalDate.now()).getYears();
    }

    @Override
    /*
    public int ageInDays() {
        return isBlank(dob) ? 0 : Days.daysBetween(LocalDate.parse(dob), DateUtil.today()).getDays();
    }
    */
    public int ageInDays() {
        if(isBlank(dob)) {
            return 0;
        } else {
            try {
                return Days.daysBetween(LocalDate.parse(dob), DateUtil.today()).getDays();
            } catch (Exception e) {
                Log.logError("date format error : " + dob);
                return 0;
            }
        }
    }

    @Override
    public String ageInString() {
        return "(" + format(ageInDays()) + ", " + formatGender(gender()) + ")";
    }

    private String formatGender(String gender) {
        return FEMALE_GENDER.equalsIgnoreCase(gender) ? "F" : "M";
    }


    @Override
    public boolean isSC() {
        return false;
    }

    @Override
    public boolean isST() {
        return false;
    }
    @Override
    public boolean isHighPriority() { return false; }

    @Override
    public boolean isBPL() {
        return economicStatus != null && economicStatus.equalsIgnoreCase(BPL_VALUE);
    }

    @Override
    public String profilePhotoPath() {
        return photo_path;
    }

    @Override
    public String locationStatus() {
        return locationStatus;
    }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return (!isBlank(name) && name.toLowerCase().startsWith(filterCriterion.toLowerCase()))
                || (!isBlank(motherName) && motherName.toLowerCase().startsWith(filterCriterion.toLowerCase()))
                || (!isBlank(fatherName) && fatherName.toLowerCase().startsWith(filterCriterion.toLowerCase()))
                || String.valueOf(kiNumber).startsWith(filterCriterion);
    }

    @Override
    public int compareName(SmartRegisterClient anotherClient) {
        AnakClient anotherChildClient = (AnakClient) anotherClient;
        return this.name().compareTo(anotherChildClient.name());
    }

    @Override
    public String wifeName() {
        return name();
    }

    @Override
    public String husbandName() {
        return motherName() + "," + fatherName();
    }

    public String kpspResult() { return details.get("hasil_dilakukan_kpsp") == null ? "-" : details.get("hasil_dilakukan_kpsp"); }

    public String format(int days_since) {
        int DAYS_THRESHOLD = 28;
        int WEEKS_THRESHOLD = 119;
        int MONTHS_THRESHOLD = 720;
        if (days_since < DAYS_THRESHOLD) {
            return (int) Math.floor(days_since) + "h";
        } else if (days_since < WEEKS_THRESHOLD) {
            return (int) Math.floor(days_since / 7) + "m";
        } else if (days_since < MONTHS_THRESHOLD) {
            return (int) Math.floor(days_since / 30) + "b";
        } else {
            return (int) Math.floor(days_since / 365) + "t";
        }
    }

    public AnakClient withName(String name) {
        this.name = name;
        return this;
    }

    public AnakClient withMotherName(String motherName) {
        this.motherName = motherName;
        return this;
    }

    public AnakClient withDOB(String dob) {
        this.dob = dob;
        return this;
    }

    public AnakClient withMotherAge(String motherAge) {
        this.motherAge = motherAge;
        return this;
    }

    public AnakClient withFatherName(String fatherName) {
        this.fatherName = fatherName;
        return this;
    }

    public AnakClient withVillage(String village) {
        this.village = village;
        return this;
    }

    public AnakClient withOutOfArea(boolean outOfArea) {
        this.locationStatus = outOfArea ? OUT_OF_AREA : IN_AREA;
        return this;
    }

    public AnakClient withEconomicStatus(String economicStatus) {
        this.economicStatus = economicStatus;
        return this;
    }

    public AnakClient withIsHighRisk(String isHighRisk) {
        this.isHighRisk = isHighRisk;
        return this;
    }

    public AnakClient withPhotoPath(String photoPath) {
        this.photo_path = photoPath;
        return this;
    }

    public AnakClient withKINumber(String kiNumber) {
        this.kiNumber = kiNumber;
        return this;
    }

    public AnakClient withBirthCondition(String birthCondition) {
        this.birthCondition = birthCondition;
        return this;
    }

    public AnakClient withServiceAtBirth(String serviceAtBirth) {
        this.serviceAtBirth = serviceAtBirth;
        return this;
    }

    public String getHb07() {
        return isBlank(hb07) ? "-" : formatDate(hb07);
    }

    public void setHb07(String hb07) {
        this.hb07 = hb07;
    }

    public String getBcgPol1() {
        return isBlank(bcgPol1) ? "-" : formatDate(bcgPol1);
    }

    public void setBcgPol1(String bcgPol1) {
        this.bcgPol1 = bcgPol1;
    }

    public String getDptHb1Pol2() {
        return isBlank(dptHb1Pol2) ? "-" : formatDate(dptHb1Pol2);
    }

    public void setDptHb1Pol2(String dptHb1Pol2) {
        this.dptHb1Pol2 = dptHb1Pol2;
    }

    public String getDptHb2Pol3() {
        return isBlank(dptHb2Pol3) ? "-" : formatDate(dptHb2Pol3);
    }

    public void setDptHb2Pol3(String dptHb2Pol3) {
        this.dptHb2Pol3 = dptHb2Pol3;
    }

    public String getDptHb3Pol4() {
        return isBlank(dptHb3Pol4) ? "-" : formatDate(dptHb3Pol4);
    }

    public void setDptHb3Pol4(String dptHb3Pol4) {
        this.dptHb3Pol4 = dptHb3Pol4;
    }

    public String getCampak() {
        return isBlank(campak) ? "-" : formatDate(campak);
    }

    public void setCampak(String campak) {
        this.campak = campak;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean isChildAgeNoMoreThan28days() {
        return ageInDays() < 28;
    }

    public boolean isPreterm() {
        if(Strings.isNullOrEmpty(getPregnancyAge())) return false;
        return isChildAgeNoMoreThan28days() && Integer.parseInt(getPregnancyAge()) < 37;
    }

    public boolean isLowBirthWeight() {
        if(birthWeight().equalsIgnoreCase("-")) return false;
        return isChildAgeNoMoreThan28days() && Integer.parseInt(birthWeight()) > 1500 && Integer.parseInt(birthWeight()) < 2500;
    }

    public boolean isVeryLowBirthWeigth() {
        if(birthWeight().equalsIgnoreCase("-")) return false;
        return isChildAgeNoMoreThan28days() && Integer.parseInt(birthWeight()) <= 1500;
    }

    @Override
    public boolean isHighRisk() {
        return isPreterm() || isLowBirthWeight() || isVeryLowBirthWeigth();
    }

    @Override
    public List<String> highRiskReason() {
        List<String> reason = new ArrayList<>();

        if(isPreterm()) {
            reason.add("Anak lahir prematur");
        }
        if(isLowBirthWeight()) {
            reason.add("Berat badan rendah");
        } else if(isVeryLowBirthWeigth()) {
            reason.add("Berat badan sangat rendah");
        }

        return reason;
    }

    public boolean isHbSevenDone() {
        return !isBlank(hb07);
    }

    public boolean isBcgDone() {
        return !isBlank(bcgPol1);
    }

    public boolean isDpt1Done() {
        return !isBlank(dptHb1Pol2);
    }

    public boolean isDpt2Done() {
        return !isBlank(dptHb2Pol3);
    }

    public boolean isDpt3Done() {
        return !isBlank(dptHb3Pol4);
    }

    public boolean isCampakDone() {
        return !isBlank(campak);
    }

}
