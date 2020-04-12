package com.gmall.manage;

import com.gmall.bean.PmsSkuInfo;
import org.junit.jupiter.api.Test;
import org.junit.rules.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.EmptyStackException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GmallManageServiceApplicationTests {

	@Autowired
//	private RedisTemplate redisTemplate;

	@Test
	void contextLoads() {

//		PmsSkuInfo emp = (PmsSkuInfo) redisTemplate.opsForValue().get("emp");
//		PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
//		redisTemplate.opsForValue().set("info2",pmsSkuInfo,15, TimeUnit.SECONDS);


//		Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", "lock", 60, TimeUnit.SECONDS);
//		System.out.println(flag);
//
//		Object uuidToken = redisTemplate.opsForValue().get("lock");
//		if(uuidToken!=null&&uuidToken.equals("")){
//			redisTemplate.delete("lock");
//		}
//
	}

	@Test
	public void testSuanfa(){

		int i = 1;
		i = i++;
		System.out.println(i);
	}
}
