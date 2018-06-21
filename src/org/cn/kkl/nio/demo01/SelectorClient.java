package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorClient {
	
	private Selector selector;
	private ByteBuffer outBuffer=ByteBuffer.allocate(1024);
	private ByteBuffer inBuffer=ByteBuffer.allocate(1024);
	private int keys=0;
	
	private SocketChannel channel=null;
	
	public void initClient() {
		try {
			channel=SocketChannel.open();
			selector=Selector.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress("127.0.0.1", 8889));
			channel.register(this.selector, SelectionKey.OP_CONNECT);
		} catch (IOException e) {
			System.out.println("client channel open exception");
			e.printStackTrace();
		}
	}
	
	/**
	 * monitor channel events
	 */
	private void listen() {
		while(true) {
			try {
				keys=this.selector.select();
				if(keys>0) {
					Iterator<SelectionKey> iterator=this.selector.selectedKeys().iterator();
					while (iterator.hasNext()) {
						SelectionKey selectionKey =  iterator.next();
						if(selectionKey.isConnectable()){
							SocketChannel channel=(SocketChannel) selectionKey.channel();
							if(channel.isConnectionPending()) {
								channel.finishConnect();
								System.out.println("client connecte server finish");
							}
							channel.register(this.selector, SelectionKey.OP_WRITE);
						}
						
						//write operation on channel
						
						else if(selectionKey.isWritable()) {
							SocketChannel channel=(SocketChannel) selectionKey.channel();
							outBuffer.clear();
							System.out.println("client is reading writing data......");
							channel.write(outBuffer.wrap("I am clientA hello serverA".getBytes()));
							channel.register(this.selector, SelectionKey.OP_READ);
							System.out.println("client send data finish");
						}
						
						//read operation on channel
						else if(selectionKey.isReadable()) {
							SocketChannel channel=(SocketChannel) selectionKey.channel();
							inBuffer.clear();
							System.out.println("client start received data......");
							int count=channel.read(inBuffer);
							System.out.println("data==>"+new String(inBuffer.array(),0,count));
							System.out.println("client receive data finish!!!!");
						}
						
					}
				}else {
					System.out.println("selector finished without any keys.");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void start() {
		initClient();
		listen();
	}
	
	public static void main(String[] args) {
		new SelectorClient().start();
	}

}
