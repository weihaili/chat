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

	// Socket地址：ip+端口
	private final static InetSocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 7080);

	public static void main(String[] args) throws IOException {
		// 打开通道
		SocketChannel socketChannel = SocketChannel.open();
		// 通道设置成非阻塞模式
		socketChannel.configureBlocking(false);
		// 打开筛选器
		Selector selector = Selector.open();
		// 注册选择器
		socketChannel.register(selector, SelectionKey.OP_CONNECT);
		socketChannel.connect(serverAddress);
		System.out.println(socketChannel.isConnected());

		Set<SelectionKey> selectionKeys;
		Iterator<SelectionKey> iterator;
		SelectionKey selectionKey;
		SocketChannel client;
		String receiveTest;
		String sendText;
		int count = 0;
		while (true) {
			selectionKeys = selector.selectedKeys();
			iterator = selectionKeys.iterator();
			while (iterator.hasNext()) {
				selectionKey = iterator.next();
				if (selectionKey.isConnectable()) {
					System.out.println("client connect");
					client = (SocketChannel) selectionKey.channel();
					if (client.isConnectionPending()) {
						client.finishConnect();
						System.out.println("客户端完成连接操作！");
						sendbuffer.clear();
						sendbuffer.put("Hello,Server".getBytes());
						sendbuffer.flip();
						client.write(sendbuffer);
					}
					client.register(selector, SelectionKey.OP_READ);

				}
				if (selectionKey.isReadable()) {
					client = (SocketChannel) selectionKey.channel();
					receivebuffer.clear();
					count = client.read(receivebuffer);
					if (count > 0) {
						receiveTest = new String(receivebuffer.array(), 0, count);
						System.out.println("客户端接收到服务端的数据:" + receiveTest);
						client.register(selector, SelectionKey.OP_WRITE);

					}
				}
				if (selectionKey.isWritable()) {
					sendbuffer.clear();
					client = (SocketChannel) selectionKey.channel();
					sendText = "Msg from client--->" + flag++;
					sendbuffer.put(sendText.getBytes());
					sendbuffer.flip();
					client.write(sendbuffer);
					System.out.println("客户端发送方数据给服务端：" + sendText);
					client.register(selector, SelectionKey.OP_READ);

				}
			}

			selectionKeys.clear();
		}
	}
}
