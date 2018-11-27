package com.demo.tools.tomcat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MyTomcat {

	private static final int port = 8099;
    private static final Properties properties = new Properties();
    public static final Map<String, Servlet> servletMapping = new HashMap<>();

    private void init() {

        InputStream io = null;

        String basePath;

        try {
            //获取basePath
            basePath = MyTomcat.class.getResource("/").getPath();
            System.out.println(basePath);
            io = new FileInputStream(basePath + "web.properties");
            properties.load(io);
            io.close();

            //初始化ServletMapping
            //返回属性key的集合
            Set<Object> keys = properties.keySet();
            for (Object key : keys) {
                System.out.println(key.toString() + "=" + properties.get(key));
                //根据key值获取className
                Object classname = properties.get(key.toString());
                servletMapping.put(key.toString(), (Servlet) Class.forName(classname.toString()).newInstance());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (io != null) {
                try {
                    io.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Tomcat 服务已启动，地址：localhost ,端口：" + port);
            //持续监听
            do {
                Socket socket = serverSocket.accept();
                //处理任务
                Thread thread = new SocketProcess(socket);
                thread.start();
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //http://localhost:8099/login
    public static void main(String[] args) {
    	MyTomcat my = new MyTomcat();
    	my.init();
    	my.start();
	}

}
