package com.demo.tools.ratelimit.guava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

/**
 * tryAcquire(long timeout, TimeUnit unit)
 * 从RateLimiter 获取许可如果该许可可以在不超过timeout的时间内获取得到的话，
 * 或者如果无法在timeout 过期之前获取得到许可的话，那么立即返回false（无需等待）
 */
public class Demo2 {

	public static void main(String[] args) {
        //0.5代表一秒最多多少个
        RateLimiter rateLimiter = RateLimiter.create(0.5);
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new UserRequest(i));
        }
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Runnable runnable : tasks) {
        	//判断能否在1秒内得到令牌，如果不能则立即返回false，不会阻塞程序
        	if (!rateLimiter.tryAcquire(2000, TimeUnit.MILLISECONDS)) {
                System.out.println("短期无法获取令牌，真不幸，排队也瞎排");
                continue;
            }
            System.out.println("等待时间：" + rateLimiter.acquire());
            threadPool.execute(runnable);
        }
    }

    private static class UserRequest implements Runnable {
        private int id;

        public UserRequest(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(id);
        }
    }

}
