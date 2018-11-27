package com.demo.tools.tomcat;

//此类中模仿httpservlet类，添加service，doget、dopost方法
public abstract class Servlet {

    public void service(Request request, Response response) {

        //判断是调用doget 还是 dopost
        if ("get".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }

    }

    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);

}