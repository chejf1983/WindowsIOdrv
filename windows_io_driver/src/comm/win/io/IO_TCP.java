/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.win.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import comm.win.io.WindowsIOFactory.IOTYPE;
import comm.absractio.WIOInfo;
import comm.absractio.WAbstractIO;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jiche
 */
public class IO_TCP implements WAbstractIO {

    private Socket mysocket;
    private OutputStream tcpout = null;
    private InputStream tcpin = null;

    private String serverIp;
    private int portNum;
    private boolean isClosed = true;

    public IO_TCP(String serverIp, int portnum) {
        this.serverIp = serverIp;
        this.portNum = portnum;
    }

    public IO_TCP(Socket socket) throws Exception {
        this.mysocket = socket;
        this.mysocket.setKeepAlive(true);
        this.tcpout = this.mysocket.getOutputStream();
        this.tcpin = this.mysocket.getInputStream();

        this.serverIp = ((InetSocketAddress) this.mysocket.getRemoteSocketAddress()).getHostString();
        this.portNum = ((InetSocketAddress) this.mysocket.getRemoteSocketAddress()).getPort();
        this.isClosed = false;
    }

    @Override
    public boolean IsClosed() {
        return this.isClosed;
    }

    @Override
    public void SendData(byte[] data) throws Exception {
        tcpout.write(data);
    }
    
    
    private Future<Integer> ret;
    
    @Override
    public int ReceiveData(final byte[] data, int timeout) throws Exception {
        ret = Executors.newSingleThreadExecutor().submit(() -> {
            int rclen = 0;
            int index = 0;
            //10ms一个周期
            while ((index++) * 1 < timeout) {
                //如果有数据，读一次
                if (tcpin.available() > 0) {
                    byte[] tmp_data = new byte[1000];
                    //读取到临时buffer中
                    int tmp_len = tcpin.read(tmp_data);
                    //将临时buffer复制到data中
                    System.arraycopy(tmp_data, 0, data, rclen, tmp_len);
                    //增加读取的长度
                    rclen += tmp_len;
                } else if (rclen > 0) {
                    //如果没有数据，并且已经获得了一些数据，返回
                    return rclen;
                }
                //等待10ms
                TimeUnit.MILLISECONDS.sleep(1);
            }
            return rclen;
        });

        int len = ret.get(timeout, TimeUnit.MILLISECONDS);
        ret = null;
        return len;
    }

    @Override
    public void Cancel() {
        if(this.ret != null){
            this.ret.cancel(true);
        }
    }
//
//    @Override
//    public int ReceiveData(byte[] data, int timeout) throws Exception {
//        this.mysocket.setSoTimeout(timeout);
//        try {
//            return tcpin.read(data);
//        } catch (SocketTimeoutException ex) {
//            return 0;
//        }
//    }

    @Override
    public WIOInfo GetConnectInfo() {
        return new WIOInfo(IOTYPE.TCP.toString(), this.serverIp, String.valueOf(this.portNum));
    }

    @Override
    public void Open() throws Exception {
        if (this.IsClosed()) {
            this.mysocket = new Socket();
            this.mysocket.setKeepAlive(true);
            this.mysocket.connect(new InetSocketAddress(InetAddress.getByName(this.serverIp),
                    this.portNum), 1000);
            this.tcpout = this.mysocket.getOutputStream();
            this.tcpin = this.mysocket.getInputStream();
            isClosed = false;
        }
    }

    @Override
    public void Close() {
        if (!this.IsClosed()) {
            try {
                mysocket.close();
                tcpout.close();
                tcpin.close();
            } catch (IOException ex) {
                Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
            } finally {
                isClosed = true;
            }
        }
    }

    @Override
    public int MaxBuffersize() {
        return 65535;
    }

    @Override
    public void SetConnectInfo(WIOInfo info) {
        if (info.iotype.contentEquals(IOTYPE.TCP.toString())) {
            this.serverIp = info.par[0];
            this.portNum = Integer.valueOf(info.par[1]);
        }
    }
}
