package org.ei.opensrp.indonesia.view.contract;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ANCServiceType;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.Visits;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.ei.opensrp.domain.ANCServiceType.ANC_1;
import static org.ei.opensrp.domain.ANCServiceType.ANC_2;
import static org.ei.opensrp.domain.ANCServiceType.ANC_3;
import static org.ei.opensrp.domain.ANCServiceType.ANC_4;
import static org.ei.opensrp.domain.ANCServiceType.KB_IUD;
import static org.ei.opensrp.domain.ANCServiceType.KB_Implant;
import static org.ei.opensrp.domain.ANCServiceType.PNC;
import static org.ei.opensrp.domain.ANCServiceType.PNC_1;
import static org.ei.opensrp.domain.ANCServiceType.PNC_2;
import static org.ei.opensrp.domain.ANCServiceType.PNC_3;
import static org.ei.opensrp.indonesia.util.StringUtil.humanize;
import static org.ei.opensrp.view.contract.AlertDTO.emptyAlert;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 2/17/15.
 */
public class KartuIbuClient extends BidanSmartRegisterClient implements KISmartRegisterClient {

    public static final String CATEGORY_ANC = "anc";
    public static final String CATEGORY_TT = "tt";
    public static final String CATEGORY_IFA = "ifa";
    public static final String CATEGORY_HB = "hb";
    public static final String CATEGORY_DELIVERY_PLAN = "delivery_plan";
    public static final String CATEGORY_PNC = "pnc";
    public static final String CATEGORY_KB = "kb";

    private static final String[] SERVICE_CATEGORIES = {CATEGORY_ANC, CATEGORY_PNC, CATEGORY_KB};

    private static Map<String, List<ANCServiceType>> categoriesToServiceTypeMap = new HashMap<String, List<ANCServiceType>>();

    static {
        categoriesToServiceTypeMap.put(CATEGORY_ANC, Arrays.asList(ANC_1, ANC_2, ANC_3, ANC_4));
        categoriesToServiceTypeMap.put(CATEGORY_PNC, Arrays.asList(PNC,PNC_1, PNC_2, PNC_3));
        categoriesToServiceTypeMap.put(CATEGORY_KB, Arrays.asList(KB_IUD, KB_Implant));
    }


    private String entityId;
    private String puskesmas;
    private String province;
    private String kabupaten;
    private String posyandu;
    private String householdAddress;
    private String noIbu;
    private String uniqueId;
    private String wifeName;
    private String wifeAge;
    private String golonganDarah;
    private String husbandName;
    private String edd;
    private String village;
    private String dateOfBirth;
    private String numberOfPregnancies;
    private String parity;
    private String numberLivingChildren;
    private String numberOfAbortions;
    private String rtRw;
    private Map<String, String> status = new HashMap<String, String>();
    private List<KIChildClient> children;
    private String kbMethod;
    private String kbStart;
    private String IsHighRisk;
    private String isHighRiskANC;
    private String isHighPriority;
    private String religion;
    private String education;
    private String job;
    private String insurance;
    private String phoneNumber;
    private List<String> highRiskPregnancyReason;
    private Map<String, Visits> serviceToVisitsMap;
    private String ibuId;

    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;

    public KartuIbuClient(String entityId,String puskesmas, String province, String kabupaten, String posyandu, String householdAddress, String noIbu, String wifeName, String wifeAge, String golonganDarah, String husbandName, String village) {
        this.highRiskPregnancyReason = new ArrayList<>();
        this.entityId = entityId;
        this.puskesmas = puskesmas;
        this.province = province;
        this.kabupaten = kabupaten;
        this.posyandu = posyandu;
        this.householdAddress = householdAddress;
        this.noIbu = noIbu;
        this.wifeName = wifeName;
        this.wifeAge = wifeAge;
        this.golonganDarah = golonganDarah;
        this.husbandName = husbandName;
        this.village = village;
        this.children = new ArrayList<KIChildClient>();
        this.serviceToVisitsMap = new HashMap<String, Visits>();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getPhoneNumber() {
        return humanize(phoneNumber);
    }

    public String getReligion() {
        return humanize(religion);
    }

    public String getEducation() {
        return humanize(education);
    }

    public String getJob() {
        return humanize(job);
    }

    public String getInsurance() {
        return humanize(insurance);
    }

    // Getter
    public String getEntityId() {
        return entityId;
    }

    public String getPuskesmas() {
        return humanize(puskesmas);
    }

    public String getProvince() {
        return province;
    }

    public String getKabupaten() {
        return kabupaten;
    }

    public String getPosyandu() {
        return posyandu;
    }

    public String getHouseholdAddress() {
        return householdAddress;
    }

    public String getNoIbu() {
        return Strings.isNullOrEmpty(noIbu) ? "-" : noIbu;
    }

    public int getNoIbuInteger() {
        int noIbu = 0;
        try {
            noIbu = Integer.parseInt(getNoIbu());
        } catch (Exception e) {
            e.printStackTrace();
            noIbu = Integer.MAX_VALUE;
            return noIbu;
        }

        return noIbu;
    }

    public String getUniqueId() {
        return Strings.isNullOrEmpty(uniqueId) ? "-" : uniqueId;
    }

    public String getWifeName() {
        return Strings.isNullOrEmpty(this.wifeName) ? "" : humanize(this.wifeName);
    }

    public String getWifeAge() {
        return wifeAge;
    }

    public String getGolongan_darah() {
        return golonganDarah;
    }

    public String getEdd() {
        if(Strings.isNullOrEmpty(edd)) return "-";
        if(edd.equalsIgnoreCase("invalid date")) return "-";

        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormat.forPattern("dd MMM YYYY");
        LocalDateTime date = parse(edd, formatter);

        return "" + date.toString(formatter2);
    }

    public String getDueEdd() {
        String _edd = getEdd();
        String _dueEdd = "";

        if(!getEdd().equalsIgnoreCase("-")) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM YYYY");
            LocalDate date = parse(_edd, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();

            date = date.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);

            int months = Months.monthsBetween(dateNow, date).getMonths();
            if(months >= 1) {
                _dueEdd = "" + months + " " + Context.getInstance().getStringResource(R.string.months_away);
            } else if(months == 0){
                if(DateUtil.dayDifference(dateNow, date) > 0) {
                    _dueEdd =  Context.getInstance().getStringResource(R.string.this_month);
                }
                _dueEdd = Context.getInstance().getStringResource(R.string.delivered);
            } else {
                _dueEdd = Context.getInstance().getStringResource(R.string.delivered);
            }
        }

        return _dueEdd;
    }

    public LocalDateTime edd() {
        if(Strings.isNullOrEmpty(edd)) return null;
        if(edd.equalsIgnoreCase("invalid date")) return null;
        return parse(edd);
    }

    @Override
    public String numberOfPregnancies() {
        return Strings.isNullOrEmpty(numberOfPregnancies) ? "-" : numberOfPregnancies;
    }

    @Override
    public String parity() {
        return Strings.isNullOrEmpty(parity) ? "-" : parity;
    }

    @Override
    public String numberOfLivingChildren() {
        return Strings.isNullOrEmpty(numberLivingChildren) ? "-" : numberLivingChildren;
    }

    @Override
    public String numberOfAbortions() {
        return Strings.isNullOrEmpty(numberOfAbortions) ? "-" : numberOfAbortions;
    }

    // Setter
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setPuskesmas(String district) {
        this.puskesmas = district;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setKabupaten(String kabupaten) {
        this.kabupaten = kabupaten;
    }

    public void setPosyandu(String posyandu) {
        this.posyandu = posyandu;
    }

    public void setHouseholdAddress(String householdAddress) {
        this.householdAddress = householdAddress;
    }

    public void setNoIbu(String noIbu) {
        this.noIbu = noIbu;
    }

    public void setWifeName(String wifeName) {
        this.wifeName = wifeName;
    }

    public void setWifeAge(String wifeAge) {
        this.wifeAge = wifeAge;
    }

    public void setGolonganDarah(String golonganDarah) {
       this.golonganDarah = golonganDarah;
    }

    public KartuIbuClient withEdd(String edd) {
        if(status.size() != 0) {
            if(status.get("type").equalsIgnoreCase("pnc")) {
                this.edd = null;
                return this;
            }
        }
        this.edd = edd;
        return this;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public void setRtRw(String rtRw) { this.rtRw = rtRw; }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return getWifeName();
    }

    @Override
    public String displayName() {
        return getWifeName();
    }

    @Override
    public String village() {
        return Strings.isNullOrEmpty(village) ? "" : humanize(village.replace("+", " "));
    }

    @Override
    public String wifeName() {
        return Strings.isNullOrEmpty(this.wifeName) ? "" : humanize(this.wifeName);
    }

    @Override
    public String husbandName() {
        return Strings.isNullOrEmpty(this.husbandName) ? "" : humanize(this.husbandName);
    }

    @Override
    public int age() {
        return StringUtils.isBlank(dateOfBirth) ? 0 : Years.yearsBetween(LocalDate.parse(dateOfBirth), LocalDate.now()).getYears();
    }

    @Override
    public int ageInDays() {
        return StringUtils.isBlank(dateOfBirth) ? 0 : Days.daysBetween(LocalDate.parse(dateOfBirth), DateUtil.today()).getDays();
    }

    @Override
    public String ageInString() {
        return "(" + age() + ")";
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
    public boolean isHighPriority() {
        if(Strings.isNullOrEmpty(isHighPriority)) return false;
        return isHighPriority.equalsIgnoreCase("yes");
    }

    @Override
    public boolean isBPL() {
        return false;
    }

    @Override
    public String profilePhotoPath() {
        return null;
    }

    @Override
    public String locationStatus() {
        return null;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getRtRw() { return rtRw; }

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return wifeName.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || husbandName.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || String.valueOf(noIbu).startsWith(filterCriterion)
                || String.valueOf(puskesmas).startsWith(filterCriterion);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return this.wifeName().compareToIgnoreCase(client.wifeName());
    }

    public Map<String, String> status() {
        return status;
    }

    public KartuIbuClient withDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public KartuIbuClient withStatus(Map<String, String> status) {
        this.status = status;
        return this;
    }

    public KartuIbuClient withNumberOfPregnancies(String numberOfPregnancies) {
        this.numberOfPregnancies = numberOfPregnancies;
        return this;
    }

    public KartuIbuClient withParity(String parity) {
        this.parity = parity;
        return this;
    }

    public KartuIbuClient withNumberOfLivingChildren(String numberLivingChildren) {
        this.numberLivingChildren = numberLivingChildren;
        return this;
    }

    public KartuIbuClient withNumberOfAbortions(String numberOfAbortions) {
        this.numberOfAbortions = numberOfAbortions;
        return this;
    }

    public KartuIbuClient withKbMethod(String kbMethod) {
        this.kbMethod = kbMethod;
        return this;
    }

    public KartuIbuClient withHighRiskFromANC(boolean isHighRisk) {
        setIsHighRiskFromANC(isHighRisk);
        return this;
    }

    public KartuIbuClient withKbStart(String kbStart) {
        this.kbStart = kbStart;
        return this;
    }

    public KartuIbuClient withUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public KartuIbuClient withKBInformation(String kbMethod, String kbStart) {
        this.kbMethod = kbMethod;
        this.kbStart = kbStart;
        return this;
    }

    public KartuIbuClient addChild(KIChildClient childClient) {
        children.add(childClient);
        return this;
    }

    public List<KIChildClient> children() {
        return children;
    }

    public KartuIbuClient withIsHighRisk(String isHighRisk) {
        this.IsHighRisk = isHighRisk;
        return this;
    }

    public void setIsHighRiskANC(String isHighRiskANC) {
        this.isHighRiskANC = isHighRiskANC;
    }

    public KartuIbuClient withHighPriority(String isHighPriority) {
        this.isHighPriority = isHighPriority;
        return this;
    }

    @Override
    public String kbMethod() {
        return Strings.isNullOrEmpty(kbMethod) ? "-" : humanize(kbMethod);
    }

    @Override
    public String kbDate() {
        return Strings.isNullOrEmpty(kbStart) ? "-" : kbStart;
    }

    public KartuIbuClient withHighRiskLabour(String highRiskLabour) {
        setIsHighRiskLabour(!Strings.isNullOrEmpty(highRiskLabour) && highRiskLabour.equalsIgnoreCase("yes"));
        return this;
    }

    public KartuIbuClient withIsHighRiskPregnancy(String highRiskPregnancy) {
        setIsHighRiskPregnancy(!Strings.isNullOrEmpty(highRiskPregnancy) && highRiskPregnancy.equalsIgnoreCase("yes"));
        return this;
    }

    // Schedule Service
    public String motherId(){
        return ibuId;
    }

    public void setMotherId(String motherId) {
        this.ibuId = motherId;
    }

    public void setAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
    }

    public void setServicesProvided(List<ServiceProvidedDTO> servicesProvided) {
        this.services_provided = servicesProvided;
    }

    public Map<String, Visits> serviceToVisitsMap() {
        return serviceToVisitsMap;
    }

    private AlertDTO serviceToProvide(String category) {
        if (StringUtils.isBlank(category)) {
            return emptyAlert;
        }
        return serviceToVisitsMap.get(category).toProvide;
    }

    public AlertDTO getANCAlert(ANCServiceType type) {
        return serviceToProvide(type.category());
    }

    public AlertDTO getANCAlertByCategory(String category) {
        return serviceToProvide(category);
    }

    public ServiceProvidedDTO getANCServiceProvidedDTO(String serviceName) {
        for (ServiceProvidedDTO serviceProvidedDTO : services_provided) {
            if (serviceProvidedDTO.name().equalsIgnoreCase(serviceName)) {
                return serviceProvidedDTO;
            }
        }
        return null;
    }

    public void ancPreProcess() {
        initialize(SERVICE_CATEGORIES, serviceToVisitsMap);
        initializeAllServiceToProvideAndProvided(categoriesToServiceTypeMap);
    }

    private void initialize(String[] types, Map<String, Visits> serviceToVisitsMap) {
        for (String type : types) {
            serviceToVisitsMap.put(type, new Visits());
        }
    }

    private void initializeAllServiceToProvideAndProvided(Map<String, List<ANCServiceType>> categoriesToServiceTypeMap) {
        Set<String> keys = categoriesToServiceTypeMap.keySet();
        for (String key : keys) {
            initializeServiceToProvideAndProvided(categoriesToServiceTypeMap.get(key));
        }
    }

    private void initializeServiceToProvideAndProvided(List<ANCServiceType> types) {
        for (ANCServiceType type : types) {
            initializeServiceToProvideAndProvided(type);
        }
    }

    private void initializeServiceToProvideAndProvided(ANCServiceType type) {
        for (AlertDTO alert : alerts) {
            if (type.equals(alert.ancServiceType())) {
                serviceToVisitsMap.get(type.category()).toProvide = alert;
            }
        }

        for (ServiceProvidedDTO service : services_provided) {
            if (type.equals(service.ancServiceType())) {
                serviceToVisitsMap.get(type.category()).provided = service;
            }
        }
    }

}
