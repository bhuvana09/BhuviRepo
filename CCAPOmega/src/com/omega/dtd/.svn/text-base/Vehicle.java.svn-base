/*
 * IVehicleDAO.java 7:44:54 PM Apr 30, 2010 version 1.0
 *
 * Copyright (c) 1998-2010 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */
package com.omega.dtd;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;


/**
 *
 * @author Java CoE
 *
 * Java Bean Representing Vehicle Entity.
 * @version 1.0
 */
public class Vehicle {
    private String name;
    private Integer ssn;
    private Integer age;
    private String gender;
    private String vehicleModel;
    private String make;
    private Integer year;
    private String licenseNumber;
    private Integer lstYearAccidents;
    private String coverageType;
    private Integer coverageAmount;
    private Double premium;
    private Date date;
    private String errorMessage;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ssn
     */
    public Integer getSsn() {
        return ssn;
    }

    /**
     * @param ssn the ssn to set
     */
    public void setSsn(Integer ssn) {
        this.ssn = ssn;
    }

    /**
     * @return the age
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age the age to set
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return the gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * @return the vehicleModel
     */
    public String getVehicleModel() {
        return vehicleModel;
    }

    /**
     * @param vehicleModel the vehicleModel to set
     */
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    /**
     * @return the make
     */
    public String getMake() {
        return make;
    }

    /**
     * @param make the make to set
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * @return the licenseNumber
     */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * @param licenseNumber the licenseNumber to set
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * @return the lstYearAccidents
     */
    public Integer getLstYearAccidents() {
        return lstYearAccidents;
    }

    /**
     * @param lstYearAccidents the lstYearAccidents to set
     */
    public void setLstYearAccidents(Integer lstYearAccidents) {
        this.lstYearAccidents = lstYearAccidents;
    }

    /**
     * @return the coverageType
     */
    public String getCoverageType() {
        return coverageType;
    }

    /**
     * @param coverageType the coverageType to set
     */
    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }

    /**
     * @return the coverageAmount
     */
    public Integer getCoverageAmount() {
        return coverageAmount;
    }

    /**
     * @param coverageAmount the coverageAmount to set
     */
    public void setCoverageAmount(Integer coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    /**
     * @return the premium
     */
    public Double getPremium() {
        return premium;
    }

    /**
     * @param premium the premium to set
     */
    public void setPremium(Double premium) {
        this.premium = premium;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.getSsn())
                                          .append(this.getLicenseNumber())
                                          .append(this.getVehicleModel())
                                          .toHashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = false;

        if ((obj != null) && Vehicle.class.isAssignableFrom(obj.getClass())) {
            Vehicle vehicle = (Vehicle) obj;
            equals = new EqualsBuilder().append(this.getName(),
                    vehicle.getName())
                                        .append(this.getCoverageType(),
                    vehicle.getCoverageType())
                                        .append(this.getSsn(), vehicle.getSsn())
                                        .append(this.getVehicleModel(),
                    vehicle.getVehicleModel()).isEquals();
        }

        return equals; 
    }
}
