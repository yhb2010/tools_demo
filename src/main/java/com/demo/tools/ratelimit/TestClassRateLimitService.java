package com.demo.tools.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cdel.redis.anno.CacheRedis;
import com.cdel.util.anno.method.MethodRateLimit;
import com.cdel.util.anno.myenum.LimitTypeEnum;
import com.cdeledu.domain.ServiceResult;

@Service
public class TestClassRateLimitService {

	@Value("${redis.appName}")
	private String appName;

    @MethodRateLimit(cacheRedis = @CacheRedis(time = 1))
    public ServiceResult<Object> counter(){
        System.out.println("业务逻辑。。。。");
        return ServiceResult.getSuccessResult("ok");
    }

    @MethodRateLimit(limit = 10)
    public ServiceResult<Object> token3(){
    	System.out.println("业务逻辑。。。。");
    	return ServiceResult.getSuccessResult("ok");
    }

    @MethodRateLimit(limitType = LimitTypeEnum.JAVA)
    public ServiceResult<Object> leak(){
    	System.out.println(System.currentTimeMillis() + ": 业务逻辑。。。。");
    	return ServiceResult.getSuccessResult("ok");
    }

    @MethodRateLimit(limitType = LimitTypeEnum.JAVA)
    public ServiceResult<Object> token(){
    	System.out.println(System.currentTimeMillis() + ": 业务逻辑。。。。");
    	return ServiceResult.getSuccessResult("ok");
    }

}