package com.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.InputMap;

public class TcpClient {

	public static void main(String args[]) throws IOException
	{
		Socket socket=null;
		InputStream in=null;
		BufferedReader read=null;
		OutputStream out=null;
		//change must be!
		Game game=new Game(args[4],args[5]);
		try {
			socket=new Socket(args[0],Integer.parseInt(args[1]),InetAddress.getByName(args[2]),Integer.parseInt(args[3]));
			in=socket.getInputStream();
			read=new BufferedReader(new InputStreamReader(in));
			out=socket.getOutputStream();
			//register
			game.reg(out);
			//game round
			game.start(read,out);
			//finish
			socket.close();
		} catch (IOException e) {
			System.out.println("over");
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
			if(socket!=null)
				socket.close();
			e.printStackTrace();
		}finally{
			if(in!=null)
				in.close();
			if(out!=null)
				out.close();
			if(socket!=null)
				socket.close();
		}
	}
}
