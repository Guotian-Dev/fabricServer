package org.hyperledger.fabric.sdkintegration;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.dirpoll.FileUtils;
import org.hyperledger.fabric.dirpoll.Launcher;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.junit.Test;

public class DataNormalTest {
	
	
	
	
	
	@Test
	public void testFileMove() {
		
//		FileUtils.moveFile("C:/res/dawenjian.zip", "C:/test1/dawenjian.zip");
		
		File f = new File("C:/test1/1");
		boolean mkdir = f.mkdir();
		System.out.println(mkdir);
		
	}
	
	@Test
	public void testFile() {
		File outbox = new File("C:/res");
		String[] subDirs = outbox.list();
		for(String eachDirName:subDirs) {
			System.out.println(">>>>" + eachDirName);
			
			File eachDir = new File("C:/res" + File.separator + eachDirName);
			System.out.println(eachDir.getAbsolutePath());
			System.out.println("是否是目录：" + eachDir.isDirectory());
			
		}
	}
	
	@Test
	public void FileStoreTest() {
		try {
			FileStoreDriver fsd = new FileStoreDriver("C:/res/org1.properties");
			FileHandle fh = new FileHandle("dawenjian.zip", FileHandle.MB);
			byte[] fileBlock = null;
			String latestTxID = null;
			
			List<String> paramList = new ArrayList<String>();
			paramList.add(MyDataHandleUtil.INSERTE_OPERATE);
			String[] args = paramList.toArray(new String[paramList.size()]);
			
			while((fileBlock = fh.readNextBlock(latestTxID)) != null) {
				System.out.println(new String(fileBlock));
				fsd.runTransactionWithBytes(args, fileBlock);
				latestTxID = fsd.getLateastTransactionID();
				System.out.println("################: " + new String(fsd.getPayloadByTxID(latestTxID), "UTF-8"));
			}
			fh.close();
			System.exit(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void FileStoreDriverTest() {
		try {
			FileStoreDriver fsd = new FileStoreDriver("C:/res/org1.properties");
			FileHandle fh = new FileHandle("C:/res/test.txt", 5);
			byte[] fileBlock = null;
			String latestTxID = null;
			
			List<String> paramList = new ArrayList<String>();
			paramList.add(MyDataHandleUtil.INSERTE_OPERATE);
			String[] args = paramList.toArray(new String[paramList.size()]);
			
			while((fileBlock = fh.readNextBlock(latestTxID)) != null) {
				System.out.println(new String(fileBlock));
				fsd.runTransactionWithBytes(args, fileBlock);
				latestTxID = fsd.getLateastTransactionID();
				System.out.println("################: " + new String(fsd.getPayloadByTxID(latestTxID)));
			}
			fh.close();
			System.exit(0);
			
		/***********************************
			FileStoreDriver fsd = new FileStoreDriver(false);
			String fileBlock = "";
			for(int i=0; i<1024; i++) {//1024*128
				fileBlock+="1234----" ;
			}
			fileBlock += "$$$$";
			
			fsd.runTransactionWithBytes(
				MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "vic", "111900"),
				fileBlock.getBytes()
			);
			
			System.out.println("################: " + fsd.getLateastTransactionID());
			System.out.println("################: " + new String(fsd.getPayloadByTxID(null)));
			
			fsd.queryByChainCode("vic");
			
			fileBlock += "%%%%";
			fsd.runTransactionWithBytes(
				MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE),
				fileBlock.getBytes()
			);
			
			System.out.println("################: " + fsd.getLateastTransactionID());
			System.out.println("################: " + new String(fsd.getPayloadByTxID(null)));
		***********************************/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test21() {
		String uuid = DataHandleUtil.getUUID();
		System.out.println(uuid);
		System.out.println("----------------");
		String uuid1 = DataHandleUtil.getUUID();
		System.out.println(uuid1);
		System.out.println("----------------");
		String uuid2 = DataHandleUtil.getUUID();
		System.out.println(uuid2);
		System.out.println("----------------");
		String uuid3 = DataHandleUtil.getUUID();
		System.out.println(uuid3);
	}
	
//	@Test
//	public void test20() {
//		//ABCDEFG(0开始)
//		byte[] readText = FileHandle.randomRed("C:/res/test.txt",4, 2);
//		
//		if(readText != null) {
//			System.out.println(readText.length);
//		}
//		
//		System.out.println(new String(readText,0,readText.length));
//		
//		FileHandle.byteToFile("newText1.properties", readText);
//		
//		
//		
//	}
	
	@Test
	public void test19() {
		NewDataDriver dd = new NewDataDriver();
	}
	
	@Test
	public void test18() {
		DataDriver dd = new DataDriver(true);
		try {
			
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "b", "900"));
			
//			Thread.sleep(5000);
			
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "a", "900"));
			
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "c", "900"));
			
////			Thread.sleep(5000);
//			
//			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "d", "900"));
//			
//			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "e", "900"));
//			
////			Thread.sleep(5000);
//			
//			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "f", "900"));
			
			String blockWalker = dd.blockWalker();
			System.out.println(blockWalker);
			
//			System.out.println("----------------------------------------");
//			
//			Map<String, Object> jsonToMap = DataJsonUtils.jsonToMap(blockWalker, Object.class);
//			
//			System.out.println(jsonToMap);
//			
//			System.out.println("----------------------------------------");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void test16() {
		DataDriver dd = new DataDriver(true);
		try {
			String channelQuerry1 = dd.channelQuerry("peer0.org1.example.com");
			System.out.println(channelQuerry1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test17() {
		DataDriver dd = new DataDriver(true);
		try {
			dd.blockWalker();;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void test() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("a", "100");
		map.put("b", "200");
		
		String mapJson = MyJsonUtils.toJson(map);
		System.out.println("mapJson:" + mapJson);
		
		String mapJsonReplace = mapJson.replace("{", "").replace(":", ",").replace("}", "").replace("\"", "").trim();
		System.out.println("mapJsonReplace:" + mapJsonReplace);
		
		String[] strArr = mapJsonReplace.split(",");
		
		for (String string : strArr) {
			System.out.println(string);
		}
		
		
	}
	
	@Test
	public void test2() {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("a", "100");
		map.put("b", "200");
		
		String jsonStr = MyJsonUtils.toJson(map);
	
		System.out.println(jsonStr);
		
		String[] strArr = MyDataHandleUtil.mapJsonToStringArr(jsonStr);
		
		for (String string : strArr) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test3() {
//		"move", "a", "b", "100"
		
		ArrayList<String> list = new ArrayList<String>();
		
		list.add("move");
		list.add("a");
		list.add("b");
		list.add("100");
		
		System.out.println(list);
		
//		System.out.println(list.get(1));
		
		//如果以list传递
		String[] strArr = MyDataHandleUtil.moveDataHandle(list);
		for (String string : strArr) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test4() {
		String[] strArr = MyDataHandleUtil.paramHandle(MyDataHandleUtil.DELETE_OPERATE, "b");
		System.out.println(MyJsonUtils.toJson(strArr));
		for (String string : strArr) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test5() {
		String[] strArr = MyDataHandleUtil.paramHandle(MyDataHandleUtil.QUERY_OPERATE, "b");
		System.out.println(MyJsonUtils.toJson(strArr));
		for (String string : strArr) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test6() {
		String[] strArr = MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "c", "d", "1092039");
		System.out.println(MyJsonUtils.toJson(strArr));
		for (String string : strArr) {
			System.out.println(string);
		}
	}
	
	@Test
	public void test7() {
		
		MyOrgInstance orgIns = new MyOrgInstance();
		System.out.println(orgIns);
	}
	
	//测试newChannel
	@Test
	public void test8() {
		try {
			
			//执行new
			SampleOrg org = MyOrgInstance.getOrgInstance("peerOrg1", "Org1MSP");
			
			HFClient client = MyOrgInstance.getHFClient();
			
			//执行newChannel
			Channel chain = MyOrgInstance.getChannel(true, client);
			
			Map<String, String> map = new HashMap<String, String>();
			map.put("a", "100");
			map.put("b", "200");
			
			//init chaincodeId
			ChaincodeID initChaincodeID = MyOrgInstance.initChaincodeID(org, chain, client, MyDataHandleUtil.mapJsonToStringArr(MyJsonUtils.toJson(map)));
			
			//执行交易
			MyOrgInstance.runTransaction(org, client, chain, initChaincodeID, MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			
			//执行查询
			MyOrgInstance.queryByChainCode(client, chain, initChaincodeID, "a");
			MyOrgInstance.queryByChainCode(client, chain, initChaincodeID, "b");
			
			chain.shutdown(true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//如果还要封装，就将执行的这几个过程进行封装
	@Test
	public void test10() {
		try {
			SampleOrg org = MyOrgInstance.getOrgInstance("peerOrg1", "Org1MSP");
			
			HFClient client = MyOrgInstance.getHFClient();
			//客户端上下文设置
			client.setUserContext(org.getUser("user1"));
			
			
			Channel chain = MyOrgInstance.getChannel(false, client);
			chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            
            final ChaincodeID chainCodeID = ChaincodeID.newBuilder()
            		.setName("lvqinghao888_go")
                    .setVersion("1")
                    .setPath("example_cc").build();
            MyOrgInstance.runTransaction(org, client, chain, chainCodeID, MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			
            MyOrgInstance.queryByChainCode(client, chain, chainCodeID, "a");
            MyOrgInstance.queryByChainCode(client, chain, chainCodeID, "b");
            chain.shutdown(true);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test11() {
		DataDriver dd = new DataDriver(true);
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			dd.queryByChainCode("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test15() {
		DataDriver dd = new DataDriver(true);
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "d", "900"));
			dd.queryByChainCode("d");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test13() {
		DataDriver dd = new DataDriver(true);
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			dd.queryByChainCode("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test12() {
		DataDriver dd = new DataDriver(true);
		
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "b", "900"));
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "a", "900"));
			
			System.out.println("a的值为:" + dd.queryByChainCode("a","peer0.org1.example.com"));
			System.out.println("a的值为:" + dd.queryByChainCode("a","peer1.org1.example.com"));
			System.out.println("b的值为:" + dd.queryByChainCode("b"));
			
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			
			System.out.println("a的值为:" + dd.queryByChainCode("a"));
			System.out.println("b的值为:" + dd.queryByChainCode("b"));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			String[] paramHandle = MyDataHandleUtil.paramHandle(MyDataHandleUtil.DELETE_OPERATE, "a");
//			for (String string : paramHandle) {
//				System.out.print(string + "-");
//			}
//			dd.runTransaction(paramHandle);
//			dd.queryByChainCode("a");
//		} catch (Exception e) {
//			System.out.println("a在块中不存在");
//		}
	}
	
	
	@Test
	public void test9() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("a", "100");
		map.put("b", "200");
		
		String mapJson = MyJsonUtils.toJson(map);
		System.out.println(mapJson);
	}

}
