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

package com.omega.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.omega.utility.DBConnection;

/**
 * 
 * @author Java CoE
 * 
 *         Contains methods to fetch reference data from the database.
 * @version 1.0
 */
public class LookupDAO implements ILookupDAO
{
    
/** Logger */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(LookupDAO.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public Integer lookupBasePremium(String type, Integer amount)
            throws SQLException
    {

        if (LOGGER.isDebugEnabled())
        {
            LOGGER
                    .debug(
                            "[LookupDAO].[lookupBasePremium] Insurence type {} Coverage {}",
                            new Object[] { type, amount });
        }
        // premium fetched from database
        Integer basePremium = 0;
        
        // connection , statement and result set objects
        DBConnection dbConnection = new DBConnection();
        ResultSet dbResultSet = null;
        PreparedStatement psmt = null;
        Connection connection = null;
        
        /*
         * Fetch base premium for a combination of insurance type and required
         * coverage
         */
        String psmtQuery =
                "select BASEPREMIUM from base_premium where COVERAGETYPE = ? and COVERAGEAMOUNT = ?";
        try
        {
            connection = dbConnection.getConnection();
            
            psmt = connection.prepareStatement(psmtQuery);
            psmt.setString(1, type);
            psmt.setInt(2, amount);
            dbResultSet = psmt.executeQuery();
            // extract the base premium
            if (dbResultSet.next())
            {
                basePremium = Integer
                        .parseInt(dbResultSet.getString("BASEPREMIUM"));
                // close the connection. If unclosed not thrown as error but
                // logged.
            }
        } finally
        {
            try
            {
                if (dbResultSet != null)
                {
                    dbResultSet.close();
                }
                if (null != psmt)
                {
                    psmt.close();
                }
                if (connection != null)
                {
                    connection.close();
                }
            } catch (SQLException e)
            {
                if (LOGGER.isErrorEnabled())
                {
                    LOGGER.error(
                            "[CollectionController].[doGet] SQLException{}",
                            new Object[] { e.getMessage() });
                }
            }
        }
        
        return basePremium;
    }
    
}
