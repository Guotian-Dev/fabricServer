package org.hyperledger.fabric.dirpoll;

/**
 * 控制类，在main函数中启动
 * @author PC
 *
 */
public class Launcher {
	
	public static void directoryPollStartUp() {
		
		(new DirPollThread()).start();
	}
	
}




