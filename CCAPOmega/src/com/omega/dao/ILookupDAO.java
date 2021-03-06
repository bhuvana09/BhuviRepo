/*
 * ILookupDAO.java 7:44:54 PM Apr 30, 2010 version 1.0
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

import java.sql.SQLException;


/**
 *
 * @author Java CoE
 *
 *         Contains methods to fetch reference data from the database.
 *
 * @version 1.0
 */
public interface ILookupDAO {
    /**
     * lookupBasePremium fetched base premium from reference data in the
     * database using a combination of Insurance type and the amount required
     * for the insurence
     *
     * @param type Insurance Type BI,CI etc.,
     * @param amount Standard Amounts of insurences required
     * @return return base premium
     * @throws SQLException thrown if unable to connect to database or unable to
     *             run the query with the input data
     */
    Integer lookupBasePremium(String type, Integer amount)
        throws SQLException;
}
