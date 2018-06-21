package org.cn.kkl.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

public class AIOClient {
	
	private AsynchronousSocketChannel client=null;
	
	public AIOClient(String host,int port) {
		try {
			client=AsynchronousSocketChannel.open();
			Future<?> future=client.connect(new InetSocketAddress(host, port));
			System.out.println(future);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	private void write(byte b) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		byteBuffer.put(b);
		byteBuffer.flip();
		client.write(byteBuffer);
	}
	
	public static void main(String[] args) {
		AIOClient aioClient= new AIOClient("127.0.0.1",8889);
		aioClient.write((byte)11);
		
		Buffer buffer;
	}

}
