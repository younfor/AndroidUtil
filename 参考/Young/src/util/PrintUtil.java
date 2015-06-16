package util;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;


public class PrintUtil {
	public  static final PrintStream systemOut=System.out;
	public  static void  printToLog(String logName,boolean isDeleteExists) {
		File file = new File("/home/young/huawei/player_log/"+logName+".txt");
		if(isDeleteExists&&file.exists()){
			file.delete();
		}
		FileOutputStream fileOutputStream=null;
		try {
			fileOutputStream = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream ps = new PrintStream(fileOutputStream);
		System.setOut(ps);
//		System.out.println("\n******************" + new Date() + "***************\n");
	}
	
	
	public static void printToConsole(PrintStream out){
		System.out.println("\n*****************************end*****************************\n");
		System.setOut(out);
		System.out.println("To read the Log.txt");
	}
}
