package org.hyperledger.fabric.filestore;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件
 * @author PC
 *
 */
public class GlobalConfig {
	
	private static Properties cfg;
	
	//driver配置
	private static String orgConfPathTrue;
	private static String orgConfPathFalse;
	
	//目录轮询目录及相关配置
	private static String outboxDir;
	private static long interval;
	private static String temp_suffix;
	
	//目录轮询方式
	private static String mode;
	//使用监听方式，循环检测的次数和检测时间设置
	private static long time;
	private static int count;
	
	//文件处理方式
	private static String fileHandle;
	
	
	//文件hash
	private static String hashCode;

	static {
		//加载配置文件
		getCfg("C:/res/globalConf.properties");
	}
	
	
	/**
	 * 读取配置文件
	 * @param cfgPath
	 */
    private static void getCfg(String cfgPath) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(cfgPath));
			cfg = new Properties();
			cfg.load(in);
			
			orgConfPathTrue = (String) cfg.get("orgConfPathTrue");
			orgConfPathFalse = (String) cfg.get("orgConfPathFalse");
			outboxDir = (String) cfg.get("outboxDir");
			interval = Long.valueOf((String) cfg.get("interval"));
			temp_suffix = (String) cfg.get("temp_suffix");
			
			mode = (String)cfg.get("mode");
			time = Long.valueOf((String)cfg.get("time"));
			count = Integer.valueOf((String)cfg.get("count"));
			fileHandle = (String)cfg.get("fileHandle");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    
	public static String getFileHandle() {
		return fileHandle;
	}

	public static String getOrgConfPathTrue() {
		return orgConfPathTrue;
	}


	public static String getOrgConfPathFalse() {
		return orgConfPathFalse;
	}

	public static String getOutboxDir() {
		return outboxDir;
	}

	public static long getInterval() {
		return interval;
	}

	public static String getTemp_suffix() {
		return temp_suffix;
	}

	public static String getHashCode() {
		return hashCode;
	}

	public static String getMode() {
		return mode;
	}

	public static long getTime() {
		return time;
	}

	public static int getCount() {
		return count;
	}
}