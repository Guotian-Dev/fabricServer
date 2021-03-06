package org.hyperledger.fabric.sdkintegration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 文件处理的类
 * @author PC
 *
 */

public class FileHandle {
	
	public static final int MB = 1024*1024;
	public static final int KB = 1024;
	
	private RandomAccessFile raf;
	private long fileSize;
	private int blockSize;
	private int blockNO;
	private String blockTxIDs;
	
	/**
	 * 
	 * @param filePath 文件路径
	 * @param blockSize 块大小
	 */
	public FileHandle(String filePath, int blockSize) {
		this.blockSize = blockSize;
		this.blockNO = 0;
		this.blockTxIDs = "";
		try {
			this.raf = new RandomAccessFile(filePath, "r");
			this.fileSize = raf.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String[] getBlockTxIDs() {
		if(blockTxIDs == null) {
			return null;
		} 
		String txIDs = this.blockTxIDs.substring(1);
		return txIDs.split("~");
	}
	
	public byte[] readNextBlock(String latestTxID) {
		try {
			if(latestTxID != null) {
				blockTxIDs += ("~" + latestTxID);
				System.out.println(blockTxIDs);
			}
			byte[] blockBuf = new byte[blockSize];
			long pointer = blockNO * blockSize;
			raf.seek(pointer);
	        int hasRead = raf.read(blockBuf);
	        if(hasRead != -1) {
	        	blockNO++;
		        if(hasRead == blockSize) {
		        	return blockBuf;
		        } else { //the last block is normally less then blockSize
		        	return Arrays.copyOfRange(blockBuf, 0, hasRead);
		        }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public void close() {
		try {
			this.raf.close();
			this.blockNO = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static byte[] randomBlockRead(String filePath, int startBlock, int blockSize) { 
		RandomAccessFile raf = null;
		try{  
	        raf = new RandomAccessFile(filePath, "r"); 
	        raf.length();
	        //System.out.println("RandomAccessFile文件指针的初始位置:"+raf.getFilePointer());
	        //seek移动的单位为字节
	        long pointer = startBlock * blockSize; //从o号块开始
	        raf.seek(pointer); //移动文件指针位置  
	        byte[] buff = new byte[blockSize];  
	        int hasRead = raf.read(buff);
	        //如果读不到指定大小的文件，重新读
	        if(hasRead != blockSize && hasRead != -1) {
	        	buff = Arrays.copyOfRange(buff, 0, hasRead);
	        }
	        if(hasRead != -1) {
	        	return buff;
	        }
	    }catch(Exception e){  
	        e.printStackTrace();  
	    }finally {
	    	if(raf != null) {
	    		try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
		}
		return null;
	}
	
	
	/**
	 * byte-file
	 * @param filename
	 * @param data
	 * @throws Exception
	 */
	public static void byteToFile(String filename, byte[] data) {
		
		if (data != null) {
			
			String filepath = "C:/res/" + filename;
			
			File file = new File(filepath);
			
			if (file.exists()) {
				file.delete();
			}
			
			FileOutputStream fos = null;
			
			try {
				fos = new FileOutputStream(file);
				fos.write(data, 0, data.length);
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	} 
	
	/**
	 * 对文件进行hash
	 * @param filePath文件路径	hashCode hash方式 SHA-1/SHA-256/MD5
	 * @return
	 * @throws Exception
	 */
	public static String getHash(String filePath, String hashCode) {
			
		byte[] b = null;
		try {
			b = createChecksum(filePath, hashCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String hash = "";
		if(b != null) {
			for (int i = 0; i < b.length; i++) {
				hash += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);// 加0x100是因为有的b[i]的十六进制只有1位
			}
		}
		return hash;
	}
	
	/**
	 * 
	 * @param filePath
	 * @param hashCode - 编码SHA-1/SHA-256/MD5 
	 * @return
	 * @throws Exception
	 */
	public static byte[] createChecksum(String filePath, String hashCode) throws Exception {
		InputStream fis = new FileInputStream(filePath);

		byte[] buffer = new byte[1024];
		
		MessageDigest complete = MessageDigest.getInstance(hashCode); // 如果想使用SHA-1或SHA-256，则传入SHA-1,SHA-256
		
		int hasRead;
		
//		do {
//			hasRead = fis.read(buffer); // 从文件读到buffer，最多装满buffer
//			if (hasRead > 0) {
//				complete.update(buffer, 0, hasRead); // 用读到的字节进行MD5的计算，第二个参数是偏移量
//			}
//		} while (hasRead != -1);
		
		while ((hasRead = fis.read(buffer, 0, buffer.length)) != -1) {
			complete.update(buffer, 0, hasRead);
		}

		fis.close();
		return complete.digest();
	}
	
	
	/**
	 * 文件拆分块数
	 * @param fileBlockSize 单位 B
	 * @param fileSize 单位 B
	 * @return
	 */
	public static int fileSplit(int fileBlockSize, int fileSize) {
		
		return fileSize / fileBlockSize + 1;
		
	}
		  
	
	
}
