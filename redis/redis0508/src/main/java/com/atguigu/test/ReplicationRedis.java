package com.atguigu.test;

import redis.clients.jedis.Jedis;

public class ReplicationRedis {

	public static void main(String[] args)
	{
		Jedis jedisM = new Jedis("192.168.22.167",6379);
		Jedis jedisS = new Jedis("192.168.22.167",6380);
		
		jedisS.slaveof("192.168.22.167",6379);
		
		
		jedisM.set("k3","v3");
		
		
		String result = jedisS.get("k3");
		
		
		System.out.println("#############result: "+result);
		

	}

}
