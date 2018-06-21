package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client01 {
	
	private int blockSize=4096;
	private  ByteBuffer receiveBuffer =ByteBuffer.allocate(blockSize);
	private ByteBuffer sendBuffer = ByteBuffer.allocate(blockSize);
	private SocketChannel client=null;
	
	private SocketChannel connecteServer(InetSocketAddress ipAndPort) {
		try {
			client=SocketChannel.open();
			boolean flag=client.connect(ipAndPort);
			if(flag) {
				System.out.println("client connection server success");
			}
		} catch (IOException e) {
			System.out.println("open socketChannel exception");
			e.printStackTrace();
		}
		return client;
	}
	
	private void sendDataToServer(String sendText) {
		if(null==sendText || sendText.isEmpty()) {
			System.out.println("data is null please check");
			return;
		}
		sendBuffer.clear();
		sendBuffer.put(sendText.getBytes());
		sendBuffer.rewind();
		try {
			client.write(sendBuffer);
			sendBuffer.clear();
			System.out.println("*******send data success*************");
		} catch (IOException e) {
			System.out.println("send massge exception");
			e.printStackTrace();
		}
	}
	
	private String receiveDataFromServer() {
		receiveBuffer.clear();
		String receiveText="";
		try {
			int count=client.read(receiveBuffer);
			receiveBuffer.rewind();
			if(count>0) {
				receiveText = new String(receiveBuffer.array(),0,count);
			}
		} catch (IOException e) {
			System.out.println("receive data from server exception");
			e.printStackTrace();
		}
		return receiveText;
	}
	
	private String dialogWithServer(InetSocketAddress ipAndPort,String str) {
		if(str==null || str.isEmpty()) {
			return "";
		}
		connecteServer(ipAndPort);
		sendDataToServer(str);
		return receiveDataFromServer();
	}
	
	public static void main(String[] args) {
		Client01 client01=new Client01();
		InetSocketAddress ipAndPort=new InetSocketAddress("127.0.0.1", 8889);
		String str = "I am on the moon";
		String info=client01.dialogWithServer(ipAndPort, str);
		System.out.println(info);
	}

}
