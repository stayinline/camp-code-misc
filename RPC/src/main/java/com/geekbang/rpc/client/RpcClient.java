package com.geekbang.rpc.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.geekbang.rpc.protocol.Protocol;

public class RpcClient {

    private String serverAddress;
    private int serverPort;

    public RpcClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    //RpcConnector + RpcInvoker
    @SuppressWarnings("resource")
    public Object sendAndReceive(Protocol protocol) {
        Object result = null;

        try {
            Socket socket = new Socket(serverAddress, serverPort);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            // 将protocol中具体的类信息(方法名、参数值参和类型等)向socket传输
            objectOutputStream.writeObject(protocol);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 将server端传输过来的数据读出来
            result = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
