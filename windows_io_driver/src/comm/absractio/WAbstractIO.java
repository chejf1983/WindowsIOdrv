/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comm.absractio;

/**
 *
 * @author jiche
 */
public interface WAbstractIO {

    /**
     * io 是否关闭
     *
     * @return
     */
    public boolean IsClosed();

    //打開IO
    public void Open() throws Exception;

    //關閉IO
    public void Close();

    /**
     * 发送数据
     *
     * @param data
     * @throws Exception
     */
    public void SendData(byte[] data) throws Exception;

    /**
     * 接收数据，如果没有数据，data = {0};
     *
     * @param data
     * @return
     * @throws Exception
     */
    public int ReceiveData(byte[] data, int timeout) throws Exception;
    
    public void Cancel();
    /**
     * 获取io信息
     *
     * @return
     */
    public WIOInfo GetConnectInfo();
    
    public void SetConnectInfo(WIOInfo info);
    
    //最大包长度
    public int MaxBuffersize();
}
