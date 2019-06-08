package com.atguigu.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolRedis {

	public static void main(String[] args)
	{
		JedisPool jedisPool = JedisPoolUtils.getInstance();
		Jedis jedis = null;
		try 
		{
			jedis = jedisPool.getResource();
			
			jedis.set("k5","0508good");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			JedisPoolUtils.close(jedisPool, jedis);
		}
	}

}
