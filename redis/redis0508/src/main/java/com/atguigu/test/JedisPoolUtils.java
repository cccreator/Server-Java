package com.atguigu.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtils 
{
	private static volatile JedisPool jedisPool = null;
	
	private JedisPoolUtils() {}
	
	
	//DCL(double check lock)
	public static JedisPool getInstance()
	{
		
		if(null == jedisPool)
		{
			synchronized (JedisPoolUtils.class) 
			{
				if(null == jedisPool)
				{
					JedisPoolConfig poolConfig = new JedisPoolConfig();
					poolConfig.setMaxActive(100);
					poolConfig.setMaxIdle(32);
					poolConfig.setMaxWait(100 * 1000);
					poolConfig.setTestOnBorrow(true);
					
					jedisPool = new JedisPool(poolConfig,"192.168.22.167",6379);
				}
			}
		}
		return jedisPool;
	}
	

	public static void close(JedisPool jedisPool,Jedis jedis)
	{
		if(null != jedis)
		{
			jedisPool.returnResourceObject(jedis);
		}
	}

}
