package com.wuyiqukuai.fabric.service;

import java.util.List;
import java.util.Map;

import com.wuyiqukuai.fabric.domain.Employee;

public interface EmployeeService {
	
	public List<Employee> getEmps();

	public Employee getEmpObjById(int id);

	public List<Employee> getEmpPage(Map<String, Object> pageRows);

	public int getEmpCount();

}
