package org.cn.kkl.nio.demo01;

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

public class NIOServer {

	private int flag = 1;
	private int blockSize = 4096;
	private ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);// 发送数据缓冲区
	private ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);// 接收数据缓冲区
	private Selector selector;// 选择器
	
	private int keys=0;

	public NIOServer() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		// 设置是否组阻塞
		serverSocketChannel.configureBlocking(false);
		// 创建客户端和服务端的socket.socket网络套接字，用来向网络发送请求，或者应答请求。
		ServerSocket serverSocket = serverSocketChannel.socket();
		// 绑定socket地址，IP端口
		serverSocket.bind(new InetSocketAddress("127.0.0.1",8889));
		// 打开筛选器
		this.selector = Selector.open();

		// 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作，返回key
		serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		System.out.println("Server start ->" + 8889);
	}

	// NIOServer的监听事件
	public void listen() throws IOException {
		while (true) {
			keys=this.selector.select();
			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				// 负责多线程并发的安全的key
				SelectionKey selectionKey = iterator.next();
				iterator.remove();
				// 业务逻辑
				handleKey(selectionKey);
			}
		}
	}

	// 业务逻辑
	public void handleKey(SelectionKey selectionKey) throws IOException {
		// 服务端监听通道
		ServerSocketChannel server = null;
		SocketChannel client = null;
		String reciveText;
		String sendText;
		int count = 0;
		if (selectionKey.isAcceptable()) {
			// 服务端接收客户端信息
			server = (ServerSocketChannel) selectionKey.channel();
			client = server.accept();
			client.configureBlocking(false);
			client.register(selector, SelectionKey.OP_READ);
		} else if (selectionKey.isReadable()) {
			// 服务端读取客户端信息
			client = (SocketChannel) selectionKey.channel();
			count = client.read(receivebuffer);
			if (count > 0) {
				reciveText = new String(receivebuffer.array(), 0, count);
				System.out.println("服务端接收到客户端的信息：" + reciveText);
				client.register(selector, selectionKey.OP_WRITE);
			}
		} else if (selectionKey.isWritable()) {
			// 服务端发送数据给客户端
			sendbuffer.clear();
			client = (SocketChannel) selectionKey.channel();
			sendText = "mag send to client:" + flag++;
			sendbuffer.put(sendText.getBytes());
			sendbuffer.flip();
			client.write(sendbuffer);
			System.out.println("服务端发送数据给客户端：" + sendText);

		}

	}

	public static void main(String[] args) throws IOException {
		new NIOServer().listen();
	}

}
