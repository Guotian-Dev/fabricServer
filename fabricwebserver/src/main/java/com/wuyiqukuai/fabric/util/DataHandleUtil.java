package com.wuyiqukuai.fabric.util;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import sun.misc.BASE64Encoder;

public class DataHandleUtil {
	
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
	
	//sha256hash
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
    
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
    
    //md5
	public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		//确定计算方法
		MessageDigest md5=MessageDigest.getInstance("MD5");
		BASE64Encoder base64en = new BASE64Encoder();
		//加密后的字符串
		String newstr=base64en.encode(md5.digest(str.getBytes("utf-8")));
		return newstr;
	}
	
	//base64
    public static String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = new BASE64Encoder().encode(b);  
        }  
        return s;  
    } 
	
}
