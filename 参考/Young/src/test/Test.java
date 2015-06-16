package test;

import java.io.UnsupportedEncodingException;

public class Test {
	public static void main(String args[]) {
//		Party[] party=new Party[1024*1024*64];
//		System.out.println(party);
//		System.out.println(0x1FC0 + 1);
//		System.out.println(0x10);
//		System.out.println(8<<24);
//		System.out.println(1024*1024*16*8+1024*1024/2+1024*1024/4+1024*1024/8);
//				for(int i=0;i<50;i++){
//					System.out.println("hello linux");
//				}
//			}
		String s="all_in \n";
		String b="fold \n";
		try {
			System.out.println(s.getBytes("ASCII"));
			System.out.println(b.getBytes("ASCII"));
			
			System.out.println((s+b).getBytes("ASCII"));
			System.out.println((b+b).getBytes("ASCII"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(s.getBytes());
		System.out.println(b.getBytes());
	
	}
}

