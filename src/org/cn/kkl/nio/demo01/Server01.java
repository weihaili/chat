package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server01 {
	private int blockSize=4096;
	private ByteBuffer receiveBuffer=ByteBuffer.allocate(blockSize);
	private ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
	private ServerSocketChannel serverChannel=null;
	private SocketChannel clientChannel=null;
	
	/**
	 * @param port
	 * 1. open serverSocketChannel
	 * 2. get serverSocket and bind inetSocketAdress
	 */
	private void openServer(int port) {
		try {
			serverChannel=ServerSocketChannel.open();
			serverChannel.socket().bind(new InetSocketAddress(port));
			System.out.println("server already open at localhost in -->"+port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *wait client connection
	 *deal with client request 
	 */
	private void waitConnectionAndDealRequest() {
		while(true) {
			try {
				clientChannel=serverChannel.accept();
				if(null!=clientChannel) {
					System.out.println("client connection server success");
				}
				processRequest();
				
				clientChannel.close();
				
			} catch (IOException e) {
				System.out.println("get clientChannel exception");
				e.printStackTrace();
			}
		}
	}

	/**
	 *process client request 
	 */
	private void processRequest() {
		receiveBuffer.clear();
		try {
			int count = clientChannel.read(receiveBuffer);
			//receiveBuffer.flip();
			receiveBuffer.rewind();
			String receiveText=new String(receiveBuffer.array(),0,count);
			System.out.println("server receive client data is : ");
			System.out.println(receiveText);
			receiveBuffer.clear();
			
			responseClient();
			System.out.println("server process client data complish");
		} catch (IOException e) {
			System.out.println("server receive client data exception");
			e.printStackTrace();
		}
	}
	
	private void responseClient() {
		String sendText="hello client,server001 receive your data and you must part that location right now";
		sendBuffer.put(sendText.getBytes());
		sendBuffer.rewind();
		try {
			clientChannel.write(sendBuffer);
		} catch (IOException e) {
			System.out.println("response client exception");
			e.printStackTrace();
		}
	}
	
	private void start(int port) {
		openServer(port);
		waitConnectionAndDealRequest();
		try {
			clientChannel.close();
		} catch (IOException e) {
			System.out.println("close clientChannel exception"+clientChannel);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Server01().start(8889);;
	}

}
