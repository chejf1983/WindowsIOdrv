/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package USBDriver;

/**
 *
 * @author Administrator
 */
public class USBDevice {

    private final int usbnum;
    private boolean isOpen = false;

    public USBDevice(int devnum) {
        this.usbnum = devnum;
    }

    public int GetDevNum() {
        return this.usbnum;
    }

    public void OpenUSB() throws Exception {
        if (!this.isOpen) {
            int[] devnum = USBLib.SearchUSBDev();
            if (usbnum >= devnum.length) {
                throw new Exception("USB:" + usbnum + "is not exist");
            }

            if (0 == USBLib.OpenUSBImpl(usbnum)) {
                this.isOpen = true;
            } else {
                throw new Exception("USB Open Failed:" + usbnum);
            }
        }
    }

    public void SendData(byte[] data) throws Exception {
        if (this.isOpen) {
            if (USBLib.USBBulkWriteDataImpl(usbnum, data, data.length, 1000) != data.length) {
                throw new Exception("Send Data Failed");
            }
        } else {
            throw new Exception("USB is not opened yet");
        }
    }

    public int ReceiveData(byte[] rcbuffer, int timeout) throws Exception {
        if (this.isOpen) {
            int ret = USBLib.USBBulkReadDataImpl(usbnum, rcbuffer, rcbuffer.length, timeout);
            if (ret > rcbuffer.length) {
                throw new Exception("Recevei Data Failed");
            } else {
                return ret;
            }
        } else {
            throw new Exception("USB is not opened yet");
        }
    }

    public void CloseUSB() throws Exception{
        if (this.isOpen) {
            this.isOpen = false;
            USBLib.CloseUSBImpl(usbnum);
        }
    }

    public boolean IsOpen() {
        return this.isOpen;
    }
}
