package com.bmw.m2.utils;


import android.content.SharedPreferences;


import com.bmw.m2.VLCApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.bmw.m2.utils.NumberUtil.getIntFromBytes;
import static com.bmw.m2.utils.ThreadUtil.sleep;
import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/2/17.
 */

public class UdpSocketUtil {

    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private DatagramPacket datagramPacketRead;
//    private ExecutorService fixThreadPool;
    private boolean isStop;
//    private static final int RESULT_COUNT = 104;

    public UdpSocketUtil() {

        /*SharedPreferences sharedPreferences = VLCApplication.getSharedPreferences();
        String ip = sharedPreferences.getString("ip", "172.169.10.7");
        int port = sharedPreferences.getInt("port", 69);
        socketLogin(ip, port);*/
//        fixThreadPool = Executors.newFixedThreadPool(50);

        log("udp: 包准备就绪");
        read();
//        read_car();

    }

    public void socketLogin(String ip, int port, int result_count, int myPort) {
        try {
            log("udp: socket连接中");
            if (socket == null) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(myPort));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (datagramPacket == null)
            try {
                InetAddress address = InetAddress.getByName(ip);
                datagramPacket = new DatagramPacket(new byte[1], 1, address, port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        else {
            try {
                InetAddress address = InetAddress.getByName(ip);
                datagramPacket.setAddress(address);
                datagramPacket.setPort(port);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        if (datagramPacketRead == null) {
                byte[] bytes = new byte[result_count];
                datagramPacketRead = new DatagramPacket(bytes, bytes.length);
        }
    }

    private void socketLoginOut() {
        if (socket != null) {
            socket.close();
        }
        socket = null;
    }

    public void changeIp(String ip, int port) {
        socketLoginOut();
//        socketLogin(ip,port,-1,20108);
        SharedPreferences.Editor editor = VLCApplication.getSharedPreferences().edit();
        editor.putString("ip", ip);
        editor.putInt("port", port);
        editor.commit();
    }

    private void initSocket() {
//        if (fixThreadPool != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (socket == null && !isStop) {

                        if (socket != null) {
                            log("udp: socket连接成功");
                            break;
                        }
                    }
                }
            }).start();
//        }
    }

    public void sendCommands(final byte[] commands) {
        if (socket == null || datagramPacket == null) {
            error("发送：socket为空！");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                datagramPacket.setData(commands);
                datagramPacket.setLength(commands.length);
                try {
                    socket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void read() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    if (socket != null && datagramPacketRead != null) {
                        try {
//                            log("等待接收数据！");
//                            sleep(100);
                            socket.receive(datagramPacketRead);
                            byte[] bytesResult = datagramPacketRead.getData();
                            /*for(int i=0;i<bytesResult.length;i++){
                                log("\ncar : byte["+i+"] = "+Integer.toHexString(bytesResult[i]&0xff));
                            }*/

                            if (isGetThrRightResult(bytesResult) && listener != null)
                                listener.result(bytesResult);

                        } catch (IOException e) {
                            error("数据读取错误：Error：" + e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private boolean isGetThrRightResult(byte[] bytes){
        if (getIntFromBytes(bytes[0]) == 0xa5 && getIntFromBytes(bytes[1]) == 0x5a) {
            int length = getIntFromBytes(bytes[2]) | (getIntFromBytes(bytes[3]) << 8);
            length += 2;
            if (length == bytes.length)
                return true;
        }
        return false;
    }

    public void release() {
//        fixThreadPool.shutdownNow();
        isStop = true;
        if (socket != null) {
            socket.close();
        }
        log("socket连接：释放内存！");

    }

    public interface OnCommandResultListener {
        void result(byte[] bytes);
    }

    private OnCommandResultListener listener;

    public void setOnCommandResultListener(OnCommandResultListener listener) {
        this.listener = listener;
    }
}
