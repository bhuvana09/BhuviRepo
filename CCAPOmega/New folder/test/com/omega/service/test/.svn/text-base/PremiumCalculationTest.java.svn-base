
package com.omega.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.omega.exception.ServiceException;
import com.omega.service.PremiumCalculation;
import com.omega.test.utils.VechicleUtility;

/**
 * @author 167522
 * 
 */
public class PremiumCalculationTest extends VechicleUtility
{
    

    /**
     * 
     */
    @Test
    public void testCalculatePremium()
    {

        PremiumCalculation calculation = new PremiumCalculation();
        try
        {
            assertEquals(127.27272727272728, calculation
                    .calculatePremium(this.testVechile));
        } catch (ServiceException e)
        {
            fail("SQL Exception occures");
        }
    }
    
}
