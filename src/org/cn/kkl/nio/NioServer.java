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
	
	public NioServer() throws IOException {
		//Open server channel
		ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);//set non-block
		ServerSocket serverSocket=serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress("127.0.0.1",8889));//bind port
		
		this.selector = Selector.open();//open selector
		
		serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
		System.out.println("server start-->"+8889);
	}
	
	public void listen() throws IOException {
		while(true) {
			int keys=this.selector.select();//traverse selector
			if(keys<=0) {
				System.out.println("no channel registered to selector");
				break;
			}
			Set<SelectionKey> selectionKeys=this.selector.selectedKeys();
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
		String receiveText;
		String sendText;
		
		if(selectionKey.isAcceptable()) {
			ServerSocketChannel serverChannel=(ServerSocketChannel) selectionKey.channel();
			SocketChannel channel=serverChannel.accept();
			channel.configureBlocking(false);
			channel.register(this.selector, SelectionKey.OP_READ);
		}
		
		//read operation
		else if(selectionKey.isReadable()) {
			SocketChannel channel=(SocketChannel) selectionKey.channel();
			receiveBuffer.clear();
			int count=channel.read(receiveBuffer);
			receiveBuffer.rewind();
			if(count>0) {
				receiveText=new String(receiveBuffer.array(), 0, count);
				System.out.println("server receive client date :"+receiveText);
			}
			channel.register(this.selector, SelectionKey.OP_WRITE);
		}
		
		//write operation 
		else if(selectionKey.isWritable()) {
			 sendBuffer.clear();
			 SocketChannel channel=(SocketChannel) selectionKey.channel();
			 sendText="msg send to client"+flag++;
			 sendBuffer.put(sendText.getBytes());
			 sendBuffer.rewind();
			 channel.write(sendBuffer);
			 System.out.println("server send data"+sendText+" to clent success");
		}
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		NioServer server=new NioServer();
		
		Thread.sleep(10000);
		server.listen();
	}

}
