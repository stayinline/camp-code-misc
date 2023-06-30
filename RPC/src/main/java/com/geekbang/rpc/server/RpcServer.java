package com.geekbang.rpc.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.geekbang.rpc.protocol.Protocol;

public class RpcServer {

    //线程池
    private int threadSize = 10;
    private ExecutorService threadPool;
    /**
     * 自定义缓存 k->接口全限定名称，v->具体实现类实例
     */
    private Map<String, Object> servicePool;
    //服务端口
    private int port = 9000;

    public RpcServer() {
        super();
        synchronized (this) {
            threadPool = Executors.newFixedThreadPool(this.threadSize);
        }
    }

    public RpcServer(int threadSize, int port) {
        this.threadSize = threadSize;
        this.port = port;
        synchronized (this) {
            threadPool = Executors.newFixedThreadPool(this.threadSize);
        }
    }

    public RpcServer(Map<String, Object> servicePool, int threadSize, int port) {
        this.threadSize = threadSize;
        this.port = port;
        this.servicePool = servicePool;
        synchronized (this) {
            threadPool = Executors.newFixedThreadPool(this.threadSize);
        }
    }

    /**
     * 1. 实现Socket监听：RpcAcceptor
     *
     * @throws IOException
     */
    public void service() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket receiveSocket = serverSocket.accept();
            final Socket socket = receiveSocket;

            //执行请求
            threadPool.execute(() -> {
                        try {
                            //2. 处理请求
                            process(socket);

                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

    //2.处理请求：RpcProcessor
    private void process(Socket receiveSocket) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        ObjectInputStream objectInputStream = new ObjectInputStream(receiveSocket.getInputStream());

        Protocol transportMessage = (Protocol) objectInputStream.readObject();

        //调用服务
        Object result = call(transportMessage);

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(receiveSocket.getOutputStream());
        objectOutputStream.writeObject(result);
        objectOutputStream.close();

    }

    //3.执行方法调用：RpcInvoker
    private Object call(Protocol protocol) throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InstantiationException, InvocationTargetException {

        if (servicePool == null) {
            synchronized (this) {
                servicePool = new HashMap<>();
            }
        }

        //通过接口名称构建实现类
        String interfaceName = protocol.getInterfaceName();
        Object service = servicePool.get(interfaceName);
        // 通过反射加载interfaceName具体指定的类信息
        Class<?> serviceClass = Class.forName(interfaceName);

        //判断servicePool对象是否存在，如果不存在，就创建新对象并放入池中
        if (service == null) {
            synchronized (this) {
                service = serviceClass.newInstance();
                servicePool.put(interfaceName, service);
            }
        }

        //通过实现类来构建方法，这里是通过方法名称，参数类型，来找到具体的那个方法
        Method method = serviceClass.getMethod(protocol.getMethodName(), protocol.getParamsTypes());

        //最后通过反射来实现该方法的执行
        Object result = method.invoke(service, protocol.getParameters());
        return result;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    public Map<String, Object> getServicePool() {
        return servicePool;
    }

    public void setServicePool(Map<String, Object> servicePool) {
        this.servicePool = servicePool;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
