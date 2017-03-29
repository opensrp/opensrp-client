package org.ei.opensrp.core.db.domain;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Drug extends BaseDataObject {
	private String drugName;
	private String drugBaseName;
	private Map<String, String> codes;
	private String dosageForm;
	private String route;
	private String id;
	private String doseStrength;
	private String units;
	private String maximumDailyDose;
	private String minimumDailyDose;
	private String description;
	private String combination;
		
	protected Drug() {
		
	}

	public Drug(String baseEntityId) {
		
	}
	public Drug(String drugName,String drugBaseName, Map<String, String> codes,
			String route, String dosageForm, String doseStrenght
			, String units, String maxDailyDose,String miniDailyDose,String Description,String combination) {
		this.drugName = drugName;
		this.drugBaseName = drugBaseName;
		this.codes = codes;
		this.doseStrength = doseStrenght;
		this.route = route;
		this.dosageForm = dosageForm;
		this.maximumDailyDose = maxDailyDose;
		this.minimumDailyDose = miniDailyDose;
		this.description = Description;
		this.combination = combination;
	}
	@Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }              
	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public Map<String, String> getCodes() {
		return codes;
	}

	public void setCodes(Map<String, String> codes) {
		this.codes = codes;
	}

	public String getDosageForm() {
		return dosageForm;
	}

	public void setDosageForm(String dosageForm) {
		this.dosageForm = dosageForm;
	}

	public String getDrugBaseName() {
		return drugBaseName;
	}

	public void setDrugBaseName(String drugBaseName) {
		this.drugBaseName = drugBaseName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoseStrength() {
		return doseStrength;
	}

	public void setDoseStrength(String doseStrength) {
		this.doseStrength = doseStrength;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getMaximumDailyDose() {
		return maximumDailyDose;
	}

	public void setMaximumDailyDose(String maximumDailyDose) {
		this.maximumDailyDose = maximumDailyDose;
	}

	public String getMinimumDailyDose() {
		return minimumDailyDose;
	}

	public void setMinimumDailyDose(String minimumDailyDose) {
		this.minimumDailyDose = minimumDailyDose;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}	
}
