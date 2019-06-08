package com.atguigu.test;

import java.util.Set;

import redis.clients.jedis.Jedis;

public class APIRedis {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("192.168.22.167",6379);
		
		Set<String> set = jedis.keys("*");
		System.out.println(set.size());
		
		jedis.set("k3","v3");
		
		System.out.println(jedis.get("k3"));
		
		//家庭作业
	}

}
