package org.hyperledger.fabric.dirpoll;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.filestore.FileHandle;
import org.hyperledger.fabric.filestore.FileStoreDriver;
import org.hyperledger.fabric.filestore.GlobalConfig;

/**
 * 文件处理的工具类
 * @author PC
 *
 */
public class FileUtils {
	
	private final static Log logger = LogFactory.getLog(FileUtils.class);
	
	/**
	 * 文件保存
	 * @param filePath
	 * @param blockSize
	 */
	public static String handleFile(String filePath, int blockSize) {
		
		FileHandle fh = null;
		
		String fileUUID = null;
		
		
		try {
			
			FileStoreDriver fsd = new FileStoreDriver(GlobalConfig.getOrgConfPathFalse());
			// upload the file
			fh = new FileHandle(filePath, blockSize); 
			
			//param Handle
			List<String> paramList = new ArrayList<String>();
			paramList.add("insert");
			String[] args = paramList.toArray(new String[paramList.size()]);
			
			byte[] fileBlock = null;
			String latestTxID = null;
			
			while ((fileBlock = fh.readNextBlock(latestTxID)) != null) {
				
				fsd.runTransactionWithBytes(args, fileBlock);
				latestTxID = fsd.getLateastTransactionID();
			}
	
			// insert the meta info of the latest file uploaded
			paramList.add(fh.getFileUUID());
			// return fileUUID
			fileUUID = fh.getFileUUID();
			
			paramList.add(fh.getFileID());
			paramList.add(fh.getJsonMetaData());
			args = paramList.toArray(new String[paramList.size()]);
			fsd.runTransactionWithBytes(args, null);
			
			
			logger.debug("文件上传成功");
			
			// query meta info
			String queryByChainCode = fsd.queryByChainCode("file_total");
			
			logger.debug(queryByChainCode);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return "failed";
		} finally {
			if(fh != null) {
				fh.close();
			}
		}
		return fileUUID;
	}
	
	/**
	 * 目录创建
	 * @param destDirName
	 * @return
	 */
    public static boolean createDir(String destDirName) {  
        File dir = new File(destDirName);  
        if (dir.exists()) {  
            logger.debug("创建目录" + destDirName + "失败，目标目录已经存在");  
            return false;  
        }  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        //创建目录  
        if (dir.mkdirs()) {  
        	logger.debug("创建目录" + destDirName + "成功！");  
            return true;  
        } else {  
        	logger.debug("创建目录" + destDirName + "失败！");  
            return false;  
        }  
    }
	
	
	
	/**
	 * 移动文件
	 * @param from
	 * @param to
	 */
	public static void moveFile(String from, String to) throws Exception{
		
		File foFile = new File(from);
		
		File toFile = new File(to); 
		
		if(!foFile.renameTo(toFile)) {
			System.out.println("Rename error, newFileName: " + toFile.getAbsolutePath());
			throw new Exception("failed,Rename error");
		} else {
			System.out.println("Rename OK, newFileName: " + toFile.getAbsolutePath());
		}
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 * @return
	 */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
