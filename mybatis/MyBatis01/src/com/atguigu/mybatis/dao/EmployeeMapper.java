package com.atguigu.mybatis.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.atguigu.mybatis.beans.Employee;

//可以理解为jdbc中dao层写的 XxxxDao.
public interface EmployeeMapper {

	//根据id查询对应的员工信息
	public Employee getEmpById(Integer id );
	
	//添加员工信息
	/**
	 * tips: 如果想要在做完增删改操作后获取到对数据库的影响条数,
	 * 		 可以直接在接口中方法的返回值定义即可.
	 */
	public boolean addEmp(Employee employe);
	
	//修改员工信息
	public void updateEmp(Employee employee);
	
	//根据id删除员工信息
	public void deleteEmp(Integer id );
	
	//传递多个参数
	public Employee getEmpByIdAndLastName(@Param("id")Integer id , @Param("lastName")String lastName);
		
	public Employee getEmpByMap(Map<String,Object> map );
}
