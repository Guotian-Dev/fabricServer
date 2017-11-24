package com.wuyiqukuai.fabric;

import org.hyperledger.fabric.dirpoll.DirPollThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableCaching

public class Application extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

//	@Autowired
//	private DruidDataSource druidDataSource;
//
//	@Bean
//	public PlatformTransactionManager txManager() {
//		return new DataSourceTransactionManager(druidDataSource);
//	}
	
//	@Autowired
//	private static GlobalConfig globalConfig;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		//目录轮询操作
//		(new DirPollThread()).start();
	}
}
