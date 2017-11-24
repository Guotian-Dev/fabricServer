package com.wuyiqukuai.fabric.util;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * json handle gson
 * @author root
 *
 */
public class DataJsonUtils {
	
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
	 * obj to json
	 * @param obj
	 * @return
	 */
	public static String toJsonContainDate(Object obj) {
		Gson gson = new GsonBuilder()  
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				  .create();
		return gson.toJson(obj);
	}
	
	
	/**
	 * img to json
	 * @param obj
	 * @return
	 */
	public static String imgToJson(String path) {
		String imgStr = ImageHandle.getImageStr(path);
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
		return ImageHandle.generateImage(imgStr, path);
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
	 * 
	 * @param json
	 * @param valueType
	 * @return
	 */
	public static <T> T fromJsonContainDate(String json, Class<T> valueType) {
		
		Gson gson = new GsonBuilder()  
				  .setDateFormat("yyyy-MM-dd HH:mm:ss")  
				  .create(); 
		
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
		
		Type type = new ParamType(List.class, new Class[]{elementType});
		
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
		
		Type type = new ParamType(List.class, new Class[]{elementType});
		
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
		
		Type type = new ParamType(Map.class, new Class[]{String.class, elementType});
		
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
		
		Type type = new ParamType(Map.class, new Class[]{String.class, elementType});
		
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
		Type typeMap = new ParamType(Map.class, new Class[]{String.class, elementType});
		
		Type typeList = new ParamType(List.class, new Type[]{typeMap});
		
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
		
		Type typeMapN = new ParamType(Map.class, new Class[]{String.class, elementType});
		
		Type typeMapW = new ParamType(Map.class, new Type[]{String.class, typeMapN});
		
		return gson.fromJson(str, typeMapW);
	}
	
	// Map<String, List<Map<String, String>>>
	public static <T> Map<String, List<Map<String, T>>> jsonToDcQt(String str, Class<T> elementType) {
		if(gson == null) {
			gson = new Gson();
		}
		
		Type typeMapN = new ParamType(Map.class, new Class[]{String.class, elementType});
		
		Type typeMapW1 = new ParamType(List.class, new Type[]{String.class, typeMapN});
		
		Type typeMapW2= new ParamType(Map.class, new Type[]{String.class, typeMapW1});
		
		return gson.fromJson(str, typeMapW2);
	}
	
//	/**
//	 * fileInfo json 生成
//	 */
//	public static String fileInfo() {
//		
//		List<BlockFileInfo> list = new ArrayList<BlockFileInfo>();
//		
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file1", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file2", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file3", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file4", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file5", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file6", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file7", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file8", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file9", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		list.add(new BlockFileInfo("uuid-or-hash-value-of-the-file10", "8192Bytes", "n", "xxxx.data", "2017-08-09 12:30:45", "198888888888888Bytes", null));
//		
//		
//		Map<String, Object> map = new HashMap<String,Object>();
//		map.put("file_total", "389");
//		map.put("file_begin", "369");
//		map.put("file_end", "379");
//		map.put("file_group", list);
//		
//		return DataJsonUtils.toJson(map);
//	}
	
}