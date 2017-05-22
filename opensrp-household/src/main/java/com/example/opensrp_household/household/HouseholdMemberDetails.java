package com.example.opensrp_household.household;

import org.ei.opensrp.commonregistry.CommonPersonObject;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberDetails {

    public String programId;

    public String memberName;

    public String contact;

    public String memberRegister;

    public String memberAge;

    public String memberRelationWithHousehold;

    public int memberImageId;

    public String btnFollowup;

    public String memberGender;

    public boolean cantBeEnrolled;

    public boolean memberExists;

    public boolean ancEnrollment;

    public CommonPersonObject client;

    public String contact(){return contact;}

    public void setContact(String contact){this.contact = contact;}

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberRegister() {
        return memberRegister;
    }

    public void setMemberRegister(String memberRegister) {
        this.memberRegister = memberRegister;
    }

    public String getMemberAge() {
        return memberAge;
    }

    public void setMemberAge(String memberAge) {
        this.memberAge = memberAge;
    }

    public String getMemberRelationWithHousehold() {
        return memberRelationWithHousehold;
    }

    public void setMemberRelationWithHousehold(String memberRelationWithHousehold) {
        this.memberRelationWithHousehold = memberRelationWithHousehold;
    }

    public int getMemberImageId() {
        return memberImageId;
    }

    public void setMemberImageId(int memberImageId) {
        this.memberImageId = memberImageId;
    }

    public String getBtnFollowup() {
        return btnFollowup;
    }

    public void setBtnFollowup(String btnFollowup) {
        this.btnFollowup = btnFollowup;
    }

    public boolean isMemberExists() {
        return memberExists;
    }

    public void setMemberExists(boolean memberExists) {
        this.memberExists = memberExists;
    }

    public CommonPersonObject getClient() {
        return client;
    }

    public void setClient(CommonPersonObject client) {
        this.client = client;
    }

    public String getMemberGender() {
        return memberGender;
    }

    public void setMemberGender(String memberGender) {
        this.memberGender = memberGender;
    }

    public boolean isCantBeEnrolled() {
        return cantBeEnrolled;
    }

    public void setCantBeEnrolled(boolean cantBeEnrolled) {
        this.cantBeEnrolled = cantBeEnrolled;
    }

    public boolean isAncEnrollment() {
        return ancEnrollment;
    }

    public void setAncEnrollment(boolean ancEnrollment) {
        this.ancEnrollment = ancEnrollment;
    }
}
