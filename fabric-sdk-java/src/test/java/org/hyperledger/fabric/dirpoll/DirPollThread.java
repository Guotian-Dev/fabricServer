package org.hyperledger.fabric.dirpoll;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.filestore.GlobalConfig;

/**
 * 目录轮询控制
 * @author PC
 *
 */
public class DirPollThread extends Thread {
	
	private final static Log logger = LogFactory.getLog(DirPollThread.class);
	
	//周期性的任务执行
	private final ScheduledExecutorService scheduExec;
	
	private final ExecutorService fileHandleThreadPool;
	
	//每个目录，准备一个线程
	private final Map<String, FileHandleThread> dirNameThreadMap;
	
	//将要轮询的目录
	private String outboxDir;
//	private final String outboxDir="C:/lx1;C:/lx2";
	
	//下次执行的时间间隔
	private long interval;
	
	//临时后缀名(使用多次监听的方式判断文件是否可以上传)
	private String temp_suffix;
	
	//设置为temp/noTemp
	private String mode;
	private long time;
	private int count;
	
	//fileHandle （delete/move）
	private String fileHandle;
	
//	private String temp_suffix = ".zipbac";
	
	//状态
	public static final String TERMINATED = "TERMINATED";
	public static final String RUNNING = "RUNNING";
	public static final String READYTORUN = "READYTORUN";
	
	public DirPollThread() {
		
		this.outboxDir = GlobalConfig.getOutboxDir();
		this.temp_suffix = GlobalConfig.getTemp_suffix();
		this.interval = GlobalConfig.getInterval();
		
		//模式设置
		this.mode = GlobalConfig.getMode();
		this.time = GlobalConfig.getTime();
		this.count = GlobalConfig.getCount();
		//模式设置
		
		//文件处理方式
		this.fileHandle = GlobalConfig.getFileHandle();
		//文件处理方式
		
		this.dirNameThreadMap = new HashMap<String, FileHandleThread>();
		
		//获取一个周期性执行的线程
		this.scheduExec =  Executors.newSingleThreadScheduledExecutor();
		
		//给每个目录下创建OK/FAILED文件
		String[] dirs = outboxDir.split(";");
		
		for (String dir : dirs) {

			String okPath = dir + File.separator + "ok";
			
			FileUtils.createDir(okPath);
			
			String failedPath = dir + File.separator + "failed";
			
			FileUtils.createDir(failedPath);
		}
		
		this.fileHandleThreadPool = Executors.newFixedThreadPool(
			Integer.parseInt(
				"10"
			)
		);
	}
	
	@Override
	public void run() {
		
		//执行调度任务
    	this.scheduExec.scheduleAtFixedRate(new Runnable() {
    		
    		@Override
            public void run() {
                try {
                	
                	logger.debug("目录轮询执行");
                	//获取将要轮询的目录
                	String[] dirs = outboxDir.split(";");
                	
                	//给每个目录创建一个操作线程
            		for(String dirName : dirs) {
            			File eachDir = new File(dirName);
            			
            			logger.debug("eachDir.getAbsolutePath()->" + eachDir.getAbsolutePath());
            			
        				//给每个目录配置一个线程
            			if(eachDir.isDirectory()) {
            				
            				FileHandleThread thread = null;
            				
            				if(null == dirNameThreadMap.get(dirName)) { //当前没有线程处理当前目录，则添加1个线程
            					
            					thread = createHandleThread(eachDir);
            					
                			} else {
                				
                				String flag = dirNameThreadMap.get(dirName).getFlag();
                				//TODO 发送线程执行完成后，线程对象还存在吗？还能调用getFlag()吗？
                				if(TERMINATED.equals(flag)){
                					//dirNameThreadMap.remove(eachDirName);
                					//need not remove, because put will overwrite it.
                					thread = createHandleThread(eachDir);
                				} 
                			}
            				
            				if(thread != null) {
            					dirNameThreadMap.put(dirName, thread);
            					fileHandleThreadPool.execute(thread);
            				}
            			}
            		}
            				
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, this.interval, TimeUnit.MILLISECONDS);
    	
	}
    
    /**
     * 创建一个操作文件的线程
     * @param relationDir
     * @return
     */
	public FileHandleThread createHandleThread(File relationDir) {
		
		logger.debug("relationDir->" + relationDir.getAbsolutePath());
		
		FileHandleThread thread = null;
		
		//获取目录下的所有文件名
		String[] fileNames = relationDir.list();
		
		File realFile = null;
		
		for(String fileName:fileNames) {
			
			realFile = new File(relationDir.getAbsolutePath() + File.separator + fileName);
			
			logger.debug("realFile->" + realFile.getAbsolutePath());
			
			
			//只处理文件，不处理目录
//			if(realFile.isFile() && (!realFile.getName().endsWith(this.temp_suffix))) {
//				
//				thread = new FileHandleThread(relationDir, this.temp_suffix);
//				
//				////System.out.println("SendingThread created");
//				break;
//			}
			
			if(realFile.isFile()) {
				
				//不同的模式创建不同的线程
				if("temp".equals(this.mode) && (!realFile.getName().endsWith(this.temp_suffix))) {
					
					thread = new FileHandleThread(relationDir, this.temp_suffix, !"temp".equals(this.mode), fileHandle);
					
					////System.out.println("SendingThread created");
					break;
				}
				
				if("noTemp".equals(this.mode)) {
					
					thread = new FileHandleThread(relationDir, this.time, this.count, "noTemp".equals(this.mode), fileHandle);
					
					////System.out.println("SendingThread created");
					break;
				}
				
			}
			
		}
		return thread;
	}
	
}
