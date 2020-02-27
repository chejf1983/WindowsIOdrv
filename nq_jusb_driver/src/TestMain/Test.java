/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestMain;

import USBDriver.USBDevice;
import USBDriver.USBLib;

/**
 *
 * @author Administrator
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        System.out.println(DLLPatchTest.GetDLLPatch());
//        System.out.println(USBLib.libpatch);        
        USBLib.InitLib();
        byte[] senddata = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x7B, (byte) 0x7B,
            (byte) 0x0, (byte) 0x0, (byte) 0x30, (byte) 0x0, (byte) 0x0, (byte) 0x0,
            (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x44, (byte) 0x44,
            (byte) 0x55, (byte) 0xAA, (byte) 0x7D, (byte) 0x7D};
        int[] devs = USBLib.SearchUSBDev();
        //int[] devs = new int[]{0};
        System.out.println("找到设备:" + devs.length);
        if (devs.length > 0) {
            USBDevice dev = new USBDevice(devs[0]);
            //USBDevice  dev = new USBDevice(0);
            dev.OpenUSB();
            dev.SendData(senddata);
            byte[] tmp = new byte[1000];
            long len = dev.ReceiveData(tmp, 1000);
            System.out.println("长度:" + len);
            System.out.print("收到数据:");
            for (int i = 0; i < len; i++) {
                System.out.print(tmp[i] + " ");
            }
            dev.CloseUSB();
        }
    }
}
