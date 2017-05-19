package com.opendanmaku.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class DanmakuClient extends Thread {
	static final int MAX_THREADS = 5000;  
	private int id = 0;  
	private static int threadCount = 0;  
	private Socket s;  
	private BufferedReader in;  

	public static int getThreadCount(){  
		return threadCount;  
	}  

	public DanmakuClient(InetAddress ia){  
		id = threadCount++;  
		System.out.println("DanmakuClient: " + id);
		try{  
			s = new Socket(ia, 7890);  
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));  
			start();  
		}catch(Exception e1){ 
			e1.printStackTrace();
			try{  
				s.close();  
			}catch(Exception e2){  
				System.out.println("Error in Client\n");  
			}  
		}  

	}  

	public void run() {
		try{  
			String str; 
			for(;;) {  
				str=in.readLine();  
				System.out.println(System.currentTimeMillis() + " Server(" + id + ") reply: " + str);  
			}  
		}catch(Exception e){
			e.printStackTrace();
		}finally{  
			try {  
				s.close();  
			} catch (Exception e){}  
		}  
	}  

	public static void main(String args[]) throws Exception {  
		//InetAddress ia = InetAddress.getByName("47.90.59.0");
		InetAddress ia = InetAddress.getByName("115.29.100.203");
		//InetAddress ia = InetAddress.getByName(null);
		while(true){
			if(getThreadCount() < MAX_THREADS)  
				new DanmakuClient(ia);
			else 
				break;  
			Thread.currentThread().sleep(10);  
		}
		
		for (;;) {
			
			Thread.currentThread().sleep(10000);
			System.out.println("### idle ###");
		}
	}
	
}
