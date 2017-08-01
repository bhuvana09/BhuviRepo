
package com.omega.suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.omega.dao.test.LookupDAOTest;
import com.omega.dao.test.VehicleDAOTest;
import com.omega.service.test.MasterCalculationsTest;
import com.omega.service.test.PremiumCalculationTest;

/**
 * @author 167522
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { LookupDAOTest.class, VehicleDAOTest.class,
        PremiumCalculationTest.class, MasterCalculationsTest.class })
public class AllTests
{
    

    /**
     * @return integrate all test cases in here
     */
    public static Test suite()
    {

        return new JUnit4TestAdapter(AllTests.class);
    }
}
