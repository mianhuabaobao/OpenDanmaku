package com.opendanmaku.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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
				//str=in.readLine();  
				//System.out.println(System.currentTimeMillis() + " Server(" + id + ") reply: " + str);  
				System.out.println( "Server(" + id + ") reply: " + in.read());  
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
		boolean testRedis = false;
		
		if (testRedis) {
			
			String redisIp = "47.90.59.0";
			int reidsPort = 63799;
			JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisIp, reidsPort);
			System.out.println(String.format("redis pool is starting, redis ip %s, redis port %d", redisIp, reidsPort));
		        
		        
			Jedis jedis = jedisPool.getResource(); //new Jedis("47.90.59.0", 63799);
			//jedis.set("hello".getBytes("UTF-8"),"my world!".getBytes("UTF-8"));
			jedis.set("hello".getBytes("UTF-8"), new byte[]{'a','o'});
			
			byte[] channel = "helloworld".getBytes("UTF-8");
			
			System.out.println("hello >>> " + jedis.get("hello"));
			
			jedis.publish(channel, new byte[]{'a','o'});

			BinaryJedisPubSub jedisPubSub = new BinaryJedisPubSub () {
				@Override  
			    public void onMessage(byte[] channel, byte[] message) {
					System.out.println("onMessage >>> " + new String(message));
				}
			};
			
			jedis.subscribe(jedisPubSub, channel);
			
			System.out.println("disconnect >>> ");

			jedis.disconnect();
			return;
		}
		
		InetAddress ia = InetAddress.getByName("47.90.59.0");
		//InetAddress ia = InetAddress.getByName("115.29.100.203");
		//InetAddress ia = InetAddress.getByName(null);
		while(true){
			if(getThreadCount() < MAX_THREADS)  
				new DanmakuClient(ia);
			else 
				break;  
			Thread.sleep(10);  
		}
		
		for (;;) {
			Thread.sleep(10000);
			System.out.println("### idle ###");
		}
	}
	
}
