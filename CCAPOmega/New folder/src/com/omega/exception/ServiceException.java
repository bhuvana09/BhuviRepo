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

package com.omega.exception;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This InValidVehicleException class will be exception class thrown from
 * services with context and nested exception, which will be caught in the
 * clients like UI.
 * <p/>
 * InValidVehicleException will be thrown with appropriate comments
 * 
 * @author Java CoE
 * @version 1.0
 */
public class ServiceException extends Exception
{
    

    /** Resource bundle file name. */
    private static final String RESOURCE_BUNDLE = "exception_messages";
    
    /** Serial version UID. */
    private static final long serialVersionUID = 2159675965932131060L;
    
    /** Exception code. */
    private String exceptionCode = null;
    
    /** Exception context. */
    private String exceptionContext = null;
    
    /**
     * Logger
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ServiceException.class);
    
    /**
     * The Constructor is a convenient method to create the exception without
     * the the throwable object. In case a new exception to be created from
     * scratch this constructor would be used.
     * 
     * @param exceptionCode This denotes any rule violation or a business rule
     *            failure This error code format is "MOD_nnnn", for example
     *            "FL_1000".
     * @param exceptionContext The context in which the error occured are
     *            provided here. For example, "the folder id is '4567' "
     */
    public ServiceException(String exceptionCode, String exceptionContext)
    {

        this.exceptionCode = exceptionCode;
        this.exceptionContext = exceptionContext;
    }
    
    /**
     * The Constructor is a convenient method to create the exception with the
     * the throwable object, from the parent exception.
     * 
     * @param exceptionCode This denotes any rule violation or a business rule
     *            failure This error code format is "MOD_nnnn", for example
     *            "FL_1000".
     * @param exceptionContext The context in which the error occured are
     *            provided here. For example, "the folder id is '4567'"
     * @param cause the parent exception is nested with this exception, so the
     *            root cause is not lost
     */
    public ServiceException(String exceptionCode, String exceptionContext,
            Throwable cause)
    {

        this(exceptionCode, exceptionContext);
        this.initCause(cause);
    }
    
    /**
     * 
     * TODO explaing the business in the method input , output parameters thrown
     * exceptions
     * @return string
     */
    public String getExceptionCode()
    {

        return this.exceptionCode;
    }
    
    /**
     * 
     * TODO explaing the business in the method input , output parameters thrown
     * exceptions
     * @param exceptionCode
     */
    public void setExceptionCode(String exceptionCode)
    {

        this.exceptionCode = exceptionCode;
    }
    
    /**
     * 
     * TODO explaing the business in the method input , output parameters thrown
     * exceptions
     * @return string
     */
    public String getExceptionContext()
    {

        return this.exceptionContext;
    }
    
    /**
     * 
     * TODO explaing the business in the method input , output parameters thrown
     * exceptions
     * @param exceptionContext
     */
    public void setExceptionContext(String exceptionContext)
    {

        this.exceptionContext = exceptionContext;
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public String getMessage()
    {

        return (exceptionContext != null ? String.format("[%1$s] %2$s",
                exceptionCode, exceptionContext)
                : String.format("[%1$s]", exceptionCode));
    }
    
    /**
     * {@inheritDoc}.
     */
    @Override
    public String getLocalizedMessage()
    {

        return (exceptionContext != null ? String.format("%1$s, %2$s",
                getMessageByCode(), exceptionContext)
                : String.format("%1$s", getMessageByCode()));
    }
    
    /**
     * Returns exception message by it's code from resource bundle.
     * 
     * @return exception message by it's code
     */
    private String getMessageByCode()
    {

        String rst = null;
        
        try
        {
            rst =
                    ResourceBundle.getBundle(RESOURCE_BUNDLE, Locale.US)
                    .getString(exceptionCode);
        } catch (MissingResourceException e)
        {
            LOGGER.error(e.getMessage(), e);
            rst = exceptionCode;
        }
        return rst;
    }
    
}
