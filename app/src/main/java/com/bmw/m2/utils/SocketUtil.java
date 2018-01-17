package com.bmw.m2.utils;

import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.bmw.m2.utils.StreamUtil.close;
import static com.bmw.m2.utils.ThreadUtil.sleep;
import static com.bmw.m2.utils.ThrowUtil.error;
import static com.bmw.m2.utils.ThrowUtil.log;

/**
 * Created by admin on 2017/5/17.
 */

public class SocketUtil {

    private String ip = "192.168.191.1";
    private int port = 20108;
    private static final int RESULT_COUNT = 128;
    private Socket socket;
    private InputStream socketReader;
    private OutputStream socketWriter;
    private boolean isResetSocket;
    private byte[] resultBytes;
    private boolean isFinishNow;

    public void changeIp(String ip,int port){
        if(socket!= null){
            try {
                if(socketReader!=null)
                    socketReader.close();
                if(socketWriter!=null)
                    socketWriter.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        this.ip = ip;
        this.port = port;
    }

    public SocketUtil() {
        resultBytes = new byte[RESULT_COUNT];
        getCommands();
    }


    public void resetSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    socket = null;
                    socket = new Socket(ip, port);
//                    socket = new Socket("192.168.191.1",20108);
                    log("数据:socket连接： 正在连接");
                    socket.setSoTimeout(50);
                    socketWriter = socket.getOutputStream();
                    socketReader = socket.getInputStream();
                    if (socket != null)
                        log("数据:socket连接:  连接成功!");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (socket == null) {
                    sleep(1000);
                    resetSocket();
                }
            }
        }).start();
    }

    private void initSocketRW() {
        if ((socketWriter == null || socketReader == null) && socket != null) {
            try {
                if (socketWriter == null)
                    socketWriter = socket.getOutputStream();
                if (socketReader == null)
                    socketReader = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeCommand(byte[] commands) {
        try {
            socketWriter.write(commands);
            socketWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            if (socket != null && !socket.isClosed()) {
                error("socketService is already closed!  ");
                socket = null;
            }
        }
    }

    public void sendCommands(byte[] commands) {
        if (socket == null) {
            if (!isResetSocket) {
                isResetSocket = true;
                resetSocket();
            }
            return;
        }
        isResetSocket = false;
        initSocketRW();
        writeCommand(commands);
    }

    private void getCommands() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinishNow) {
                    if (socket == null)
                        continue;
                    initSocketRW();
                    readCommands();
                }
            }
        }).start();
    }

    private void readCommands() {
        try {
//            log("socket reading ...");
            socketReader.read(resultBytes);
            printSocketResult();
            if (listener != null)
                listener.result(resultBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSocketResult() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < resultBytes.length; i++) {
            stringBuilder.
                    append("\n").
                    append("byte[").
                    append(i).
                    append("] = ").
                    append(Integer.toHexString(resultBytes[i] & 0xff));
        }
        log("\n\n\n");
        log(stringBuilder.toString());
        log("\n\n\n");
    }

    public interface OnCommandResultListener {
        void result(byte[] bytes);
    }

    private OnCommandResultListener listener;

    public void setOnCommandResultListener(OnCommandResultListener listener) {
        this.listener = listener;
    }

    public void release() {
        isFinishNow = true;
        close(socketReader);
        close(socketWriter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            close(socket);
        } else
            closeSocket();

    }

    private void closeSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
