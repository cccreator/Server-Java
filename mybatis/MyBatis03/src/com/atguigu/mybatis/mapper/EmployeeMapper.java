package com.atguigu.mybatis.mapper;

import java.util.List;

import com.atguigu.mybatis.beans.Employee;

public interface EmployeeMapper {
	
	public Employee getEmpById(Integer id );
	
	public void addEmp(Employee employee);
	
	public List<Employee> getEmps();
}
