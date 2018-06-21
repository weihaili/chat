package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SocketChannel;

public class Client {
	private ByteBuffer buff= ByteBuffer.allocate(1024);
	private IntBuffer intBuffer=buff.asIntBuffer();
	private SocketChannel channel=null;
	
	private SocketChannel connecte() {
		try {
			channel=SocketChannel.open();
			boolean flag=channel.connect(new InetSocketAddress("127.0.0.1", 8888));
			System.out.println(flag);
		} catch (IOException e) {
			System.out.println("connection server exception");
			e.printStackTrace();
		}
		return channel;
	}
	
	/**
	 * send request to server
	 * @param a
	 * @param b
	 */
	private void sendRequest(int a,int b) {
		buff.clear();
		intBuffer.put(0, a);
		intBuffer.put(1, b);
		try {
			channel.write(buff);
		} catch (IOException e) {
			System.out.println("client write data exception");
			e.printStackTrace();
		}
		System.out.println("client send data "+a+"+"+b);
	}
	
	/**
	 * receive server send data
	 * @return
	 */
	private int receiveResult() {
		buff.clear();
		try {
			channel.read(buff);
		} catch (IOException e) {
			System.out.println("client receive server data exception");
			e.printStackTrace();
		}
		return intBuffer.get(0);
	}
	
	private int getSum(int a,int b) {
		int result=0;
		channel=connecte();
		sendRequest(a, b);
		result=receiveResult();
		return result;
	}
	
	public static void main(String[] args) {
		int result=new Client().getSum(1, 1);
		System.out.println(result);
	}
}
