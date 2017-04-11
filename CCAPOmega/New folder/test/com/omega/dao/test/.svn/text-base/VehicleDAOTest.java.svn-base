
package com.omega.dao.test;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.omega.dao.IVehicleDAO;
import com.omega.dao.VehicleDAO;
import com.omega.test.utils.VechicleUtility;

/**
 * @author 167522
 * 
 */
public class VehicleDAOTest extends VechicleUtility
{
    

    /**
     * 
     */
    @Test
    public void testSaveVechicleQuotation()
    {

        IVehicleDAO dao = new VehicleDAO();
        try
        {
            dao.saveVechicleQuotation(this.testVechile);
            assert (true); // incase of success no exceptions will be thrown
        } catch (SQLException e)
        {
            fail("SQL Exception occures");
        }
    }
}
