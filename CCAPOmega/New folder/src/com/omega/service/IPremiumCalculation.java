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

package com.omega.service;

import com.omega.dtd.Vehicle;
import com.omega.exception.ServiceException;

/**
 * 
 * @author Java CoE
 * 
 *         Contains methods for calculating insurance premium
 * @version 1.0
 */
public interface IPremiumCalculation
{
    

    /**
     * 
     * 
     * @param vehicle Vehicle entity with uncalculated premium
     * @return returns the premium excluding tax
     * @throws ServiceException Thrown when business rule fails or when unable
     *             to access resources
     */
    Double calculatePremium(Vehicle vehicle) throws ServiceException;
}
