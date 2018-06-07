package org.cn.kkl.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.cn.kkl.util.IoClose;

public class Server {
	private List<MyChannel> members = new ArrayList<MyChannel>();
	
	public static void main(String[] args) {
		new Server().start();
	}
	
	private void start() {
		try {
			ServerSocket server =new ServerSocket(8888);
			while (true) {
				Socket client=server.accept();
				MyChannel mc=new MyChannel(client);
				members.add(mc);
				new Thread(mc).start();
			}
		} catch (IOException e) {
			System.out.println("server port is occupancy");
			e.printStackTrace();
		}
	}
	
	private class MyChannel implements Runnable{
		private DataInputStream dis;
		private DataOutputStream dos;
		private boolean isRunning=true;
		
		public MyChannel() {}
		
		public MyChannel(Socket client) {
			try {
				dis=new DataInputStream(client.getInputStream());
				dos=new DataOutputStream(client.getOutputStream());
			} catch (IOException e) {
				System.out.println("server get stream exception");
				e.printStackTrace();
				IoClose.closeAll(dos,dis);
				members.remove(this);
				isRunning=false;
			}
		}
		
		private String getMsgFromClient() {
			try {
				return dis.readUTF();
			} catch (IOException e) {
				System.out.println("server getMsgFromClient is exception");
				e.printStackTrace();
				IoClose.closeAll(dis,dos);
				members.remove(this);
				isRunning=false;
			}
			return null;
		}
		
		private void sendMsgToClient(String msg) {
			try {
				if (msg.isEmpty()) {
					return;
				}else {
					dos.writeUTF(msg);
					dos.flush();
				}
			} catch (IOException e) {
				System.out.println("server sendMsgToClient is exception");
				e.printStackTrace();
				IoClose.closeAll(dis,dos);
				members.remove(this);
				isRunning=false;
			}
		}
		
		private void sendMsgToOtherClient() {
			String msg=getMsgFromClient();
			if(msg.isEmpty()) {
				return;
			}else{
				for (MyChannel mc : members) {
					if(mc==this) {
						continue;
					}else {
						mc.sendMsgToClient(msg);
					}
				}
			}
		}
		
		@Override
		public void run() {
			while (isRunning) {
				sendMsgToOtherClient();
			}
			
		}
		
	}

}
