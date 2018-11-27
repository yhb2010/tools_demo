package com.demo.tools.ratelimit;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.cdel.util.anno.importer.EnableRateLimitRedisConfig;

@Import(EnableRateLimitRedisConfig.class)
@Configuration
public class SyjRateLimitConfig {

}