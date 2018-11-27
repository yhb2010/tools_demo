package com.demo.tools.ratelimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cdel.util.anno.clazz.EnableRateLimit;
import com.cdel.util.anno.clazz.EnableRedisCache;
import com.cdel.util.anno.clazz.EnableRateLimitRedis;

@EnableRateLimitRedis
@EnableRateLimit
@EnableRedisCache
@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
