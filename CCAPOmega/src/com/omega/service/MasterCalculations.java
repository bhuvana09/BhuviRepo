
package com.omega.service;

/**
 * 
 * @author 187925
 * 
 */
public class MasterCalculations
{
    
private static String name=null;
	
    /**
     * 
     * @param i
     * @param b
     * @return string
     */
    public static String getBigger(int i, int b)
    {
        String aa = "";
        if (b == 0)
        {
            aa = "b zero";
        }
        else if (i < b)
        {
            aa = "less than";
        }
        else if (b < i)
        {
            aa = "greater than";
        }
        else
        {
            aa = "equals";
        }
        
        return aa;
    }

    public static String getBiggerB(int i, int b)
    {
        String aa = "";
        if (b == 0)
        {
            aa = "b zero";
        }
        else if (i < b)
        {
            aa = "less than";
        }
        else if (b < i)
        {
            aa = "greater than";
        }
        else
        {
        	System.out.println("aa"+aa);
            aa = "equals";
        }
        
        return aa;
    }
}
