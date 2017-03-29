package org.ei.opensrp.core.db.domain;

import java.util.Map;

import org.joda.time.DateTime;

public class DrugOrder extends BaseDataObject {
	private String _id;
	private String baseEntityId;
	private Map<String, String> codes;
	private String orderType;
	private String drug;
	private String orderNumber;
	private String action;
	private String previousOrder;
	private DateTime dateActivated;
	private String discontinuedBy;
	private DateTime discontinuedDate;
	private String discontinuedReason;
	private DateTime autoExpireDate;
	private String urgency;
	private String instructions;
	private String orderReason;
	private String dosingType;
	private String dose;
	private String frequency;
	private String descriptions;
	private String quantity;
	private String orderer;
	private String route;
	private String quantityUnits;
	 
	public DrugOrder(String baseEntityId,String ordererName,String drug,String orderType, Map<String, String> codes,
			String orderNumber, String action, String previousOrder
			, DateTime dateActivated, DateTime autoExpireDate,String urgency,
			String instructions, String dosingType, String description,String quantity,String route, String quantityUnits)
	{
		this.baseEntityId=baseEntityId;
		this.orderer=ordererName;
		this.drug=drug;
		this.orderType=orderType;
		this.codes=codes;
		this.orderNumber=orderNumber;
		this.action=action;
		this.previousOrder=previousOrder;
		this.dateActivated=dateActivated;
		this.autoExpireDate=autoExpireDate;
		this.urgency=urgency;
		this.instructions=instructions;
		this.dosingType=dosingType;
		this.descriptions=description;
		this.quantity=quantity;
		this.route=route;
		this.quantityUnits=quantityUnits;
	}
	
	public DrugOrder(String baseEntityId, String orderType, String drug, Map<String, String> codes,
			DateTime dateActivated, DateTime autoExpireDate, String frequency, String quantity, String quantityUnits,
			String orderNumber, String action, String urgency, String instructions, String dose, String dosingType,
			String previousOrder, String discontinuedBy, DateTime discontinuedDate, String discontinuedReason,
			String orderReason, String orderer, String route, String descriptions) {
		super();
		this.baseEntityId = baseEntityId;
		this.orderType = orderType;
		this.drug = drug;
		this.codes = codes;
		this.dateActivated = dateActivated;
		this.autoExpireDate = autoExpireDate;
		this.frequency = frequency;
		this.quantity = quantity;
		this.quantityUnits = quantityUnits;
		this.orderNumber = orderNumber;
		this.action = action;
		this.urgency = urgency;
		this.instructions = instructions;
		this.dose = dose;
		this.dosingType = dosingType;
		this.previousOrder = previousOrder;
		this.discontinuedBy = discontinuedBy;
		this.discontinuedDate = discontinuedDate;
		this.discontinuedReason = discontinuedReason;
		this.orderReason = orderReason;
		this.orderer = orderer;
		this.route = route;
		this.descriptions = descriptions;
	}

	public DrugOrder()
	{
		
	}
	
	public DrugOrder(String drug)
	{
		this.drug=drug;
	}
	public Map<String, String> getCodes() {
		return codes;
	}


	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public void setCodes(Map<String, String> codes) {
		this.codes = codes;
	}

	
	 
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPreviousOrder() {
		return previousOrder;
	}

	public void setPreviousOrder(String previousOrder) {
		this.previousOrder = previousOrder;
	}

	public DateTime getDateActivated() {
		return dateActivated;
	}

	public void setDateActivated(DateTime dateActivated) {
		this.dateActivated = dateActivated;
	}

	public DateTime getAutoExpireDate() {
		return autoExpireDate;
	}

	public void setAutoExpireDate(DateTime autoExpireDate) {
		this.autoExpireDate = autoExpireDate;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getOrderReason() {
		return orderReason;
	}

	public void setOrderReason(String orderReason) {
		this.orderReason = orderReason;
	}

	public String getDiscontinuedBy() {
		return discontinuedBy;
	}

	public void setDiscontinuedBy(String discontinuedBy) {
		this.discontinuedBy = discontinuedBy;
	}

	public DateTime getDiscontinuedDate() {
		return discontinuedDate;
	}

	public void setDiscontinuedDate(DateTime discontinuedDate) {
		this.discontinuedDate = discontinuedDate;
	}

	public String getDiscontinuedReason() {
		return discontinuedReason;
	}

	public void setDiscontinuedReason(String discontinuedReason) {
		this.discontinuedReason = discontinuedReason;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getBaseEntityId() {
		return baseEntityId;
	}

	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}

	public String getOrderer() {
		return orderer;
	}

	public void setOrderer(String orderer) {
		this.orderer = orderer;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getQuantityUnits() {
		return quantityUnits;
	}

	public void setQuantityUnits(String quantityUnits) {
		this.quantityUnits = quantityUnits;
	}

	public String getDosingType() {
		return dosingType;
	}

	public void setDosingType(String dosingType) {
		this.dosingType = dosingType;
	}

	public String getDose() {
		return dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
}
