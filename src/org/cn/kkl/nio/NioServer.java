package org.cn.kkl.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
	private int flag=1;
	
	private int blockSize=4096;
	private ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
	private Selector selector;
	
	public NioServer(int port) throws IOException {
		//Open server channel
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);//set non-block
		ServerSocket serverSocket=serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(port));//bind port
		
		selector = Selector.open();//open selector
		
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		System.out.println("server start-->"+port);
	}
	
	public void listen() throws IOException {
		while(true) {
			selector.select();//traverse selector
			Set<SelectionKey> selectionKeys=selector.selectedKeys();
			System.out.println(Arrays.toString(selectionKeys.toArray()));
			Iterator<SelectionKey> iterator=selectionKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				//process business logic
				handlekey(selectionKey);
			}
		}
	}
	
	public void handlekey(SelectionKey selectionKey) throws IOException {
		ServerSocketChannel server=null;
		SocketChannel client= null;
		String receiveText;
		String sendText;
		int count=0;
		
		if(selectionKey.isAcceptable()) {
			server=(ServerSocketChannel) selectionKey.channel();
			client=server.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
		}else if(selectionKey.isReadable()) {
			client=(SocketChannel) selectionKey.channel();
			count=client.read(receiveBuffer);
			if(count>0) {
				receiveText=new String(receiveBuffer.array(), 0, count);
				System.out.println("server receive client date :"+receiveText);
			}
			client.register(selector, SelectionKey.OP_WRITE);
		}else if(selectionKey.isWritable()) {
			 sendBuffer.clear();
			 client=(SocketChannel) selectionKey.channel();
			 sendText="msg send to client"+flag++;
			 sendBuffer.put(sendText.getBytes());
			 sendBuffer.flip();
			 client.write(sendBuffer);
			 System.out.println("server send data"+sendText+" to clent success");
		}
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		int port=8888;
		NioServer server=new NioServer(port);
		
		Thread.sleep(10000);
		server.listen();
	}

}
