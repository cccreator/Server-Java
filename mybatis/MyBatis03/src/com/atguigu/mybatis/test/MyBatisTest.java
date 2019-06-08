package com.atguigu.mybatis.test;

import java.io.InputStream;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.atguigu.mybatis.beans.Employee;
import com.atguigu.mybatis.mapper.EmployeeMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

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
	 * 两级缓存
	 * 
	 * 一级缓存（本地缓存） 
	 * 		SqlSession级别的缓存,本质上就是SqlSession级别的一个map。
	 *      每一个SqlSession对象都有自己的一级缓存.互不共享.
	 *      一级缓存是默认开启的
	 *      工作机制: 与数据库的一次会话期间（通过一个SqlSession）,查询到的数据会放在一级缓存中.
	 *              以后如果需要获取相同的数据,直接从缓存中获取,而不需要发送sql到数据库查询.
	 * 一级缓存失效的情况:
	 * 		1.SqlSession不同.     	
	 * 		2.SqlSession相同,但是查询条件不同.  
	 * 		3.SqlSession相同,但是在两次查询期间执行了增删改的操作.
	 * 		4.SqlSession相同,手动清除了一级缓存.
	 * 
	 * 二级缓存（全局缓存） 基于namespace级别的缓存. 二级缓存默认是关闭的，必须手动配置开启.
	 * 		工作机制:
	 * 		1.一个会话(SqlSession)，查询一条数据,这个数据就会被放在当前会话的一级缓存中.
	 * 		2.如果会话提交或者关闭,一级缓存中的数据会被保存到二级缓存中.
	 * 	
	 * 		使用步骤:
	 * 		1.在全局配置文件中开启二级缓存:
	 * 		   <setting name="cacheEnabled" value="true"/>
	 *		2.在想要使用二级缓存的sql映射文件中配置使用二级缓存:
	 *		   <cache></cache>
	 *		3.我们的POJO需要实现序列化接口.
	 *
	 * 和缓存相关的设置:
	 * 1. cacheEnabled=false:关闭的是二级缓存,一级缓存依旧可以使用.
	 * 2. 每一个select标签都有useCache=true属性.
	 * 	    如果useCache=false:  不使用二级缓存，依旧使用一级缓存.
	 * 3. 每一个增删改标签都有flushCache=true属性:
	 * 	   在两次查询期间做了增删改操作,一二级缓存都会清除.
	 *    每一个查询标签都有flushCache=false,如果改为true,每次查询之后都会清除缓存，缓存是没有被使用的.
	 * 4.sqlSession.clearCache(): 只会清空当前session的一级缓存.
	 * 
	 * 5.localCacheScope:设置一级缓存(本地缓存)的作用域。
	 * 		SESSION :会话期间
	 *  	STATEMENT:  可以禁用一级缓存.
	 * 	
	 * 缓存数据的查找顺序:  二一库.
	 *  二级缓存
	 * 	一级缓存 	  
	 *  数据库
	 * 
	 * 整合第三方的缓存:  EhCache
	 * 	1.导入Ehcache的jar包  以及适配包
	 * 	  ehcache-core-2.6.8.jar
		  mybatis-ehcache-1.0.3.jar
          slf4j-api-1.6.1.jar
          slf4j-log4j12-1.6.2.jar
	 *  2.配置Ehcache的配置文件
	 *    
	 * 	3.在映射文件中配置使用Ehcache
	 * 	  <cache type="org.mybatis.caches.ehcache.EhcacheCache"></cache>
	 */
	@Test
	public void testFirstLevelCache()throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		//SqlSession session2 = ssf.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			//EmployeeMapper mapper2 = session2.getMapper(EmployeeMapper.class);
			
			Employee emp1 = mapper.getEmpById(1001);
			System.out.println("emp1:" + emp1);
			System.out.println("-------------------------------");
			//添加
			
//			mapper.addEmp(new Employee("Jerry","f", "jerry@sina.com"));
//			session.commit();
			
			//清除缓存中的数据
			//session.clearCache();
			
			Employee emp2 = mapper.getEmpById(1001);
			System.out.println("emp2:"+ emp2);
			
		}finally{
			session.close();
		}
	}
	
	
	@Test
	public void testSecondLevelCache()throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		SqlSession session2 = ssf.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			EmployeeMapper mapper2 = session2.getMapper(EmployeeMapper.class);
			
			Employee emp1 = mapper.getEmpById(1001);
			System.out.println("emp1:" + emp1);
			System.out.println("-------------------------------");
			session.commit(); //session.close();
			
			//session.clearCache();
			//添加
//			mapper.addEmp(new Employee("White","f", "white@sina.com"));
//			session.commit();
			
			Employee emp2 = mapper2.getEmpById(1001);
			System.out.println("emp2:"+ emp2);
			
		}finally{
			session.close();
			session2.close();
		}
	}
	
	@Test
	public void testPageHelper()throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			//查询之前设置分页信息
			Page<Object> page = PageHelper.startPage(9,1);  //显示第几页,每页显示多少条
			List<Employee> emps = mapper.getEmps();
			//查询之后获取分页信息
			PageInfo<Employee> info = new PageInfo<Employee>(emps,5);
			
			for (Employee employee : emps) {
				System.out.println(employee);
			}
			//获取分页相关的信息
			System.out.println("当前页码:"+page.getPageNum());
			System.out.println("总记录数:"+page.getTotal());
			System.out.println("总页码:"+page.getPages());
			System.out.println("每页显示的记录数:"+page.getPageSize());
			
			System.out.println("------------------------");
			
			System.out.println("当前页码:"+info.getPageNum());
			System.out.println("总记录数:"+info.getTotal());
			System.out.println("总页码:"+info.getPages());
			System.out.println("每页显示的记录数:"+info.getPageSize());
			System.out.println("是否是第一页:"+ info.isIsFirstPage());
			System.out.println("是否是最后一页:"+info.isIsLastPage());
			System.out.println("是否有上一页:"+info.isHasPreviousPage());
			System.out.println("是否有下一页:"+info.isHasNextPage());
			
			//分页逻辑
			System.out.println("连续显示的页码:");
			int [] nums = info.getNavigatepageNums();
			for (int i : nums) {
				System.out.print(i + " ");
			}
			System.out.println();
		}finally{
			session.close();
		}
	}
	
	
	
}
