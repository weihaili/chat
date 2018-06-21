package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectorServer {
	
	private Selector selector;
	private ServerSocketChannel serverChannel=null;
	private int keys=0;
	
	public static void main(String[] args) {
		new SelectorServer().start();
	}
	
	private void start() {
		initServer();
		listen();
	}

	/**
	 *  initialization server connection , channel,register events 
	 */
	private void initServer() {
		try {
			this.selector=Selector.open();
			serverChannel=ServerSocketChannel.open();
			serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8889));
			serverChannel.configureBlocking(false);
			SelectionKey key=serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void listen() {
		System.out.println("server already boot finish");
		while(true) {
			try {
				keys=this.selector.select();
				Iterator<SelectionKey> iterator=this.selector.selectedKeys().iterator();
				if(keys>0) {
					//start polling
					while(iterator.hasNext()) {
						SelectionKey selectionKey=iterator.next();
						iterator.remove();
						if(selectionKey.isAcceptable()) {
							serverChannel=(ServerSocketChannel) selectionKey.channel();
							SocketChannel channel=serverChannel.accept();
							channel.configureBlocking(false);
							//send data to client
							channel.write(ByteBuffer.wrap(new String("hello client a").getBytes()));
							channel.register(this.selector, SelectionKey.OP_READ);
						}
						
						//read operation on channel
						else if(selectionKey.isReadable()) {
							read(selectionKey);
						}
					}
				}else {
					System.out.println("select finished without any keys.");
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * read client send data
	 * @param selectionKey
	 */
	private void read(SelectionKey selectionKey) {
		SocketChannel channel=(SocketChannel) selectionKey.channel();
		ByteBuffer buffer= ByteBuffer.allocate(1024);
		try {
			int len=channel.read(buffer);
			System.out.println("server receive data is :"+new String(buffer.array(),0,len));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
