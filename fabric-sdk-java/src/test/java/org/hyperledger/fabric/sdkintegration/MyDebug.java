package org.hyperledger.fabric.sdkintegration;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class MyDebug {

	
	public static void printProperties(String str, Properties p) {
		System.out.println("----" + str);
		if(p != null) {
   		    Enumeration<?> e = p.propertyNames();  
            while (e.hasMoreElements()) {  
                String key = (String) e.nextElement();  
                String value = p.getProperty(key);  
                System.out.println(key + "~~~~" + value);  
            }  
   	    } else {
   		    System.out.println("----Properties is null");
   	    }
	}
	
	
	public static void printMap(String str, Map<String, String> m) {
		System.out.println("----" + str);
		System.out.println(m);
	}
	
	public static void printSet(String str, Set<String> set) {
		System.out.println("----" + str);
		System.out.println(set);
	}
	
	public static void print(String str) {
		System.out.println("*****************");
		System.out.println(str);
		System.out.println("*****************");
	}
	
	
}
