
package com.omega.dao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.omega.dao.ILookupDAO;
import com.omega.dao.LookupDAO;

/**
 * @author 167522
 * 
 */
public class LookupDAOTest
{
    

    /**
     * 
     */
    @Test
    public final void testSaveVechicleQuotation()
    {

        ILookupDAO lookup = new LookupDAO();
        
        try
        {
            assertEquals(500+"", lookup.lookupBasePremium("BI", 5000)+"");
        } catch (SQLException e)
        {
            fail("SQL Exception occures");
        }
    }
    
}
