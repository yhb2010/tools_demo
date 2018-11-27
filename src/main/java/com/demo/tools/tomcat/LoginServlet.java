package com.demo.tools.tomcat;

//定义一个具体的Servlet类，继承其父类
public class LoginServlet extends Servlet {

    @Override
    public void doGet(Request request, Response response) {
        doPost(request,response);
    }

    @Override
    public void doPost(Request request, Response response)  {
        try {
        	response.write("Hello,welcome to here !");
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

}