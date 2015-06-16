package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReplyEval {
	public static void  main(String []args) throws IOException, InterruptedException{
		File file=new File("F:/HuaWei/share/player_log/log1111.txt");
		FileInputStream fileInputStream=new FileInputStream(file);
		BufferedReader reader=new BufferedReader(new InputStreamReader(fileInputStream));
		while(true){
			if(reader.readLine()==null)
				Thread.sleep(500);
			System.out.println(reader.readLine());
			if(reader.readLine()=="party :500"){break;}
		}
	}
}
