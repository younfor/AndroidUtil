package util;

import java.util.HashMap;

public class RuntimeEval {
	public static  HashMap<Integer,Integer>largeWin=new  HashMap<Integer,Integer>();
	public static  HashMap<Integer,Integer>largeLost=new  HashMap<Integer,Integer>();
	
	
	
	public static void gameOver(){
		System.out.println("largeWin:"+largeWin);
		System.out.println("largeLost:"+largeLost);
	}
}
