package test;

import java.util.HashMap;

public class hashmapTest {
	public static void main(String []s){
	
		HashMap <String,Integer> map=new HashMap <String,Integer>();
		map.put("key", 5);
		int a=map.get("key")+1;
		System.out.println(map);
}}
