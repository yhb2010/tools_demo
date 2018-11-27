package com.demo.tools.tomcat;

import java.io.OutputStream;
import java.net.Socket;

//此类为一个线程，用于处理接收到的客户端Socket
public class SocketProcess extends Thread{

    protected Socket socket;

    public SocketProcess(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(socket.getInputStream());
            Response response = new Response(socket.getOutputStream());

            Servlet servlet = MyTomcat.servletMapping.get(request.getUrl());

            if (servlet != null) {
               servlet.service(request, response);
            }else{
                String res = "没有找到Servlet";
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(res.getBytes());
                outputStream.flush();
                outputStream.close();
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}