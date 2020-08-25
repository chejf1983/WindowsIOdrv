/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.win.io;

import USBDriver.USBDevice;
import comm.win.io.WindowsIOFactory.IOTYPE;
import nahon.comm.io.IOInfo;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import nahon.comm.io.AbstractIO;

/**
 *
 * @author Administrator
 */
public class IO_USB implements AbstractIO {

    private USBDevice usbdev;
    private int usbnum;

    public IO_USB(int usbnum) {
        usbdev = new USBDevice(usbnum);
        this.usbnum = usbnum;
    }

    @Override
    public boolean IsClosed() {
        return !this.usbdev.IsOpen();
    }

    @Override
    public void SendData(byte[] data) throws Exception {
        usbdev.SendData(data);
    }

//    @Override
//    public int ReceiveData(byte[] data, int timeout) throws Exception {
//        int ret = usbdev.ReceiveData(data, timeout);
//        return ret;
//    }
    private Future<Integer> ret;

    @Override
    public int ReceiveData(final byte[] data, int timeout) throws Exception {
        ret = Executors.newSingleThreadExecutor().submit(() -> usbdev.ReceiveData(data, timeout));
        int len = ret.get(timeout, TimeUnit.MILLISECONDS);
        ret = null;
        return len;
    }

    @Override
    public void Cancel() {
        if (this.ret != null) {
            this.ret.cancel(true);
        }
    }

    @Override
    public void Close() {
        if (!this.IsClosed()) {
            try {
                this.usbdev.CloseUSB();
            } catch (Exception ex) {
                Logger.getLogger(IO_USB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void Open() throws Exception {
        if (this.IsClosed()) {
            this.usbdev.OpenUSB();
        }
    }

    @Override
    public IOInfo GetConnectInfo() {
        return new IOInfo(IOTYPE.USB.toString(), String.valueOf(this.usbdev.GetDevNum()));
    }

    @Override
    public int MaxBuffersize() {
        return 40;
    }

    @Override
    public void SetConnectInfo(IOInfo info) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
