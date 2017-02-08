package org.ei.opensrp.core.db.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Obs {

	private String fieldType;
	private String fieldDataType;
	private String fieldCode;
	private String parentCode;
	private List<String> values;
	private List<String> humanReadableValues;
	private String comments;
	private String formSubmissionField;
	private DateTime effectiveDatetime;

	public Obs() { }

	public Obs(String fieldType, String fieldDataType, String fieldCode, String value, String humanReadableValue, String formSubmissionField) {
		this.setFieldType(fieldType);
		this.fieldDataType = fieldDataType;
		this.fieldCode = fieldCode;
		addToValueList(value, humanReadableValue);
		this.formSubmissionField = formSubmissionField;
	}

	public Obs(String fieldType, String fieldDataType, String fieldCode, String parentCode,
			List<String> values, List<String> humanReadableValues, String comments, String formSubmissionField) {
		this.setFieldType(fieldType);
		this.fieldDataType = fieldDataType;
		this.fieldCode = fieldCode;
		this.parentCode = parentCode;
		withValues(values, humanReadableValues);
		this.comments = comments;
		this.formSubmissionField = formSubmissionField;
	}

	public Obs(String fieldType, String fieldDataType, String fieldCode, String parentCode,
			String value, String humanReadableValue, String comments, String formSubmissionField) {
		this.setFieldType(fieldType);
		this.fieldDataType = fieldDataType;
		this.fieldCode = fieldCode;
		this.parentCode = parentCode;
		addToValueList(value, humanReadableValue);
		this.comments = comments;
		this.formSubmissionField = formSubmissionField;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldDataType() {
		return fieldDataType;
	}

	public void setFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getValue(boolean humanized) {
		if(values.size() > 1){
			throw new RuntimeException("Multiset values can not be handled like single valued fields. Use function getValues");
		}
		if(values == null || values.size() == 0){
			return null;
		}

		if(humanized && humanReadableValues != null && humanReadableValues.size() > 0){
			return humanReadableValues.get(0);
		} // otherwise values were same at insertion time

		return values.get(0);
	}

	public void setValue(String value, String humanizedValue) {
		addToValueList(value, humanizedValue);
	}

	public List<String> getValues(boolean humanized) {
		if(humanized && humanReadableValues != null && humanReadableValues.size() > 0){
			return humanReadableValues;
		} // otherwise values were same at insertion time

		return values;
	}

	public void setValues(List<String> values, List<String> humanReadableValues) {
		withValues(values, humanReadableValues);
	}

	/**
	 * Use getValues(boolean humanized) instead
	 * @return
	 */
	public List<String> getValues() {
		return values;
	}

	void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * Use getValues(boolean humanized) instead
	 * @return
	 */
	public List<String> getHumanReadableValues() {
		return humanReadableValues;
	}

	void setHumanReadableValues(List<String> values) {
		this.humanReadableValues = values;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getFormSubmissionField() {
		return formSubmissionField;
	}

	public void setFormSubmissionField(String formSubmissionField) {
		this.formSubmissionField = formSubmissionField;
	}

	public DateTime getEffectiveDatetime() {
		return effectiveDatetime;
	}

	public void setEffectiveDatetime(DateTime effectiveDatetime) {
		this.effectiveDatetime = effectiveDatetime;
	}

	public Obs withFieldType(String fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	public Obs withFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
		return this;
	}

	public Obs withFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
		return this;
	}

	public Obs withParentCode(String parentCode) {
		this.parentCode = parentCode;
		return this;
	}

	public Obs withValue(String value, String humanizedValue) {
		return addToValueList(value, humanizedValue);
	}

	public Obs withValues(List<String> values, List<String> humanReadableValues) {
		this.values = values;
		if(humanReadableValues != null && humanReadableValues.size() > 0 && humanReadableValues.equals(values) == false){
			this.humanReadableValues = humanReadableValues;
		}
		return this;
	}

	public Obs addToValueList(String value, String humanizedValue) {
		if(values == null){
			values = new ArrayList<>();
		}
		values.add(value);

		if(StringUtils.isNotBlank(humanizedValue) && humanizedValue.equals(value) == false){//only add if both are different
			if(humanReadableValues == null){
				humanReadableValues = new ArrayList<>();
			}
			humanReadableValues.add(humanizedValue);
		}
		return this;
	}

	public Obs withComments(String comments) {
		this.comments = comments;
		return this;
	}

	public Obs withFormSubmissionField(String formSubmissionField) {
		this.formSubmissionField = formSubmissionField;
		return this;
	}

	public Obs withEffectiveDatetime(DateTime effectiveDatetime) {
		this.effectiveDatetime = effectiveDatetime;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
