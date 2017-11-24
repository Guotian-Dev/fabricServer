package org.hyperledger.fabric.filestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.dirpoll.FileUtils;
import org.hyperledger.fabric.dirpoll.Launcher;
import org.junit.Test;

public class FileStoreTest {
	
	public static void main(String[] args) {
		Launcher.directoryPollStartUp();
	}
	
	@Test
	public void testMap() {
		Map<String, String> map = new HashMap<String, String>();
		String long1 = map.get("A");
		System.out.println(long1);
	}
	
	@Test
	public void testFile1() {
		
		FileUtils.handleFile("C:/res/res.zip", FileHandle.MB);
	}
	
	@Test
	public void testFile() {
		FileStoreDriver fsd = new FileStoreDriver("C:/res/orgf.properties");
		
		String queryByChainCode = fsd.queryByChainCode("file_total");
		
		System.out.println(queryByChainCode);
	}
	
	@Test
	public void FileTest() {
		try {
			FileStoreDriver fsd = new FileStoreDriver("C:/res/orgt.properties");
			// upload the file
			FileHandle fh = new FileHandle("C:/res/res.zip", FileHandle.MB); 
			
			//param Handle
			List<String> paramList = new ArrayList<String>();
			paramList.add("insert");
			String[] args = paramList.toArray(new String[paramList.size()]);
			
			byte[] fileBlock = null;
			String latestTxID = null;
			
			while ((fileBlock = fh.readNextBlock(latestTxID)) != null) {
				
				fsd.runTransactionWithBytes(args, fileBlock);
				
				latestTxID = fsd.getLateastTransactionID();
				// System.out.println("################: " + new
				// String(fsd.getPayloadByTxID(latestTxID)));
			}

			// insert the meta info of the latest file uploaded
			paramList.add(fh.getFileUUID());
			paramList.add(fh.getFileID());
			paramList.add(fh.getJsonMetaData());
			args = paramList.toArray(new String[paramList.size()]);
			fsd.runTransactionWithBytes(args, null);
			
			System.out.println("文件上传成功");
			
			// query meta info
			String queryByChainCode = fsd.queryByChainCode("file_total");
			
			System.out.println(queryByChainCode);
			
			String queryByChainCode2 = fsd.queryByChainCode("file_1");
			
			System.out.println(queryByChainCode2);

			fh.close();
			System.exit(0);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void FileStoreDriverTest2() {
		try {
			
			//System.out.println("ab".getBytes().length);
			//System.out.println("ab".getBytes("UTF-8").length);
			//System.exit(0);
//			GlobalConfig gcfg = new GlobalConfig("xxxxx");
			FileStoreDriver fsd = new FileStoreDriver("C:/res/orgt.properties");
			//FileStoreDriver fsd = new FileStoreDriver(gcfg.GetFabricConfigFile());
			
			String fileBlock = "";
			for(int i=0; i< ((1024*1024) + (512*1024))/16; i++) {//1024*128
				fileBlock += "12345678--------" ;
			}
			fileBlock += "$$$$";
			
			List<String> paramList = new ArrayList<String>();
			paramList.add("insert");
			String[] args = paramList.toArray(new String[paramList.size()]);
			
			byte[] payload = fileBlock.getBytes("UTF-8");
			System.out.println("################: " + payload.length);
			fsd.runTransactionWithBytes(args, payload);
			String latestTxID = fsd.getLateastTransactionID();
			payload = fsd.getPayloadByTxID(latestTxID);
			System.out.println("################: " + payload.length);
			System.out.println("################: " + new String(payload));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//http://www.kjson.com/
	
	@Test
	public void FileStoreDriverTest() {
		try {
			
			FileStoreDriver fsd = new FileStoreDriver("C:/res/orgt.properties");
			fsd.queryByChainCode("file_total");
			
			//upload the file
			FileHandle fh = new FileHandle("D:/test.txt", 15); //2*FileHandle.MB - 256
			List<String> paramList = new ArrayList<String>();
			paramList.add("insert");
			String[] args = paramList.toArray(new String[paramList.size()]);
			byte[] fileBlock = null;
			String latestTxID = null;
			while((fileBlock = fh.readNextBlock(latestTxID)) != null) {
				System.out.println(new String(fileBlock));
				fsd.runTransactionWithBytes(args, fileBlock);
				latestTxID = fsd.getLateastTransactionID();
				//System.out.println("################: " + new String(fsd.getPayloadByTxID(latestTxID)));
			}
			
			//insert the meta info of the latest file uploaded
			paramList.add(fh.getFileUUID());
			paramList.add(fh.getFileID());
			paramList.add(fh.getJsonMetaData());
			args = paramList.toArray(new String[paramList.size()]);
			fsd.runTransactionWithBytes(args, null);
			
			//query meta info 
			fsd.queryByChainCode(fh.getFileUUID());
			fsd.queryByChainCode("file_total");
			fsd.queryByChainCode("file_1");
			fsd.queryByChainCode(1, 2);
			
			fh.close();
			System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}






