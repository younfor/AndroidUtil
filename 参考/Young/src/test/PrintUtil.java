package test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;


public class PrintUtil {
	public static final PrintStream systemOut=System.out;
	public static void printToLog(PrintStream out,String playID) {
		File file = new File("/mnt/shared/player_log/log"+playID+".txt");
		if(file.exists()){
			file.delete();
		}
		FileOutputStream fileWriter=null;
		try {
			fileWriter = new FileOutputStream(file, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream ps = new PrintStream(fileWriter);
		System.setOut(ps);
		System.out.println("\n******************" + new Date() + "***************\n");
	}
	public static void printToConsole(PrintStream out){
		System.out.println("\n*****************************end*****************************\n");
		System.setOut(out);
		System.out.println("To read the Log.txt");
	}
}
