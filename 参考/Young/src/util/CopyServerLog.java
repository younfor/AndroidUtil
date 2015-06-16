package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
/*
 * 可单独执行此类，将服务端日志copy过来，
 */
public class CopyServerLog {
	public static void  main(String []args) throws IOException, InterruptedException{
		copyServerLog();
		copyResultData();
		copyReplayLog();
	}
	public static void copyLog(){
		try {
			copyServerLog();
			copyResultData();
			copyReplayLog();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		
	}
	public static void copyServerLog() throws IOException, InterruptedException {
		File file = new File("/home/game/run_area/server/log.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileInputStream));
		File toFile = new File("/mnt/shared/server_log/logCopy.txt");
		if(toFile.exists()){
			toFile.delete();
		}
		FileOutputStream outputStream=new FileOutputStream(toFile);
		PrintStream out= new PrintStream (outputStream,true); 
		String s;
		while((s=reader.readLine())!=null){
			out.println(s);
		}
	
			
		reader.close();
		out.close();
	}
	public static void copyResultData() throws IOException, InterruptedException {
		File file = new File("/home/game/run_area/server/data.csv");
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileInputStream));
		File toFile = new File("/mnt/shared/server_log/dataCopy.csv");
		if(toFile.exists()){
			toFile.delete();
		}
		FileOutputStream outputStream=new FileOutputStream(toFile);
		PrintStream out= new PrintStream (outputStream,true);  
		String s;
		while((s=reader.readLine())!=null){
			out.println(s);
		}
		reader.close();
		out.close();
	}
	public static void copyReplayLog() throws IOException, InterruptedException {
		File file = new File("/home/game/run_area/server/replay.txt");
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				fileInputStream));
		File toFile = new File("/mnt/shared/ui/replay.txt");
		if(toFile.exists()){
			toFile.delete();
		}
		FileOutputStream outputStream=new FileOutputStream(toFile);
		PrintStream out= new PrintStream (outputStream,true);  
		String s;
		while((s=reader.readLine())!=null){
			out.println(s);
		}
		reader.close();
		out.close();
	}
}
