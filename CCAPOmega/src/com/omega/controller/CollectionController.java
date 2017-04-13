/*
 * CollectionController.java 7:44:54 PM Apr 30, 2010 version 1.0
 * 
 * Copyright (c) 1998-2010 Cognizant Technology Solutions, Inc. All Rights
 * Reserved.
 * 
 * This software is the confidential and proprietary information of Cognizant
 * Technology Solutions. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with Cognizant.
 */

package com.omega.controller;

import com.omega.dtd.Vehicle;
import com.omega.exception.InValidVehicleException;
import com.omega.exception.ServiceException;
import com.omega.dao.LookupDAO;
import com.omega.service.IPremiumCalculation;
import com.omega.service.PremiumCalculation;

import com.omega.utility.Validation;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author Java CoE
 * 
 *         Servlet Controller. Receives vehicle form data Validates for Business
 *         and data correctness and calculates the premium
 * @version 1.0
 */
 //DEMO
public class CollectionController extends javax.servlet.http.HttpServlet
{
    

    /** Serial Version for the class */
    static final long serialVersionUID = 1L;
    
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CollectionController.class.getName());
    
    /**
     * {@inheritDoc}
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {

        if (LOGGER.isDebugEnabled())
        {
            LOGGER
                    .debug("[CollectionController].df gd[doGet] Receiving Vechile form datas");
        }
        Validation validation = new Validation();
        IPremiumCalculation premium = new PremiumCalculation();
        Vehicle vehicleDetails;
        
        try
        {
            // validate data for correcteness and business conditions
            vehicleDetails = validation.constructValidVehicle(request);
            // calculate premium.
            vehicleDetails.setPremium(premium.calculatePremium(vehicleDetails));
            
        } catch (InValidVehicleException excep)
        {
            request.setAttribute("error", excep.getLocalizedMessage());
            throw new ServletException(excep);
            
        } catch (ServiceException exce)
        {
            request.setAttribute("error", exce.getLocalizedMessage());
            throw new ServletException(exce);
        }
        // once premium is calculated forward the request.
        request.setAttribute("insureVehicle", vehicleDetails);
        RequestDispatcher view = request
                .getRequestDispatcher("displayQuatation.jsp");
        view.forward(request, response);
        
    }
    
    
	public boolean supportsClass(Class clz) 
    { 
         return clz.isAssignableFrom(LookupDAO.class); 
    } 
				 
				 
    private void setMyName(){
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sds1111111111111111111111111111111111111");
    	System.out.println("sdded by uma");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds1");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds1");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");
    }

    private void setMyName1(){
		System.out.println("sds1");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");

    }

    private void setMyName1208(){
    	System.out.println("sds");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");
    }

    private void setMyName121(){
    	System.out.println("sds");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");
    	System.out.println("sds");
    	System.out.println("sds2");
    	System.out.println("sds3");
    	System.out.println("sds4");
    }

    private void setMyName2(){
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    }

    private void setMyName3(){
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    	System.out.println("sds");
    }

}
