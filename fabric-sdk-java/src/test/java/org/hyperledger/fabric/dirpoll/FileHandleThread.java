package org.hyperledger.fabric.dirpoll;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.filestore.FileHandle;

public class FileHandleThread implements Runnable {
	
	private final static Log logger = LogFactory.getLog(FileHandleThread.class);
	
	private File relationDir;
	private String path;
	private volatile String flag;
	
	//true 循环检测  false 后缀
	private boolean mode;
	//使用临时后缀的方式
	private String temp_suffix;
	
	private long time;
	private int count;
	
	//文件处理方式
	private String fileHandle;
	
	
	
//	public FileHandleThread(File relationDir, String temp_suffix) {
//		
//		this.temp_suffix = temp_suffix;
//		this.relationDir = relationDir;
//		
//		if(relationDir.getAbsolutePath().endsWith("/") || relationDir.getAbsolutePath().endsWith("\\")) {
//			this.path = relationDir.getAbsolutePath();
//		}
//		
//		this.path = relationDir.getAbsolutePath() + File.separator;
//		
//		logger.debug("relationDir->" + relationDir);
//		logger.debug("path->" + path);
//		
//		setFlag(DirPollThread.READYTORUN);
//	}
	
	//useTemp
	public FileHandleThread(File relationDir, String temp_suffix, boolean mode, String fileHandle) {
		
		this.relationDir = relationDir;
		this.mode = mode;
		this.temp_suffix = temp_suffix;
		this.fileHandle = fileHandle;
		
		
		if(relationDir.getAbsolutePath().endsWith("/") || relationDir.getAbsolutePath().endsWith("\\")) {
			this.path = relationDir.getAbsolutePath();
		}
		
		this.path = relationDir.getAbsolutePath() + File.separator;
		
		logger.debug("relationDir->" + relationDir);
		logger.debug("path->" + path);
		
		setFlag(DirPollThread.READYTORUN);
		
		//System.out.println("临时后缀：count->" + count + "time->" + time + "mode->" + mode + "suffix->" + temp_suffix);
		
	}
	
	//useNoTemp
	public FileHandleThread(File relationDir, long time, int count, boolean mode, String fileHandle) {
		this.relationDir = relationDir;
		this.fileHandle = fileHandle;
		
		if(relationDir.getAbsolutePath().endsWith("/") || relationDir.getAbsolutePath().endsWith("\\")) {
			this.path = relationDir.getAbsolutePath();
		}
		
		this.path = relationDir.getAbsolutePath() + File.separator;
		
		this.time = time;
		this.count = count;
		this.mode = mode;
		
		
		logger.debug("relationDir->" + relationDir);
		logger.debug("path->" + path);
		
		setFlag(DirPollThread.READYTORUN);
		
		//System.out.println("循环检测：count->" + count + "time->" + time + "mode->" + mode + "suffix->" + temp_suffix);
		
	}

	public String getFlag() {
		String retval;
		synchronized(this) {
			retval = this.flag;
		}
		return retval;
	}
	
	private  void setFlag(String flag) {
		synchronized(this) {
			this.flag = flag;
		}
	}
	
	@Override
	public void run() {
		String[] fileNames = this.relationDir.list();
		
		if(mode) {
			//System.out.println(mode);
			cycleListen(fileNames);
		} else {
			useTempListen(fileNames);
		}
		//不使用finally,若出现错误,主动抛出异常,当前目录无法继续被处理，因为thread = createHandleThread(eachDir, globalConfig);不会被执行
		//但是其他目录不受影响，实际使用中，不应该启动两个进程进行操作
		setFlag(DirPollThread.TERMINATED);
	}
	

	/**
	 * 通过使用临时后缀的方式控制文件上传
	 * @param fileNames
	 */
	private void useTempListen(String[] fileNames) {
		//System.out.println(mode);
		try {
			setFlag(DirPollThread.RUNNING);
			
			for(String fileName:fileNames) {
				
				File file = new File(this.path + fileName);
				
				logger.debug("file->" + file.getAbsolutePath());
				
				if(file.isFile() && (!file.getName().endsWith(this.temp_suffix))) {
					
					//将文件写到块上
					logger.debug("--处理文件------------------------------------------");
					
					//return fileUUID / failed
					String  rtResult = FileUtils.handleFile(file.getAbsolutePath(), FileHandle.MB);
					//System.out.println("do something");
					
					if(rtResult != null && !rtResult.equals("failed") && ("move").equals(fileHandle)) {
						//将文件剪切
						logger.debug("--剪切文件------------------------------------------");
						
//						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + "000");
						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + rtResult);
					}
					
					if(rtResult != null && !rtResult.equals("failed") && ("delete").equals(fileHandle)) {
						//将文件剪切
						logger.debug("--删除文件------------------------------------------");
						
//						System.out.println("文件路径：" + file.getAbsolutePath());
						
//						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + "000");
						FileUtils.deleteFile(file.getAbsolutePath());
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 通过判断文件大小的变化来控制是否上传文件到块上
	 * @param fileNames
	 */
	private void cycleListen(String [] fileNames) {
		try {
			setFlag(DirPollThread.RUNNING);
			
			//System.out.println(Thread.currentThread().getName());
			
			for(String fileName:fileNames) {
				
				File file = new File(this.path + fileName);
				
				logger.debug("file->" + file.getAbsolutePath());
				
				if(file.isFile()) {
					
					Long lastLength = 0L;
					//处理之前对其的状态进行判断
					
					lastLength = file.length();
					
					Thread.currentThread().sleep(1000);
					
					//检测10次
					for(int i = 0; i < count; i++) {
						if(file.length() != lastLength) {
							//更新文件的长度
							lastLength = file.length();
						}
						
						//System.out.println("当前检测次数" + i);
						
						//当前线程休息10秒钟
						Thread.currentThread().sleep(time);
					}
					
					//将文件写到块上
					logger.debug("--处理文件------------------------------------------");
					
					//return fileUUID / failed
					String  rtResult = FileUtils.handleFile(file.getAbsolutePath(), FileHandle.MB);
					//System.out.println("处理文件");
					
					if(rtResult != null && !rtResult.equals("failed") && ("move").equals(fileHandle)) {
						//将文件剪切
						logger.debug("--剪切文件------------------------------------------");
						
//						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + "000");
						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + rtResult);
					}
					
					if(rtResult != null && !rtResult.equals("failed") && ("delete").equals(fileHandle)) {
						//将文件剪切
						logger.debug("--删除文件------------------------------------------");
						
//						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + "000");
//						System.out.println("文件路径：" + file.getAbsolutePath());
						FileUtils.deleteFile(file.getAbsolutePath());
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	
	
//	@Override
//	public void run() {
//		String[] fileNames = this.relationDir.list();
//		
//		try {
//			setFlag(DirPollThread.RUNNING);
//			
//			for(String fileName:fileNames) {
//				
//				File file = new File(this.path + fileName);
//				
//				logger.debug("file->" + file.getAbsolutePath());
//				
//				if(file.isFile() && (!file.getName().endsWith(this.temp_suffix))) {
//					
//					//将文件写到块上
//					logger.debug("--处理文件------------------------------------------");
//					
//					//return fileUUID / failed
//					String  rtResult = FileUtils.handleFile(file.getAbsolutePath(), FileHandle.MB);
//					//System.out.println("do something");
//					
//					if(rtResult != null && !rtResult.equals("failed")) {
//						//将文件剪切
//						logger.debug("--剪切文件------------------------------------------");
//						
//						FileUtils.moveFile(file.getAbsolutePath(), this.path + "ok" + File.separator + fileName + "." + rtResult);
//					}
//					
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		//不使用finally,若出现错误,主动抛出异常,当前目录无法继续被处理，因为thread = createHandleThread(eachDir, globalConfig);不会被执行
//		//但是其他目录不受影响，实际使用中，不应该启动两个进程进行操作
//		setFlag(DirPollThread.TERMINATED);
//	}
	
}