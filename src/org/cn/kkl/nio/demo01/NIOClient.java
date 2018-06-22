package org.cn.kkl.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOClient {
	private static int flag = 1;
	private static int blockSize = 4096;
	private static ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);// 发送数据缓冲区
	private static ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);// 接收数据缓冲区
	private static int keys=0;
	// Socket地址：ip+端口
	private static Selector selector=null;
	private static boolean isRunning=true;
	
	public static void main(String[] args) throws IOException {
		// 打开通道
		SocketChannel socketChannel = SocketChannel.open();
		// 通道设置成非阻塞模式
		socketChannel.configureBlocking(false);
		// 打开筛选器
		selector = Selector.open();
		// channel register selector
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(new InetSocketAddress("127.0.0.1", 8889));
		//socketChannel.socket().connect(new InetSocketAddress("127.0.0.1", 8889));
		System.out.println(socketChannel.isConnected());

		Set<SelectionKey> selectionKeys;
		String receiveTest;
		String sendText;
		while (isRunning) {
			keys=selector.select();
			if(keys<0) {
				isRunning=false;
				System.out.println("selector without any register channel");
			}
			selectionKeys=selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey selectionKey = iterator.next();
				if (selectionKey.isConnectable()) {
					System.out.println("client try to connect server.....");
					SocketChannel channel = (SocketChannel) selectionKey.channel();
					if (channel.isConnectionPending()) {
						channel.finishConnect();
						System.out.println("client accomplish connet with server！");
						sendbuffer.clear();
						sendbuffer.put("Hello,Server".getBytes());
						sendbuffer.rewind();
						channel.write(sendbuffer);
					}
					channel.register(selector, SelectionKey.OP_READ);
				}
				if (selectionKey.isReadable()) {
					SocketChannel channel = (SocketChannel) selectionKey.channel();
					receivebuffer.clear();
					int count = channel.read(receivebuffer);
					if (count > 0) {
						receiveTest = new String(receivebuffer.array(), 0, count);
						System.out.println("client receives server data :" + receiveTest);
					}
					channel.register(selector, SelectionKey.OP_WRITE);
				}
				if (selectionKey.isWritable()) {
					sendbuffer.clear();
					SocketChannel channel = (SocketChannel) selectionKey.channel();
					sendText = "Msg from client--->" + flag++;
					sendbuffer.put(sendText.getBytes());
					sendbuffer.rewind();
					channel.write(sendbuffer);
					System.out.println("client send server data ：" + sendText);
					channel.register(selector, SelectionKey.OP_READ);
				}
			}
			selectionKeys.clear();
		}
	}
}
