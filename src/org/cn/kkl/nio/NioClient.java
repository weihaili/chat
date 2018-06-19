package org.cn.kkl.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {
	private int flag=0;
	private int blockSize=4096;
	private ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
	
	private final static InetSocketAddress SERVER_ADDRESS=new InetSocketAddress("localhost", 8889);
	
	public static void main(String[] args) {
		try {
			SocketChannel socketChannel=SocketChannel.open();
			socketChannel.configureBlocking(false);
			Selector selector=Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(SERVER_ADDRESS);
			
			Set<SelectionKey> selectionKey;
			Iterator<SelectionKey> iterator;
		} catch (IOException e) {
			System.out.println("client socketChannel open exception");
			e.printStackTrace();
		}
	}
}
