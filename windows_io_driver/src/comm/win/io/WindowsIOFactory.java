/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.win.io;

import USBDriver.USBLib;
import gnu.io.CommPortIdentifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.io.IOInfo;
import nahon.comm.io.AbstractIO;

/**
 *
 * @author jiche
 */
public class WindowsIOFactory {

    // <editor-fold defaultstate="collapsed" desc="创建IO"> 
    public enum IOTYPE {
        COM,
        TCP,
        USB
    }

    public static AbstractIO CreateIO(IOInfo con) {
        if (!isInited) {
            try {
                InitWindowsIODriver();
            } catch (Exception ex) {
                Logger.getGlobal().log(Level.SEVERE, null, ex);
            }
        }
        if (con == null) {
            return null;
        }

        AbstractIO newio;
        switch (IOTYPE.valueOf(con.iotype)) {
            case COM:
                newio = new IO_COM(con.par[0], Integer.valueOf(con.par[1]));
                break;
            case TCP:
                newio = new IO_TCP(con.par[0], Integer.valueOf(con.par[1]));
                break;
            case USB:
                newio = new IO_USB(Integer.valueOf(con.par[0]));
                break;
            default:
                return null;
        }
//        iolist.add(newio);
        return newio;
    }

    public static IOTYPE GetIOtype(IOInfo con) {
        return IOTYPE.valueOf(con.iotype);
    }
    // </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc="初始化驱动"> 
    private static boolean isInited = false;

    public static void InitWindowsIODriver(boolean clean) throws Exception {
        if (!isInited) {
            IO_COM.InitLib(clean);
            USBLib.InitLib(clean);
            USBLib.SearchUSBDev();
            isInited = true;
        }
    }

    public static void InitWindowsIODriver() throws Exception {
        InitWindowsIODriver(false);
    }
    // </editor-fold> 

    public static String[] listCOM() throws Exception {
        if (!isInited) {
            InitWindowsIODriver(false);
        }
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        /* Clean USB and Comm Input */
        ArrayList<String> ret = new ArrayList();

        /* Foud Comm port */
        while (portList.hasMoreElements()) {
            CommPortIdentifier comportId = (CommPortIdentifier) portList.nextElement();

            /* If name is start with NT, it is an virtual USB comm port */
            ret.add(comportId.getName());
        }

        return ret.toArray(new String[0]);
    }

    public static IOInfo[] listAllUSB() throws Exception {
        if (!isInited) {
            InitWindowsIODriver(false);
        }
        USBLib.InitLib();
        ArrayList<IOInfo> ret = new ArrayList();
        for (int i : USBLib.SearchUSBDev()) {
            ret.add(new IOInfo(WindowsIOFactory.IOTYPE.USB.toString(), i + ""));
        }

        return ret.toArray(new IOInfo[0]);
    }

    public static void main(String[] args) {
        try {
            // System.out.println(System.getProperty("user.dir"));
            WindowsIOFactory.InitWindowsIODriver();
        } catch (Exception ex) {
            Logger.getLogger(WindowsIOFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
