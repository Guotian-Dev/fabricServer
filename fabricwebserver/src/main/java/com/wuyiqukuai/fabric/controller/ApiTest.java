package com.wuyiqukuai.fabric.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ApiTest {
	
	@RequestMapping("/test")
	public String testApi() {
		return "testapi";
	}

}
