/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package USBDriver;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class USBLibTest {
    
    public USBLibTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of IsInitLib method, of class USBLib.
     */
    @Test
    public void testIsInitLib() {
        try {
            System.out.println("IsInitLib");
            USBLib.InitLib();
            int[] devs = USBLib.SearchUSBDev();
        } catch (Exception ex) {
            Logger.getLogger(USBLibTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
