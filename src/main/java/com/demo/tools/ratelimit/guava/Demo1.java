package com.demo.tools.ratelimit.guava;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.RateLimiter;

/**
 * Created by wuwf on 17/7/11.
 * 有很多个任务，但希望每秒不超过X个，可用此类
 * 我们限制了2秒放行一个，可以看到第一个是直接执行了，后面的每2秒会放行一个。RateLimiter是属于单位时间内生成多少个令牌的方式，譬如2秒生成1个
 * rateLimiter.acquire()该方法会阻塞线程，直到令牌桶中能取到令牌为止才继续向下执行，并返回等待的时间。
 */
public class Demo1 {

	public static void main(String[] args) {
        //0.5代表一秒最多多少个
        RateLimiter rateLimiter = RateLimiter.create(0.5);
        List<Runnable> tasks = new ArrayList<Runnable>();
        for (int i = 0; i < 10; i++) {
            tasks.add(new UserRequest(i));
        }
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (Runnable runnable : tasks) {
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
