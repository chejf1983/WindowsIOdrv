/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package USBDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 *
 * @author Administrator
 */
public class USBLib {

    private static boolean IsInit = false;

    public static boolean IsInitLib() {
        return IsInit;
    }

    public static void InitLib() throws Exception {
        InitLib(false);
    }

    public static void InitLib(boolean clean) throws Exception {
        if (!IsInit) {
//            CreateDLLTempFile("USB_Driver.dll");
//            CreateDLLTempFile("libusb0_x86.dll");
//            CreateDLLTempFile("libusb0.dll");
//            CreateDLLTempFile("NahonUSBLib.dll");
            //String dllpath = CreateDLLTempFile("NahonUSBLib.dll");
            System.load(CreateDLLTempFile("libusb0_x86.dll", clean));
            System.load(CreateDLLTempFile("libusb0.dll", clean));
            System.load(CreateDLLTempFile("USB_Driver.dll", clean));
            System.load(CreateDLLTempFile("NahonUSBLib.dll", clean));
//            System.loadLibrary("libusb0_x86.dll");
//            System.loadLibrary("libusb0.dll");
//            System.loadLibrary("USB_Driver.dll");
//            System.loadLibrary("NahonUSBLib.dll");
            IsInit = true;
        }
    }

    private static String CreateDLLTempFile(String Filename, boolean clean) throws Exception {
        //System.out.println(System.getProperty("user.dir") + "\\jre\\bin");
        File tmp = new File(System.getProperty("user.dir") + "\\jre\\bin");
        if (tmp.exists()) {
            tmp = new File(System.getProperty("user.dir") + "\\jre\\bin\\" + Filename);
        } else {
            tmp = new File(System.getProperty("user.dir") + "\\" + Filename);
        }

        if (tmp.exists()) {
            if (clean) {
                tmp.delete();
            }
        }

        if (!tmp.exists()) {
            InputStream in = USBLib.class.getResourceAsStream("/Resource/" + Filename);
            FileOutputStream out = new FileOutputStream(tmp);

            int i;
            byte[] buf = new byte[1024];
            while ((i = in.read(buf)) != -1) {
                out.write(buf, 0, i);
            }

            in.close();
            out.close();
            System.out.println("create file:" + Filename);
        }

        // return tmp.getAbsoluteFile().getAbsolutePath();
        return tmp.getCanonicalPath();
    }

    public static int[] SearchUSBDev() throws Exception {
        int devnum = USBLib.USBScanDevImpl(1);

        int[] tmp = new int[devnum];
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = i;
        }

        return tmp;
    }

    static native int USBScanDevImpl(int NeedInit);

    static native int OpenUSBImpl(int DevIndex);

    static native int USBBulkWriteDataImpl(int nBoardID, byte[] sendbuffer, int len, int waittime);

    static native int USBBulkReadDataImpl(int nBoardID, byte[] readbuffer, int len, int waittime);

    static native int USBCtrlDataImpl(int nBoardID, int requesttype, int request, int value, int index, byte[] bytes, int size, int waittime);

    static native int CloseUSBImpl(int DevIndex);

}
