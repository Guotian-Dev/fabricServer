package org.hyperledger.fabric.sdkintegration;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.junit.Test;

public class MyNormalTest {

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
	public void test16() {
		
//		Map<String, String> map = new HashMap<String, String>();
//		
////		map.put("peer0.org1.example.com", "grpc://10.0.6.226:7051");
////		map.put("peer1.org1.example.com", "grpc://10.0.6.226:7056");
//		
//		map.put("peerName", "peer0.org1.example.com");
//		map.put("peerName", "peer1.org1.example.com");
		
		PeerDomain peer1 = new PeerDomain("peer0.org1.example.com", "grpc://10.0.6.226:7051");
		PeerDomain peer2 = new PeerDomain("peer1.org1.example.com", "grpc://10.0.6.226:7056");
		
		List<PeerDomain> listPeer = new ArrayList<PeerDomain>();
		listPeer.add(peer1);
		listPeer.add(peer2);
		
		
		
		String json = MyJsonUtils.toJson(listPeer);
		System.out.println(json);
		
		
		
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
			dd.queryByChainCode1("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test15() {
		DataDriver dd = new DataDriver(true);
		try {
			System.out.println("************************************************************************");
			System.out.println(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "d", "900").toString());
			System.out.println("************************************************************************");
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.INSERTE_OPERATE, "d", "900"));
			dd.queryByChainCode1("d");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test13() {
		DataDriver dd = new DataDriver(false);
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			dd.queryByChainCode1("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test12() {
		DataDriver dd = new DataDriver(false);
		try {
			dd.runTransaction(MyDataHandleUtil.paramHandle(MyDataHandleUtil.MOVE_OPERATE, "a", "b", "100"));
			dd.queryByChainCode1("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
