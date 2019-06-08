package com.atguigu.mybatis.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import com.atguigu.mybatis.beans.Employee;
import com.atguigu.mybatis.dao.EmployeeMapper;

public class MyBatisTest {
	/**
	 * helloWorld的小结:
	 * 
	 * 1.导入Mybatis的jar包
	 * 2.创建一个全局配置文件  mybatis-config.xml ,根据全局配置文件，创建了一个SqlSessionFactory对象.
	 * 3.创建一个sql映射文件, EmployeeMapper.xml,该配置文件中配置了sql语句.
	 * 4.将sql映射文件注册到全局配置文件中
	 * 5.从SqlSessionFactory中获取SqlSession对象. sqlSession代表和数据库的一次会话.
	 * 	 然后调用selectOne("sql语句的唯一标识",执行sql的参数)完成查询操作.
	 * 6.最后将SqlSession对象关闭.释放资源.
	 */
	
	@Test
	public void  testMyBatis() throws Exception{
		//创建SqlSessionFactory对象.
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = 
				new SqlSessionFactoryBuilder().build(inputStream);
		
		//获取SqlSession对象
		SqlSession session = sqlSessionFactory.openSession();
		
		//开始增删改查.
		try {
			/**
			 *selectOne 两个参数的意思:
			 *	1. sql语句的唯一标识
			 *  2. 执行sql传入的参数
			 *  
			 */
			Employee employee = 
					session.selectOne("myBatis.day01.suibian.selectEmployee", 1001);
		
			System.out.println(employee);
		} finally {
			//关闭session
			session.close();
		}
	}
	
	
	/**
	 *  Mapper接口的好处:
	 *  	1.接口中定义的方法有明确的类型约束(方法参数的类型   方法返回值的类型)
	 *      2.接口本身:
	 *      	接口本身就是抽象.抽出了规范.不强制要求如何做具体的实现.可以使用jdbc，hibernate，Mybatis.
	 *      	接口将规范与具体的实现分离.
	 *  Mapper接口开发， MyBatis会为接口生成代理实现类。代理对象完成具体的增删改查操作.
	 *  最底层还是使用selectOne,update等方法来完成的.
	 *  
	 *  Mapper接口开发需要注意:
	 *  	1.Mapper接口要与sql映射文件动态绑定. sql映射文件的namespace指定成接口的全类名.
	 *      2.Mapper接口方法与sql映射文件的sql语句绑定。  sql语句的id值指定成接口中的方法名.
	 */
	
	@Test
	public void testMapperInterface() throws Exception{
		//创建SqlSessionFactory对象.
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = 
				new SqlSessionFactoryBuilder().build(inputStream);
		
		//获取SqlSession对象
		SqlSession session = sqlSessionFactory.openSession();
		
		//Mapper接口 -->CRUD方法 --- >具体的实现类???(动态代理)
		try {
			//获取Mapper接口的代理实现类
			EmployeeMapper mapper =  session.getMapper(EmployeeMapper.class);
			System.out.println(mapper.getClass().getName());
			
			//执行代理类中的增删改查
			Employee employee = mapper.getEmpById(1001);
			System.out.println(employee);
		} finally{
			session.close();
		}
	}
	
	
	//将获取SqlSessionFactory的代码封装成一个方法
	public SqlSessionFactory  getSqlSessionFactory() throws Exception{
		String resource = "mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = 
				new SqlSessionFactoryBuilder().build(inputStream);
		return sqlSessionFactory;
	}
	
	
	@Test
	public void testCRUD() throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();  // 不自动提交的session
		try {
			//获取Mapper接口的代理实现类对象
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			
			//添加员工信息
			Employee employee = new Employee();
			employee.setLastName("BB");
			employee.setGender("m");
			employee.setEmail("bb@atguigu.com");
			boolean result = mapper.addEmp(employee);
			System.out.println(result);
			
			//获取主键值
			System.out.println("主键值:" + employee.getId());
			
			//修改员工信息
//			Employee employee = new Employee();
//			employee.setId(1004);
//			employee.setLastName("RoseAA");
//			employee.setGender("m");
//			employee.setEmail("rose@sina.com");
//			mapper.updateEmp(employee);
			
			//删除员工信息
			//mapper.deleteEmp(1004);
			
			
			//对于增删改操作，要进行提交
			session.commit();
		} finally{
			session.close();
		}
	}
	
	
	@Test
	public void testParameters() throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			
			Employee employee = mapper.getEmpByIdAndLastName(1001, "Tom");
			System.out.println(employee);
			
		}finally{
			session.close();
		}
	}
	
	@Test
	public void testMap() throws Exception{
		SqlSessionFactory ssf = getSqlSessionFactory();
		SqlSession session = ssf.openSession();
		try {
			EmployeeMapper mapper = session.getMapper(EmployeeMapper.class);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("aa", 1001);  // id 
			map.put("bb", "Tom"); // lastName
			map.put("tableName", "tbl_employee");
			Employee employee = mapper.getEmpByMap(map);
			System.out.println(employee);
			
		}finally{
			session.close();
		}
	}

}
