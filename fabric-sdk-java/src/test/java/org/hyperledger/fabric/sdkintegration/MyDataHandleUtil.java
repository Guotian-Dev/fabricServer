package org.hyperledger.fabric.sdkintegration;

import java.util.ArrayList;
import java.util.List;

public class MyDataHandleUtil {
	
	//query //move //delete
	public static final String MOVE_OPERATE = "move";
	public static final String QUERY_OPERATE = "query";
	public static final String DELETE_OPERATE = "delete";
	public static final String INSERTE_OPERATE = "insert";
	
	/**
	 * map to String Arr
	 * json {"a":"100","b":"200"}
	 * @param map
	 * @return
	 */
	public static String[] mapJsonToStringArr(String json) {
		
		String jsonReplace = json.replace("{", "").replace(":", ",").replace("}", "").replace("\"", "").trim();
		
		return jsonReplace.split(",");
	}
	
	/**
	 * list to String[]
	 * list demo "move", "a", "b", "100"
	 * @param list
	 * @return
	 */
	public static String[] moveDataHandle(List<String> list) {
		return list.toArray(new String[list.size()]);
	}
	
	/**
	 * list to String[]
	 * list demo "move", "a", "b", "100"
	 * @param list
	 * @return
	 */
	public static String[] paramHandle(String operate, String from, String to, String data) {
		
		List<String> paramList = new ArrayList<String>();
		paramList.add(operate);
		paramList.add(from);
		paramList.add(to);
		paramList.add(data);
		
		return paramList.toArray(new String[paramList.size()]);
	}
	
	//{"query", key}
	public static String[] paramHandle(String operate, String key) {
		
		List<String> paramList = new ArrayList<String>();
		paramList.add(operate);
		paramList.add(key);
		
		return paramList.toArray(new String[paramList.size()]);
	}
	
	//{"insert", key, value}
	public static String[] paramHandle(String operate, String key, String value) {
		
		List<String> paramList = new ArrayList<String>();
		paramList.add(operate);
		paramList.add(key);
		paramList.add(value);
		
		return paramList.toArray(new String[paramList.size()]);
	}
	
	
	public static String[] paramHandle(String operate) {
		
		List<String> paramList = new ArrayList<String>();
		paramList.add(operate);
		
		return paramList.toArray(new String[paramList.size()]);
	}
	
	
	
	//{"insert", key, value}
	public static ArrayList<byte[]> paramHandle(byte[] operate, byte[] key, byte[] value) {
		
		ArrayList<byte[]> paramList = new ArrayList<byte[]>();
		paramList.add(operate);
		paramList.add(key);
		paramList.add(value);
		
		return paramList;
	}
	

}







