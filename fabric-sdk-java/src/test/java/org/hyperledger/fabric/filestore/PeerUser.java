package org.hyperledger.fabric.filestore;

import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import io.netty.util.internal.StringUtil;


public class PeerUser implements User {
    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private Enrollment enrollment; 
    private String enrollmentSecret;

    PeerUser(String name) {
        this.name = name;
        this.roles = null;
        this.account = null;
        this.affiliation = null;
        this.enrollment = null;
        this.enrollmentSecret = null;
    }

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }
    
    public String getEnrollmentSecret() {
    	System.out.println("enrollmentSecret: " + enrollmentSecret);
        return enrollmentSecret;
    }
    
    public boolean isRegistered() {
        return !StringUtil.isNullOrEmpty(enrollmentSecret);
    }

    public boolean isEnrolled() {
        return this.enrollment != null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return this.enrollment;
    }
  
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    
    public void setMspId(String mspID) {
        this.mspId = mspID;
    }
    
    @Override
    public String getMspId() {
        return mspId;
    }

    String mspId;


}




