package org.ei.opensrp.indonesia.view.contract;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ANCServiceType;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.Visits;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.ei.opensrp.indonesia.util.StringUtil.humanize;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIANCClient extends BidanSmartRegisterClient implements KIANCSmartRegisterClient {

    public static final String CATEGORY_ANC = "anc";
    public static final String CATEGORY_TT = "tt";
    public static final String CATEGORY_HB = "hb";

    private static final String[] SERVICE_CATEGORIES = {CATEGORY_ANC, CATEGORY_TT,
            CATEGORY_HB};

    private String entityId;
    private String kiNumber;
    private String uniqueId;
    private String puskesmas;
    private String name;
    private String ancNumber;
    private String dateOfBirth;
    private String husbandName;
    private boolean isHighRisk;
    private String riskFactors;
    private String edd;
    private String village;
    private String ancStatus;
    private String kunjungan;
    private String visitDate;
    private String ttImunisasi;
    private String BB;
    private String TB;
    private String beratBadan;
    private String LILA;
    private String penyakitKronis;
    private String alergi;
    private String tanggalHPHT;
    private String riwayatKomplikasiKebidanan;
    private String highRiskPregnancyReason;
    private String kartuIbuCaseId;
    private String ancVisitNumber;
    private Visits ancVisits;

    private List<AlertDTO> alerts;
    private List<ServiceProvidedDTO> services_provided;
    private String entityIdToSavePhoto;
    private Map<String, Visits> serviceToVisitsMap;

    public KIANCClient(String entityId, String village, String puskesmas, String name, String dateOfBirth) {
        this.entityId = entityId;
        this.puskesmas = puskesmas;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.village = village;
        this.serviceToVisitsMap = new HashMap<String, Visits>();
        this.ancVisits = new Visits();
    }

    @Override
    public String eddForDisplay() {
        if(Strings.isNullOrEmpty(edd)) return "-";
        if(edd.equalsIgnoreCase("invalid date")) return "-";

        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormat.forPattern("dd MMM YYYY");
        LocalDateTime date = parse(edd, formatter);

        return "" + date.toString(formatter2);
    }

    @Override
    public LocalDateTime edd() {
        if(Strings.isNullOrEmpty(edd)) return null;
        if(edd.equalsIgnoreCase("invalid date")) return null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-dd");
        return parse(edd, formatter);
    }

    @Override
    public String pastDueInDays() {
        return null;
    }

    @Override
    public AlertDTO getAlert(ANCServiceType type) {
        return null;
    }

    @Override
    public boolean isVisitsDone() {
        return false;
    }

    @Override
    public boolean isTTDone() {
        return false;
    }

    @Override
    public String visitDoneDateWithVisitName() {
        return null;
    }

    @Override
    public String ttDoneDate() {
        return null;
    }

    @Override
    public String ifaDoneDate() {
        return null;
    }

    @Override
    public String ancNumber() {
        return null;
    }

    @Override
    public String riskFactors() {
        return riskFactors == null ? "-" : humanize(riskFactors);
    }

    @Override
    public ServiceProvidedDTO serviceProvidedToACategory(String category) {
        return null;
    }

    @Override
    public String getHyperTension(ServiceProvidedDTO ancServiceProvided) {
        return null;
    }

    @Override
    public ServiceProvidedDTO getServiceProvidedDTO(String serviceName) {
        return null;
    }

    @Override
    public List<ServiceProvidedDTO> allServicesProvidedForAServiceType(String serviceType) {
        return null;
    }

    @Override
    public String entityId() {
        return entityId;
    }

    @Override
    public String name() {
        return Strings.isNullOrEmpty(name) ? "-" : humanize(name);
    }

    @Override
    public String displayName() {
        return name();
    }

    @Override
    public String village() {
        return Strings.isNullOrEmpty(village) ? "" : humanize(village);
    }

    @Override
    public String wifeName() {
        return name();
    }

    @Override
    public String husbandName() {
        return Strings.isNullOrEmpty(husbandName) ? "-" : husbandName;
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
        return false;
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

    @Override
    public boolean satisfiesFilter(String filterCriterion) {
        return name.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || husbandName.toLowerCase(Locale.getDefault()).startsWith(filterCriterion.toLowerCase())
                || String.valueOf(kiNumber).startsWith(filterCriterion)
                || String.valueOf(puskesmas).startsWith(filterCriterion);
    }

    @Override
    public int compareName(SmartRegisterClient client) {
        return 0;
    }

    public String getPuskesmas() {
        return Strings.isNullOrEmpty(puskesmas) ? "-" : puskesmas;
    }

    public String kiNumber() {
        return Strings.isNullOrEmpty(kiNumber) ? "-" : kiNumber;
    }

    public String getUniqueId() {
        return Strings.isNullOrEmpty(uniqueId) ? "-" : uniqueId;
    }

    public String ancStatus() { return humanize(ancStatus); }

    public String ttImunisasi() { return ttImunisasi == null ? "-" : humanize(ttImunisasi); }

    public String kunjungan() { return kunjungan == null ? "-" : humanize(kunjungan); }

    public String usiaKlinis() {
        if(Strings.isNullOrEmpty(tanggalHPHT)) return "-";

        LocalDate startDate = DateUtil.getLocalDateFromISOString(tanggalHPHT);
        LocalDate endDate = DateUtil.today();

        return DateUtil.weekDifference(startDate, endDate) + " minggu";
    }

    public KIANCClient withHusband(String husbandName) {
        this.husbandName = husbandName;
        return this;
    }

    public KIANCClient withKINumber(String kiNumber) {
        this.kiNumber = kiNumber;
        return this;
    }

    public KIANCClient withUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }

    public KIANCClient withEDD(String edd) {
        this.edd = edd;
        return this;
    }

    public KIANCClient withANCStatus(String ancStatus) {
        this.ancStatus = ancStatus;
        return this;
    }

    public KIANCClient withKunjunganData(String kunjungan) {
        this.kunjungan = kunjungan;
        return this;
    }

    public KIANCClient withVisitDate(String visitDate) {
        this.visitDate = visitDate;
        return this;
    }

    public KIANCClient withTTImunisasiData(String ttImunisasi) {
        this.ttImunisasi = ttImunisasi;
        return this;
    }

    public KIANCClient withTanggalHPHT(String tanggalHPHT){
        this.tanggalHPHT = tanggalHPHT;
        return this;
    }

    public String getTB() {
        return TB;
    }

    public void setTB(String TB) {
        this.TB = TB;
    }

    public String getBB() {
        return BB;
    }

    public void setBB(String BB) {
        this.BB = BB;
    }

    public String getBeratBadan() {
        return beratBadan;
    }

    public void setBeratBadan(String beratBadan) {
        this.beratBadan = beratBadan;
    }

    public String getLILA() {
        return LILA;
    }

    public void setLILA(String LILA) {
        this.LILA = LILA;
    }

    public String getPenyakitKronis() {
        return Strings.isNullOrEmpty(penyakitKronis) ? "" : humanize(penyakitKronis);
    }

    public void setPenyakitKronis(String penyakitKronis) {
        this.penyakitKronis = penyakitKronis;
    }

    public String getAlergi() {
        return Strings.isNullOrEmpty(alergi) ? "" : StringUtil.humanize(alergi);
    }

    public void setAlergi(String alergi) {
        this.alergi = alergi;
    }

    public String tanggalHPHT() {
        return Strings.isNullOrEmpty(tanggalHPHT) ? "-" : DateUtil.formatDate(tanggalHPHT, "dd MMM YYYY");
    }

    public void setHighRiskLabour(String highRiskLabour) {
        setIsHighRiskLabour(!Strings.isNullOrEmpty(highRiskLabour) && highRiskLabour.equalsIgnoreCase("yes"));
    }

    public void setHigRiskPregnancyReason(String highRiskPregnancyReason) {
        this.highRiskPregnancyReason = highRiskPregnancyReason;
    }

    public String getHighRiskPregnancyReason() {
        return Strings.isNullOrEmpty(highRiskPregnancyReason) ? "" : humanize(highRiskPregnancyReason) + ",";
    }

    public void setRiwayatKomplikasiKebidanan(String riwayatKomplikasiKebidanan) {
        this.riwayatKomplikasiKebidanan = riwayatKomplikasiKebidanan;
    }

    public String getRiwayatKomplikasiKebidanan() {
        return Strings.isNullOrEmpty(riwayatKomplikasiKebidanan)? "" : humanize(this.riwayatKomplikasiKebidanan);
    }

    public String getKartuIbuCaseId() {
        return kartuIbuCaseId;
    }

    public void setKartuIbuCaseId(String kartuIbuCaseId) {
        this.kartuIbuCaseId = kartuIbuCaseId;
    }

    public String getAncVisitNumber() {
        return ancVisitNumber;
    }

    public String visitDate() {
        return Strings.isNullOrEmpty(visitDate) ? "-" : DateUtil.formatDate(tanggalHPHT, "dd/MM/YYYY");
    }

    public void setAncVisitNumber(String ancVisitNumber) {
        this.ancVisitNumber = ancVisitNumber;
    }

    public void setAlerts(List<AlertDTO> alerts) {
        this.alerts = alerts;
    }

    public void ancPreProcess() {
        this.ancVisits.toProvide = AlertDTO.emptyAlert;
        for(AlertDTO alert : alerts) {
            this.ancVisits.toProvide = alert;
        }
    }

    public AlertDTO getANCAlert() {
        return ancVisits.toProvide;
    }

}
