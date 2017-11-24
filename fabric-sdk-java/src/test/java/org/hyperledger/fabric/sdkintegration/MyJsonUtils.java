package org.hyperledger.fabric.sdkintegration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * json handle gson
 * @author root
 *
 */
public class MyJsonUtils {
	
	public static Gson gson = new Gson();
	
	/**
	 * obj to json
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		if(gson == null) {
			gson = new Gson();
		}
		return gson.toJson(obj);
	}
	
	/**
	 * img to json
	 * @param obj
	 * @return
	 */
	public static String imgToJson(String path) {
		String imgStr = MyImageHandle.getImageStr(path);
		return toJson(imgStr);
	}
	
	/**
	 * json to img 
	 * @param imgJson
	 * @param path
	 * @return
	 */
	public static boolean jsonToImg(String imgJson, String path) {
		String imgStr = fromJson(imgJson, String.class);
		return MyImageHandle.generateImage(imgStr, path);
	}
	/**
	 * json to obj
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> valueType) {
		if(gson == null) {
			gson = new Gson();
		}
		return gson.fromJson(json, valueType);
	}
	
	/**
	 * json from file to obj
	 * @param readerFile
	 * @param valueType
	 * @return
	 */
	public static <T> T fromJson(Reader readerFile, Class<T> valueType) {
		if(gson == null) {
			gson = new Gson();
		}
		return gson.fromJson(readerFile, valueType);
	}
	
	/**
	 * json to List<T> from file
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> List<T> jsonToList(Reader readerFile, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type type = new MyParamType(List.class, new Class[]{elementType});
		
		return gson.fromJson(readerFile, type);
	}
	
	/**
	 * json to List<T> from string
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> List<T> jsonToList(String json, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type type = new MyParamType(List.class, new Class[]{elementType});
		
		return gson.fromJson(json, type);
	}
	
	
	/**
	 * json to Map<String, T> from string
	 * @param json
	 * @param elementType
	 * @return
	 */
	public static <T> Map<String, T> jsonToMap(String json, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type type = new MyParamType(Map.class, new Class[]{String.class, elementType});
		
		return gson.fromJson(json, type);
	}
	
	/**
	 * json to Map<String, T> from file 
	 * @param json
	 * @param elementType
	 * @return
	 */
	public static <T> Map<String, T> jsonToMap(Reader reader, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type type = new MyParamType(Map.class, new Class[]{String.class, elementType});
		
		return gson.fromJson(reader, type);
	}
	
	/**
	 * json to Qt type
	 * @param str
	 * @param elementType
	 * @return
	 */
	public static <T> List<Map<String, T>> jsonToQt(String str, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		//nei - wai
		Type typeMap = new MyParamType(Map.class, new Class[]{String.class, elementType});
		
		Type typeList = new MyParamType(List.class, new Type[]{typeMap});
		
		return gson.fromJson(str, typeList);
	}
	
	/**
	 * map qt 
	 * @param str
	 * @param elementType
	 * @return
	 */
	public static <T> Map<String, Map<String, T>> jsonToQtMap(String str, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type typeMapN = new MyParamType(Map.class, new Class[]{String.class, elementType});
		
		Type typeMapW = new MyParamType(Map.class, new Type[]{String.class, typeMapN});
		
		return gson.fromJson(str, typeMapW);
	}
	
}