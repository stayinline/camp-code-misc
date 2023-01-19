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
            objectOutputStream.writeObject(protocol);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            result = objectInputStream.readObject();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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
