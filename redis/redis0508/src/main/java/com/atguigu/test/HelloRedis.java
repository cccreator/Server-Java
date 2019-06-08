package com.atguigu.test;

import redis.clients.jedis.Jedis;

public class HelloRedis {

	public static void main(String[] args)
	{
		Jedis jedis = new Jedis("192.168.22.167",6379);
		
		String result = jedis.ping();
		
		System.out.println("**********"+result);
		
		//Connection 
	}

}
