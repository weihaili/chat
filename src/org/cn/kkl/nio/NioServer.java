package org.cn.kkl.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
	private int flag=1;
	
	private int blockSize=4096;
	private ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
	private ByteBuffer receiveBuffer = ByteBuffer.allocate(blockSize);
	private Selector selector;
	
	public NioServer(int port) {
		try {
			ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);//set non-block
			ServerSocket serverSocket=serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(port));//bind port
			
			selector = Selector.open();//open selector
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			System.out.println("server start-->"+port);
			
		} catch (IOException e) {
			System.out.println("open serverSocketChannel exception");
			e.printStackTrace();
		}
	}
	
	public void listen() {
		while(true) {
			try {
				selector.select();
				Set<SelectionKey> selectionKeys=selector.selectedKeys();
				Iterator<SelectionKey> iterator=selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = iterator.next();
					iterator.remove();
					//process business logic
					if(selectionKey.isValid()) {
						handlekey(selectionKey);
					}
				}
			} catch (IOException e) {
				System.out.println("listen events exception");
				e.printStackTrace();
			}
		}
	}
	
	public void handlekey(SelectionKey selectionKey) {
		ServerSocketChannel server=null;
		SocketChannel client= null;
		String receiveText;
		String sendText;
		int count=0;
		
		if(selectionKey.isAcceptable()) {
			server=(ServerSocketChannel) selectionKey.channel();
			try {
				client=server.accept();
				client.configureBlocking(false);
				client.register(selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				System.out.println("get client chanel exception");
				e.printStackTrace();
			}
		}else if(selectionKey.isReadable()) {
			client=(SocketChannel) selectionKey.channel();
			try {
				count=client.read(receiveBuffer);
				if(count>0) {
					receiveText=new String(receiveBuffer.array(), 0, count);
					System.out.println("server receive client date :"+receiveText);
				}
				client.register(selector, SelectionKey.OP_WRITE);
			} catch (IOException e) {
				System.out.println("read client data exception");
				e.printStackTrace();
			}
		}else if(selectionKey.isWritable()) {
			 sendBuffer.clear();
			 client=(SocketChannel) selectionKey.channel();
			 sendText="msg send to client"+flag++;
			 sendBuffer.put(sendText.getBytes());
			 sendBuffer.flip();
			 try {
				client.write(sendBuffer);
				System.out.println("server send data"+sendText+" to clent success");
			} catch (IOException e) {
				System.out.println("server send client data exception");
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
		int port=8889;
		NioServer nioServer=new NioServer(port);
		nioServer.listen();
	}

}
