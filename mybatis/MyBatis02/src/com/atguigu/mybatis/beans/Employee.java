package com.atguigu.mybatis.beans;

import java.io.Serializable;

public class Employee implements Serializable{
	private Integer id ;
	private String lastName; 
	private String gender ;
	private String email;
	
	private Department dept ; //员工所在的部门
	
	public Employee() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Employee(String lastName, String gender, String email) {
		super();
		this.lastName = lastName;
		this.gender = gender;
		this.email = email;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public Department getDept() {
		return dept;
	}
	public void setDept(Department dept) {
		this.dept = dept;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", gender="
				+ gender + ", email=" + email + "]";
	} 
	
	
}
