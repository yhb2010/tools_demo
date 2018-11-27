package com.demo.tools.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Request {

	private String method;
    private String url;

	// 此处省略get方法
    public Request(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //将读取到的第一行显按空格拆分出方法与url
        //http请求协议：
        //GET /test HTTP/1.1
        String[] methodAndUrl = bufferedReader.readLine().split(" ");
        this.method = methodAndUrl[0];
        this.url = methodAndUrl[1];
    }

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
