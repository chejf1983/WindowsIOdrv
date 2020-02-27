/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.win.io;

import USBDriver.USBLib;
import java.util.logging.Level;
import java.util.logging.Logger;
import comm.absractio.WIOInfo;
import comm.absractio.WAbstractIO;

/**
 *
 * @author jiche
 */
public class WindowsIOFactory {

    public enum IOTYPE {
        COM,
        TCP,
        USB
    }

    public static WAbstractIO CreateIO(WIOInfo con) {
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

        WAbstractIO newio;
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

    public static IOTYPE GetIOtype(WIOInfo con) {
        return IOTYPE.valueOf(con.iotype);
    }

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

    public static void main(String[] args) {
        try {
            // System.out.println(System.getProperty("user.dir"));
            WindowsIOFactory.InitWindowsIODriver();
        } catch (Exception ex) {
            Logger.getLogger(WindowsIOFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
