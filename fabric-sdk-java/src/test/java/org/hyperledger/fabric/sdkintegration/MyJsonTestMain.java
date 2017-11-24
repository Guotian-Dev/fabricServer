package org.hyperledger.fabric.sdkintegration;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

public class MyJsonTestMain {
//	Gson gson = new Gson();  
//	java.lang.reflect.Type type = new TypeToken<JsonBean>() {}.getType();  
//	JsonBean jsonBean = gson.fromJson(json, type)
	
	
	public static void main(String[] args) {
		Student student = new Student();  
	    student.id = 1;  
	    student.nickName = "乔晓松";  
	    student.age = 22;  
	    student.email = "965266509@qq.com"; 
	    
//	    String stuJson = JsonUtils.toJson(student);
//	    System.out.println(stuJson);
	    
	    ArrayList<String> books = new ArrayList<String>();  
        books.add("数学");  
        books.add("语文");  
        books.add("英语");  
        books.add("物理");  
        books.add("化学");  
        books.add("生物");  
        student.books = books;  
        
//      String stuJson = JsonUtils.toJson(student);
//	    System.out.println(stuJson);
	    //{"id":1,"nickName":"乔晓松","age":22,"email":"965266509@qq.com",
	    //"books":["数学","语文","英语","物理","化学","生物"]}
	    
	    HashMap<String, String> booksMap = new HashMap<String, String>();  
        booksMap.put("1", "数学");  
        booksMap.put("2", "语文");  
        booksMap.put("3", "英语");  
        booksMap.put("4", "物理");  
        booksMap.put("5", "化学");  
        booksMap.put("6", "生物");  
        student.booksMap = booksMap; 
        String stuJson = MyJsonUtils.toJson(student);
//	    System.out.println(stuJson);
//	    {"id":1,"nickName":"乔晓松","age":22,"email":"965266509@qq.com",
//	    "books":["数学","语文","英语","物理","化学","生物"],
//	    "booksMap":{"1":"数学","2":"语文","3":"英语","4":"物理","5":"化学","6":"生物"}}
	    
	    String str = "{'id':1,'nickName':'乔晓松','age':22,'email':'965266509@qq.com',"
	    	    + "'books':['数学','语文','英语','物理','化学','生物'],"
	    	    + "'booksMap':{'1':'数学','2':'语文','3':'英语','4':'物理','5':'化学','6':'生物'}}";
	    
	    Student stu2 = MyJsonUtils.fromJson(str, Student.class);
	    
//	    System.out.println(stu2);
	    
	    Map<String, Student> map = new HashMap<String, Student>();
	    
	    map.put("stu1", student);
	    
	    
	    ///////////////////////////////////////////////////////////
	    
	    System.out.println("***************List<Map<String, Student>>*******************");
	    Map<String, Student> stuMap1 = new HashMap<String, Student>();
	    
	    stuMap1.put("stu1", student);
	    
		Map<String, Student> stuMap2 = new HashMap<String, Student>();
			    
		stuMap2.put("stu2", student);
		
		Map<String, Student> stuMap3 = new HashMap<String, Student>();
			    
		stuMap3.put("stu3", student);
		stuMap3.put("stu33", student);
	    
	    List<Map<String, Student>> listMap = new ArrayList<Map<String, Student>>();
	    listMap.add(stuMap1);
	    listMap.add(stuMap2);
	    listMap.add(stuMap3);
	    
	    String listMapStr = MyJsonUtils.toJson(listMap);
	    System.out.println(listMapStr);
	    
	    List<Map<String, Student>> jsonToQt = MyJsonUtils.jsonToQt(listMapStr, Student.class);
	    System.out.println(jsonToQt);
	    
	    Map<String, Map<String, Student>> mapqt = new HashMap<String, Map<String, Student>>();
	    mapqt.put("1", stuMap1);
	    mapqt.put("2", stuMap2);
	    mapqt.put("3", stuMap3);
	    
	    String mapQt = MyJsonUtils.toJson(mapqt);
	    
	    Map<String, Map<String, Student>> jsonToQtMap = MyJsonUtils.jsonToQtMap(mapQt, Student.class);
	    System.out.println(jsonToQtMap);
	    
	    
		System.out.println("***************List<Map<String, Student>>*******************");
	    //////////////////////////////////////////////////////////
	    
//	    System.out.println(map);
	    
	    List<String> list = new ArrayList<String>();
	    
	    String stuMap = MyJsonUtils.toJson(map);
//	    System.out.println(stuMap);
	    Map<String, Student> map1 = MyJsonUtils.fromJson(stuMap, Map.class);
//	    System.out.println(map1);
	    
	    list.add("1");
	    list.add("2");
	    list.add("3");
	    list.add("4");
	    list.add("5");
	    
	    String stuList = MyJsonUtils.toJson(list);
//	    System.out.println(stuList);
	    
	    List<String> list1 = MyJsonUtils.fromJson(stuList, List.class); 
//	    System.out.println(list1);
	    
	    Map<String, String> mapz = new HashMap<String, String>();
	    mapz.put("1", "a");
	    mapz.put("1", "a");
	    mapz.put("1", "a");
	    mapz.put("1", "a");
	    
	    Map<String, Map<String, String>> mapFz = new HashMap<String, Map<String, String>>();
	    
	    mapFz.put("A", mapz);
	    mapFz.put("B", mapz);
	    mapFz.put("C", mapz);
	    
	    String fz = MyJsonUtils.toJson(mapFz);
	    System.out.println(fz);
	    
	    Map<String, Map<String, String>> mapfzfromjson = MyJsonUtils.fromJson(fz, Map.class);
	    System.out.println(mapfzfromjson);
	    
	    
	    String json = "{\"a\":\"100\",\"b\":[{\"b1\":\"b_value1\",\"b2\":\"b_value2\"},"
	    		+ "{\"b1\":\"b_value1\",\"b2\":\"b_value2\"}],"
	    		+ "\"c\": {\"c1\":\"c_value1\",\"c2\":\"c_value2\"}}";
	    
	    Gson gson = new Gson();  
	    //java.lang.reflect.Type type = new TypeToken<JsonBean>() {}.getType();  
	    
	    JsonBean jsonBean = gson.fromJson(json, JsonBean.class);
	    System.out.println("************");
	    System.out.println(jsonBean);
	    System.out.println("************");
	    
	    Map<String, List<String>> maplist = new HashMap<String, List<String>>();
	    maplist.put("a", list);
	    maplist.put("b", list);
	    maplist.put("c", list);
	    String maplistl = MyJsonUtils.toJson(maplist);
	    System.out.println(maplistl);
	    
	    
	    System.out.println("------------list map--------------------");
		// 列表/array 数据
		String str1 = "[{'id': '1','code': 'bj','name': '北京','map': '39.90403, 116.40752599999996'}, {'id': '2','code': 'sz','name': '深圳','map': '22.543099, 114.05786799999998'}, {'id': '9','code': 'sh','name': '上海','map': '31.230393,121.473704'}, {'id': '10','code': 'gz','name': '广州','map': '23.129163,113.26443500000005'}]";

		Gson gson1 = new Gson();

		List<City> rs = new ArrayList<City>();

		Type type1 = new TypeToken<ArrayList<City>>(){}.getType();
		rs = gson1.fromJson(str1, type1);
		System.out.println(rs);
		
//		rs = JsonUtils.jsonToList(str1, City.class);
		
		System.out.println("&&&&&&&&&&&&&");
		System.out.println(rs);
		
		
		rs = MyJsonUtils.jsonToList(str1, City.class);
//		System.out.println(rs.get(0));
		
		
		for (City o : rs) {
			System.out.println("******");
			System.out.println(o.name);
			System.out.println("******");
		}
		System.out.println("&&&&&&&&&&&&&");
		// map数据

		String jsonStr = "{'1': {'id': '1','code': 'bj','name': '北京','map': '39.90403, 116.40752599999996'},'2': {'id': '2','code': 'sz','name': '深圳','map': '22.543099, 114.05786799999998'},'9': {'id': '9','code': 'sh','name': '上海','map': '31.230393,121.473704'},'10': {'id': '10','code': 'gz','name': '广州','map': '23.129163,113.26443500000005'}}";
		Map<String, City> citys = gson.fromJson(jsonStr, new TypeToken<Map<String, City>>() {
		}.getType());
		
		
//		Map<String, City> citys = JsonUtils.jsonToMap(jsonStr, City.class);
		
//		Map<String, String> citys = JsonUtils.fromJson(jsonStr, Map.class);
		
		//System.out.println(citys.get("1").name + "----------" + citys.get("2").code);
		System.out.println(citys);
		
		citys = MyJsonUtils.jsonToMap(jsonStr, City.class);
		System.out.println(citys);
		System.out.println("*********************************************************");
		System.out.println(citys.get("1").name + "----------" + citys.get("2").code);
		System.out.println("*********************************************************");
		
		
		Map<String, String> stringCity = MyJsonUtils.fromJson(jsonStr, Map.class);
		
		System.out.println("----------");
		System.out.println("*********************************************************");
		System.out.println(stringCity);
		System.out.println("*********************************************************");
		
		System.out.println("-------------json From file--------------------");
		JsonBean js = null;
//		try {
//			js = gson1.fromJson(new FileReader("src/test/resources/json.txt"), new TypeToken<JsonBean>() {}.getType());
//		} catch (JsonIOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonSyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			js = MyJsonUtils.fromJson(new FileReader("src/test/resources/json.txt"), JsonBean.class);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(js);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String format = df.format(new Date());
		System.out.println(format);// new Date()为获取当前系统时间
		
		System.out.println(MyJsonUtils.toJson(format));
		
		Gson gsonF = new GsonBuilder().serializeNulls()
		        // 设置日期时间格式，另有2个重载方法
		        // 在序列化和反序化时均生效
		        .setDateFormat("yyyy-MM-dd").create();
		System.out.println(gsonF.toJson(new Date()));
		
		System.out.println("**************img to json ***************************");
		
//		String imgStr = ImageHandle.getImageStr("src/test/resources/testpag.jpg");
//		String imgJson = JsonUtils.toJson(imgStr);
//		System.out.println(imgJson);
//		
//		System.out.println("**************img to json***************************");
//		
//		System.out.println("**************json to img ***************************");
//		
//		String imgStrFromJson = JsonUtils.fromJson(imgJson, String.class);
//		
//		Boolean b = ImageHandle.generateImage(imgStrFromJson, "src/test/resources/testpag1.jpg");
//		System.out.println(b);
		
		System.out.println("**************json to img***************************");
		
//		String abcJson = JsonUtils.toJson("abc");
//		System.out.println(abcJson);
//		
//		//json "abc" string abc
//		String adbStr = "abc";
//		System.out.println(adbStr);
//		
//		String imgJson2 = JsonUtils.imgToJson("src/test/resources/testpag.jpg");
		
		System.out.println("**************json to img***************************");
//		JsonUtils.jsonToImg(imgJson2, "src/test/resources/testpag3.jpg");
		System.out.println("**************json to img***************************");
	}
	    
}

class City{  
	  
    int id;  

    String name;  

    String code;  

    String map;  
}       
   

class JsonBean {
	public String a;
	public List<B> b;
	public C c;

	public static class B {

		public String b1;

		public String b2;

		public String getB1() {
			return b1;
		}

		public void setB1(String b1) {
			this.b1 = b1;
		}

		public String getB2() {
			return b2;
		}

		public void setB2(String b2) {
			this.b2 = b2;
		}

		@Override
		public String toString() {
			return "B [b1=" + b1 + ", b2=" + b2 + "]";
		}
		
	}

	public static class C {
		public String c1;
		public String c2;
		public String getC1() {
			return c1;
		}
		public void setC1(String c1) {
			this.c1 = c1;
		}
		public String getC2() {
			return c2;
		}
		public void setC2(String c2) {
			this.c2 = c2;
		}
		@Override
		public String toString() {
			return "C [c1=" + c1 + ", c2=" + c2 + "]";
		}
		
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public List<B> getB() {
		return b;
	}

	public void setB(List<B> b) {
		this.b = b;
	}

	public C getC() {
		return c;
	}

	public void setC(C c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return "JsonBean [a=" + a + ", b=" + b + ", c=" + c + "]";
	}
	
}

class Student {  
	
    public int id;  
    public String nickName;  
    public int age;  
    public String email;
    public ArrayList<String> books;  
    public HashMap<String, String> booksMap;
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public ArrayList<String> getBooks() {
		return books;
	}
	public void setBooks(ArrayList<String> books) {
		this.books = books;
	}
	public HashMap<String, String> getBooksMap() {
		return booksMap;
	}
	public void setBooksMap(HashMap<String, String> booksMap) {
		this.booksMap = booksMap;
	}
	@Override
	public String toString() {
		return "Student [id=" + id + ", nickName=" + nickName + ", age=" + age + ", email=" + email + ", books=" + books
				+ ", booksMap=" + booksMap + "]";
	}  
	
}
