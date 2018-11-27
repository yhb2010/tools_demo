package com.demo.tools.ratelimit;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdeledu.domain.ServiceResult;

@RestController
public class TestClassRateLimitController {

	@Autowired
	private TestClassRateLimitService testClassRateLimitService;

    @GetMapping("/t1")
    public ServiceResult<Object> noParam(){
    	for(int i=0; i<7; i++){
    		new Thread(() -> {
    			testClassRateLimitService.counter();
    		}).start();
    	}
    	try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	for(int i=0; i<7; i++){
    		new Thread(() -> {
    			testClassRateLimitService.counter();
    		}).start();
    	}
        return testClassRateLimitService.counter();
    }

    @GetMapping("/t2")
    public ServiceResult<Object> token(){
    	for(int i=0; i<160; i++){
    		new Thread(() -> {
    			testClassRateLimitService.token3();
    		}).start();
    	}
    	try {
    		TimeUnit.MILLISECONDS.sleep(1800);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	for(int i=0; i<160; i++){
    		new Thread(() -> {
    			testClassRateLimitService.token3();
    		}).start();
    	}
    	return testClassRateLimitService.token3();
    }

    @GetMapping("/t3")
    public ServiceResult<Object> t3(){
    	for(int i=0; i<100; i++){
    		new Thread(() -> {
    			ServiceResult<Object> result = testClassRateLimitService.leak();
    			if(!result.isSuccess()){
    				System.out.println(result.getErrorMsg());
    			}
    		}).start();
    	}
    	try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	for(int i=0; i<7; i++){
    		new Thread(() -> {
    			testClassRateLimitService.leak();
    		}).start();
    	}
        return testClassRateLimitService.leak();
    }

    @GetMapping("/t4")
    public ServiceResult<Object> t4(){
    	for(int i=0; i<100; i++){
    		new Thread(() -> {
    			ServiceResult<Object> result = testClassRateLimitService.token();
    			if(!result.isSuccess()){
    				System.out.println(result.getErrorMsg());
    			}
    		}).start();
    	}
    	try {
    		TimeUnit.SECONDS.sleep(1);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    	for(int i=0; i<7; i++){
    		new Thread(() -> {
    			testClassRateLimitService.leak();
    		}).start();
    	}
    	return testClassRateLimitService.leak();
    }

}