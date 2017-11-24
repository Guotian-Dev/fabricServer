package com.wuyiqukuai.fabric.configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hyperledger.fabric.filestore.FileStoreDriver;
import org.hyperledger.fabric.filestore.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.wuyiqukuai.fabric.controller.ChainCodeController;

@Configuration
//@EnableScheduling
public class ChainDriver {
	
	private final static Logger logger = LoggerFactory.getLogger(ChainCodeController.class);
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	
//	@Bean
//	public DataDriver dataDriver() {
//		return new DataDriver(true);
//	}
	
//	@Bean
//	public FileStoreDriver fileStoreDriver() {
//		return new FileStoreDriver(GlobalConfig.getOrgConfPathFalse());
//	}
	
	
//    @Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次
//    public void scheduler() {
//    	
//    	String name = Thread.currentThread().getName();
//    	logger.info("name" + name);
//    	
//    	logger.info("startTime" + System.currentTimeMillis());
//    	
//    	try {
//			Thread.currentThread().sleep(20000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//        logger.info(">>>>>>>>>>>>> scheduled ... ");
//        logger.info("endTime" + System.currentTimeMillis());
//    }
    
//    @Scheduled(fixedDelay = 5000) // 每5秒执行一次
//    public void scheduler() {
//    	System.out.println("现在时间：" + dateFormat.format(new Date()) + Thread.currentThread().getName());
//    	
//    	try {
//			Thread.currentThread().sleep(10000);
//			System.out.println("1");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	
//    }
//    @Scheduled(fixedRate = 5000) // 每5秒执行一次
//    public void scheduler() {
//    	System.out.println("现在时间：" + dateFormat.format(new Date()) + Thread.currentThread().getName());
//    	
//    	try {
//    		Thread.currentThread().sleep(300);
//    		System.out.println("1");
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	
//    }
	
//    @Scheduled(cron = "0/5 * * * * ?") // 每5秒执行一次(程序执行完后，再过5秒执行)
//    public void scheduler() {
//    	System.out.println("现在时间：" + dateFormat.format(new Date()) + Thread.currentThread().getName());
//    	
//    	try {
//    		Thread.currentThread().sleep(10000);
//    		System.out.println("1");
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	
//    }

}