//package com.wuyiqukuai.fabric.controller;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.hyperledger.fabric.sdkintegration.MyJsonUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.wuyiqukuai.fabric.domain.Employee;
//import com.wuyiqukuai.fabric.service.EmployeeService;
//import com.wuyiqukuai.fabric.util.DataJsonUtils;
//@RequestMapping("/emp")
//@Controller
//public class EmployeeController {
//	
//	private final static Logger logger = LoggerFactory.getLogger(ChainCodeController.class);
//	
//	@Autowired
//	private EmployeeService employeeService;
//	
//	@RequestMapping("/get")
//	public String getEmps(Map<String, Object> map) {
//		
//		List<Employee> emps = employeeService.getEmps();
//		
//		String empsJson = MyJsonUtils.toJson(emps);
//		
//		map.put("emps", empsJson);
//		
//		return "index";
//	}
//	
//	@ResponseBody
//	@RequestMapping("/getObj")
//	public Employee getObj(int id) {
//		
//		Employee empObjById = employeeService.getEmpObjById(id);
//		
//		return empObjById;
//	}
//	
//	@ResponseBody
//	@RequestMapping("/getList")
//	public List<Employee> getList() {
//		
//		List<Employee> emps = employeeService.getEmps();
//		
//		return emps;
//	}
//	
//	
//	
//	//临时提供一个展示多文件的方式
//	@ResponseBody
//	@RequestMapping("/empPage")
//	public String empPate(String startTime, String endTime, Integer page, Integer rows) {
//		
//		logger.debug("startTime->" + startTime);
//		logger.debug("endTime->" + endTime);
//		logger.debug("page->" + page);
//		logger.debug("rows->" + rows);
//		
//		Map<String, Object> pageRows = new HashMap<String, Object>();
//		pageRows.put("min", (page - 1) * rows);
//		pageRows.put("max", rows);
//		
//		List<Employee> emps = employeeService.getEmpPage(pageRows);
//		
//		int count = employeeService.getEmpCount();
//		
//		
//		Map<String, Object> rtMap = new HashMap<String, Object>();
//		rtMap.put("total", count);
//		rtMap.put("rows", emps);
//		
//		String rtJson = DataJsonUtils.toJson(rtMap);
//		
//		logger.debug("json:" + rtJson);
//		
//		return rtJson;
//	}
//	
//	
//	
//	
//	
////	@RequestMapping("/ex")
////	public String testException() throws Exception {
////		throw new Exception();
////	}
//}
