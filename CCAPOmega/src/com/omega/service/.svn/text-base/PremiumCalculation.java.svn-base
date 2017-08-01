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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omega.dao.ILookupDAO;
import com.omega.dao.IVehicleDAO;
import com.omega.dao.LookupDAO;
import com.omega.dao.VehicleDAO;
import com.omega.dtd.Vehicle;
import com.omega.exception.ServiceException;

/**
 * 
 * @author Java CoE
 * 
 *         Contains methods for calculating insurance premium
 * @version 1.0
 */
public class PremiumCalculation implements IPremiumCalculation
{
    

    /** Logger */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PremiumCalculation.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public Double calculatePremium(Vehicle vehicle) throws ServiceException
    {

        ILookupDAO lookup = new LookupDAO();
        IVehicleDAO vehicleDAO = new VehicleDAO();
        // Risk factor is calculated depending on the age and accidents for the
        // most recent year
        double riskFactor = ((double) vehicle.getAge() / (22 * 10))
                + ((double) vehicle.getLstYearAccidents() / 10);
        int basePremium;
        double finalPremium;
        try
        {
            // fixed premium based on the combination of insurance type and
            // coverage required
            basePremium = lookup.lookupBasePremium(vehicle.getCoverageType(),
                    vehicle.getCoverageAmount());
            // final premium depends on the risk factor
            finalPremium = basePremium * riskFactor;
            if (LOGGER.isDebugEnabled())
            {
                // use {} to decide the location of the variable in the log
                // new Object[]{variable1, variable2}
                LOGGER
                        .debug(
                                "[PremiumCalculation].[calculatePremium] Final Premium{}",
                                new Object[] { finalPremium });
            }
            vehicle.setPremium(finalPremium);
            // save the premium before displaying to the end user.
            vehicleDAO.saveVechicleQuotation(vehicle);
        } catch (SQLException e)
        {
            if (LOGGER.isErrorEnabled())
            {
                LOGGER.error("[CollectionController].[doGet] SQLException {}",
                        new Object[] { e.getMessage() });
            }
            throw new ServiceException("FS_103", "Unable to Process Request", e);
        }
        
        return finalPremium;
    }
}
