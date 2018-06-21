package org.cn.kkl.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AIOServer {
	
	public AIOServer(int port) throws IOException{
		AsynchronousServerSocketChannel listener=AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
		listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel asc, Void attachment) {
				listener.accept(null,this);
				Handler(asc);
			}


			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println("asynchronous io failed");
			}
		});
	}
	
	/**
	 * @param asc
	 * @throws Exception 
	 */
	private void Handler(AsynchronousSocketChannel asc) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(32);
		try {
			asc.read(byteBuffer).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		byteBuffer.flip();
		System.out.println("server receive data :"+byteBuffer.get());
		
	}
	
	public static void main(String[] args) {
		int port=8889;
		try {
			new AIOServer(port);
			System.out.println("server monitor port "+port);
			Thread.sleep(10000);//in order to client connection server
		} catch (IOException e) {
			System.out.println("server boot exception");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
