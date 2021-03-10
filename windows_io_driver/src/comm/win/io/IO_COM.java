/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.win.io;

//import gnu.io.CommPortIdentifier;
//import gnu.io.SerialPort;
import nahon.comm.io.IOInfo;
import java.io.*;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import comm.win.io.WindowsIOFactory.IOTYPE;
import java.util.concurrent.Future;
import nahon.comm.io.AbstractIO;

/**
 *
 * @author chejf
 */
public class IO_COM implements AbstractIO {

    private boolean isClosed = true;
    private CommPortIdentifier comportId;
    private SerialPort comserialPort;
    private OutputStream comout = null;
    private InputStream comin = null;

    private String comName;
    private int baundrate;

    public IO_COM(String name, int baundrate) {
        this.comName = name;
        this.baundrate = baundrate;
    }

    private static boolean IsInit = false;

    public static void InitLib() throws Exception {
        InitLib(false);
    }

    public static void InitLib(boolean clean) throws Exception {
        if (!IsInit) {
//            CreateDLLTempFile("win32com.dll");
//            System.load(CreateDLLTempFile("win32com.dll"));
            CreateDLLTempFile("rxtxSerial.dll", clean);
            CreateDLLTempFile("rxtxParallel.dll", clean);
            System.load(CreateDLLTempFile("rxtxSerial.dll", clean));
            System.load(CreateDLLTempFile("rxtxParallel.dll", clean));
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

        if (clean) {
            if (tmp.exists()) {
                tmp.delete();
            }
            CreateDLLTempFile(Filename, false);
        } else if (!tmp.exists()) {
            InputStream in = IO_COM.class.getResourceAsStream("/comm/resource/" + Filename);
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

    private static int sendBufferLimit = 128;

    @Override
    public void SendData(byte[] data) throws Exception {
//        Thread.sleep(20);
//        TimeUnit.MILLISECONDS.sleep(20); // 485在接收完命令时，会有一个关闭延时，为了确保在连续发送时，不会出现丢包，每次发送前等待20ms，确保485已经关闭
        //延时放到协议当中去做
        comserialPort.notifyOnOutputEmpty(true);
        for (int sendIndex = 0; sendIndex < data.length;) {
            if (data.length - sendIndex > sendBufferLimit) {
                comout.write(data, sendIndex, sendBufferLimit);
                sendIndex += sendBufferLimit;
            } else {
                comout.write(data, sendIndex, data.length - sendIndex);
                sendIndex += data.length - sendIndex;
            }
        }
    }

    private byte[] rc_buffer = new byte[20480];

    @Override
    public int ReceiveData(byte[] data, int timeout) throws Exception {
//        int index = 0;
        int rclen = 0;
        //10ms一个周期
        long start_time = System.currentTimeMillis();
        byte[] tmp_data = new byte[1000];
        while (System.currentTimeMillis() - start_time < timeout) {
            //如果有数据，读一次
            if (comin.available() > 0) {
                //读取到临时buffer中
                int tmp_len = comin.read(tmp_data);
                //将临时buffer复制到data中
                System.arraycopy(tmp_data, 0, data, rclen, tmp_len);
                //增加读取的长度
                rclen += tmp_len;
            } else if (rclen > 0) {
                //如果没有数据，并且已经获得了一些数据，返回
                return rclen;
            }
            //等待10ms
            TimeUnit.MILLISECONDS.sleep(5);
//            Thread.sleep(1);
        }

        return rclen;
    }
    private Future<Integer> ret;

//    @Override
//    public int ReceiveData(final byte[] data, int timeout) throws Exception {
//        ret = Executors.newSingleThreadExecutor().submit(() -> {
//            int rclen = 0;
//            int index = 0;
//            //10ms一个周期
//            while ((index++) * 10 < timeout) {
//                //如果有数据，读一次
//                if (comin.available() > 0) {
//                    byte[] tmp_data = new byte[1000];
//                    //读取到临时buffer中
//                    int tmp_len = comin.read(tmp_data);
//                    //将临时buffer复制到data中
//                    System.arraycopy(tmp_data, 0, data, rclen, tmp_len);
//                    //增加读取的长度
//                    rclen += tmp_len;
//                } else if (rclen > 0) {
//                    //如果没有数据，并且已经获得了一些数据，返回
//                    return rclen;
//                }
//                //等待10ms
//                Thread.sleep(10);
////                TimeUnit.MILLISECONDS.sleep(10);
//            }
//            return rclen;
//        });
//
//        int len = ret.get(timeout, TimeUnit.MILLISECONDS);
//        ret = null;
//        return len;
//    }
    @Override
    public void Cancel() {
        if (this.ret != null) {
            this.ret.cancel(true);
        }
    }

    private void CloseIO() {
        if (!this.IsClosed()) {
            try {
                if (comserialPort != null) {
                    comserialPort.close();
                }
                if (comout != null) {
                    comout.close();
                }
                if (comin != null) {
                    comin.close();
                }
            } catch (Exception ex) {
            } finally {
                this.isClosed = true;
            }
        }

    }

    @Override
    public boolean IsClosed() {
        return this.isClosed;
    }

    private void OpenIO() throws Exception {
        if (this.IsClosed()) {
            InitLib(false);

            Enumeration portList = CommPortIdentifier.getPortIdentifiers();

            while (portList.hasMoreElements()) {
                comportId = (CommPortIdentifier) portList.nextElement();
                if ((comportId.getPortType() == CommPortIdentifier.PORT_SERIAL)
                        && (comportId.getName().equals(this.comName))) {
                    comserialPort = (SerialPort) comportId.open("SimpleWriteApp", 2000);
                    comserialPort.setSerialPortParams(this.baundrate,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    comserialPort.setInputBufferSize(40960);
                    comserialPort.setOutputBufferSize(10240);
                    comout = comserialPort.getOutputStream();
                    comin = comserialPort.getInputStream();

                    this.isClosed = false;
                    return;
                }
            }

            throw new Exception("Could not found Comport");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="physical IO control"> 
//    private Lock usercoutlock = new ReentrantLock();
//    private int delaycolsetime = 200;//ms
//    private int user = 0;
    @Override
    public void Open() throws Exception {
//        this.usercoutlock.lock();
//        this.user++;
//        this.usercoutlock.unlock();
        this.OpenIO();
    }

    @Override
    public void Close() {
        CloseIO();
//        Executors.newSingleThreadExecutor().submit(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(delaycolsetime);
//                    usercoutlock.lock();
//                    user--;
//                    if (user <= 0) {
//                        if (!IsClosed()) {
//                            CloseIO();
//                        }
//                        user = 0;
//                    }
//                    usercoutlock.unlock();
//                } catch (Exception ex) {
//
//                }
//            }
//        });
    }
    // </editor-fold> 

    @Override
    public IOInfo GetConnectInfo() {
        return new IOInfo(IOTYPE.COM.toString(), this.comName, String.valueOf(this.baundrate));
    }

    @Override
    public int MaxBuffersize() {
        return 65535;
    }

    @Override
    public void SetConnectInfo(IOInfo info) {
        if (info.iotype.contentEquals(IOTYPE.COM.toString())) {
            this.comName = info.par[0];
            this.baundrate = Integer.valueOf(info.par[1]);
        }
    }

}
