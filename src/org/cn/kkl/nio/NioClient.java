package org.cn.kkl.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NioClient {
	private static int flag=1;
	private static int blockSize=4096;
	private static ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
	private static ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
	
	private final static InetSocketAddress SERVER_ADDRESS=new InetSocketAddress("127.0.0.1", 8888);
	
	public static void main(String[] args) throws IOException {
		SocketChannel socketChannel=SocketChannel.open();
		socketChannel.configureBlocking(false);
		Selector selector=Selector.open();
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(SERVER_ADDRESS);
		System.out.println(socketChannel.isConnected());
		
		Set<SelectionKey> selectionKeys;
		Iterator<SelectionKey> iterator;
		SelectionKey selectionKey;
		SocketChannel client;
		int count=0;
		
		String receiveText;
		String sendText;
		
		while(true) {
			selectionKeys=selector.selectedKeys();
			System.out.println("client :"+Arrays.toString(selectionKeys.toArray()));
			iterator=selectionKeys.iterator();
			while (iterator.hasNext()) {
				selectionKey = iterator.next();
				
				if (selectionKey.isConnectable()) {
					System.out.println("client try to connecte");
					client=(SocketChannel) selectionKey.channel();
					if(client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("client connected complish");
						sendBuffer.clear();
						
						sendBuffer.put("hello server".getBytes());
						sendBuffer.flip();
						client.write(sendBuffer);
					}
					client.register(selector, SelectionKey.OP_READ);
				}else if(selectionKey.isReadable()) {
					client=(SocketChannel) selectionKey.channel();
					receiveBuffer.clear();
					count=client.read(receiveBuffer);
					if(count>0) {
						receiveText=new String(receiveBuffer.array(),0,count);
						System.out.println("client receive server data is: "+receiveText);
					}
					client.register(selector, SelectionKey.OP_WRITE);
					
				}else if(selectionKey.isWritable()) {
					sendBuffer.clear();
					client=(SocketChannel) selectionKey.channel();
					sendText="msg send to server -> "+flag++;
					sendBuffer.put(sendText.getBytes());
					sendBuffer.flip();
					client.write(sendBuffer);
					System.out.println("client send date to server :"+sendText);
					client.register(selector, SelectionKey.OP_READ);
					
				}
			}
			selectionKeys.clear();
		}
	}
	
}
