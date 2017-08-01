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
package com.omega.utility;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

import org.apache.tomcat.dbcp.dbcp.SQLNestedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;


/**
 *
 * @author Java CoE
 * 
 *         Contains methods for connecting and closing the database connction
 *         Update database.properties file in the classes folder for altering
 *         the database settings
 * @version 1.0
 */
public class DBConnection {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnection.class.getName());

    /**
     *
     * Using credentials in the database.properties connection to the database
     * is established using commons -dhcp library
     * @return connection object is returned
     * @throws SQLException thrown when unable to connect to database
     */
    public Connection getConnection() throws SQLException {
        BasicDataSource dbDataSouce = null;
        Connection dbConnection = null;

        Configuration config = null;

        try {
            config = getPropertiesConfig();
            dbDataSouce = new BasicDataSource();
            dbDataSouce.setDriverClassName(config.getString("driver"));
            dbDataSouce.setUsername(config.getString("username"));
            dbDataSouce.setPassword(config.getString("password"));
            dbDataSouce.setUrl(config.getString("url"));
            dbConnection = dbDataSouce.getConnection();
        } catch (ConfigurationException e1) {
            if (LOGGER.isErrorEnabled()) {
                // use {} to decide the location of the variable in the log
                // new Object[]{variable1, variable2}
                LOGGER.error("[CollectionController].[doGet] Unable to read or lcate the properties file{}",
                    new Object[] { e1, });
            }

            throw new SQLNestedException("Not able to read database.properties file",
                e1);
        }

        if (null == dbConnection) {
            throw new SQLException();
        }

        return dbConnection;
    }

    /*
     * used to load the properties file from the classpath.
     */
    private static Configuration getPropertiesConfig()
        throws ConfigurationException {
        return new PropertiesConfiguration("database.properties");
    }

    /**
     * closes a given connection
     *
     * @param DBConnection connection to close
     * @throws SQLException
     */
    public void close(Connection DBConnection) throws SQLException {
        DBConnection.close();
    }
}
