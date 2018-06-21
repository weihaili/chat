package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
	
	private ByteBuffer buff=ByteBuffer.allocate(1024);
	private IntBuffer intBuff=buff.asIntBuffer();
	private SocketChannel clinetChannel=null;
	private ServerSocketChannel serverChannel=null;
	
	/**
	 * open server channel
	 */
	private void openChannel() {
		try {
			serverChannel=ServerSocketChannel.open();
			serverChannel.socket().bind(new InetSocketAddress(8888));
			System.out.println("server channel already open");
		} catch (IOException e) {
			System.out.println("open server channel exception");
			e.printStackTrace();
		}
	}
	
	private void waitRegConn() {
		while(true) {
			try {
				clinetChannel=serverChannel.accept();
				if(null!=clinetChannel) {
					System.out.println("new connection join in");
				}
				processReq();//deal with request
				clinetChannel.close();
				
			} catch (IOException e) {
				System.out.println("server waitRegConn exception");
				e.printStackTrace();
			}
		}
	}

	/**
	 * deal with client request
	 */
	private void processReq() {
		System.out.println("begin with reading and dealing client request data");
		//set byteBuffer position 0 and limit is capacity
		buff.clear();
		try {
			clinetChannel.read(buff);
			int result=intBuff.get(0)+intBuff.get(1);
			buff.flip();
			buff.clear();
			//update views original buffer date change with changes
			intBuff.put(0, result);
			clinetChannel.write(buff);
			System.out.println("read and deal client date complish");
		} catch (IOException e) {
			System.out.println("server read request data exception");
			e.printStackTrace();
		}
		
	}
	
	private void start() {
		openChannel();
		waitRegConn();
		try {
			clinetChannel.close();
			System.out.println("server deal success");
		} catch (IOException e) {
			System.out.println("server close exception");
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) {
		new Server().start();
	}
	

}
