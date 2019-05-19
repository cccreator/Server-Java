package com.atguigu.mybatis.test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.atguigu.mybatis.beans.Employee;
import com.atguigu.mybatis.mapper.EmployeeMapper;



public class MyBatisTest {

	//将获取SqlSessionFactory的代码封装成一个方法
	public SqlSessionFactory  getSqlSessionFactory() throws Exception{
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = 
				new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}
	
	
	
	
	
	/**
	 * 一级缓存 SqlSession级别的缓存 本质上就是SqlSession级别的一个Map
	 * 每一个Session对象都有自己的一级缓存 互不共享
	 * 一级缓存默认是开启的
	 * 工作机制
	 * 		与数据库的一次会话期间 通过一个Session 查询到的数据会放在一级缓存中
	 * 以后如果获取相同的数据 直接存缓存中读，而不需要发送sql
	 * 
	 * 一级缓存失效情况
	 * 1.Session不同
	 * 2.Session相同  查询条件不同
	 * 3.SqlSession相同 但是两次查询期间出现了增删改操作
	 * 4.SqlSession 相同 但是手动清除了一次缓存	
	 * 
	 * 二级缓存
	 * 全局缓存 基于nameSpace 级别的缓存。二级缓存，二级缓存默认是关闭的需要手动配置开启
	 * 	工作机制 ：
	 * 	1.一个会话查询一个数据这个数据就会被放到当前会话的一级缓存中
	 * 	2.如果会话提交或者关闭,一级缓存中的数据会保存到二级缓存中
	 * 使用步骤
	 * 1.在全局配置文件中开启二级缓存
	 * <setting name="cacheEnabled" value="true"/>
	 * 2.在想要的二级缓存的sql隐射文件中配置使用二级缓存
	 * <cache></cache>
	 * 3.我们的pojo需要实现序列化接口
	 * 和缓存相关的设置
	 * 1.cacheEnabled=false:关闭的是二级缓存，一级缓存依旧可以使用
	 * 每一个select标签都有useCache=true属性
	 * 如果useCache=false 不适用二级缓存 使用一级缓存
	 * 3.每一个增删改标签都有flushCache属性
	 *  在两次查询期间做了增删改操作，一二级缓存都会清楚
	 *  每一个增删改标签都有flushCache=false，如果改为true每次查询之后都会清除缓存 缓存是没有被使用的
	 *  4.clearCache只会清除当前session一级缓存
	 *  5.localCacheScope:设置一级缓存 本地缓存的作用域
	 *  
	 *  	SESSION 会话期间
	 *  	STATEMENT 每一次sql执行期间，可以禁用一级缓存
	 *  6.缓存数据的查找顺序 二级缓存 -》 一级缓存 -》数据库 
	 *  
	 *  整合第三方的缓存：ehCache
	 *  1.导入ehCache的jar包
	 */
	@Test
	public void testFirstLevelCache() throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		try{
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			Employee emp1 = mapper.getEmpById(1001);
		}finally{
			
			
		}
	}
}
