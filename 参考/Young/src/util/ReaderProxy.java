package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ReaderProxy extends BufferedReader {

	public ReaderProxy(Reader in,String playerId) {
		super(in);
		PrintUtil.printToLog(playerId, true);
	}
	
	public String readLine() throws IOException  {
//		String msg="err";
//		try {
//			msg = super.readLine();
//			System.out.println(msg);
//			return msg;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace(System.out);
//			readLine();
//		}
//		return msg;
		String msg="err";
		msg = super.readLine();
		System.out.println(msg);
		return msg;
	}



}
