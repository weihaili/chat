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
			ServerSocket server =new ServerSocket(8889);
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
		private String name;
		
		public MyChannel() {}
		
		public MyChannel(Socket client) {
			try {
				dis=new DataInputStream(client.getInputStream());
				dos=new DataOutputStream(client.getOutputStream());
				
				this.name=dis.readUTF();
				System.out.println(this.name);
				this.sendMsgToClient("welcome you to this chat room");
				sendMsgToOtherClient("welcome "+this.name+" enter the chat room",true);
				
			} catch (IOException e) {
				System.out.println("server get stream exception");
				e.printStackTrace();
				IoClose.closeAll(dos,dis);
				members.remove(this);
				isRunning=false;
			}
		}
		
		private String getMsgFromClient() {
			String data="";
			try {
				data= dis.readUTF();
			} catch (IOException e) {
				System.out.println("server getMsgFromClient is exception");
				e.printStackTrace();
				IoClose.closeAll(dis,dos);
				members.remove(this);
				isRunning=false;
			}
			return data;
		}
		
		private void sendMsgToClient(String msg) {
			try {
				if (msg.isEmpty()) {
					System.out.println("server sendMsgToClient get msg is null");
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
		
		private void sendMsgToOtherClient(String msg,boolean sys) {
			if(msg.isEmpty()) {
				System.out.println("server sendMsgToOtherClient get mst is null");
				return;
			}else{
				//private chat
				if(msg.startsWith("@") && msg.contains(":")) {
					String tempName=msg.substring(msg.indexOf("@")+1, msg.indexOf(":"));
					String content=msg.substring(msg.indexOf(":")+1);
					for (MyChannel mc : members) {
						if(mc.name.equals(tempName)) {
							mc.sendMsgToClient(this.name+" silently to you say: "+content);
						}
					}
				}else {
					//public chat
					for (MyChannel mc : members) {
						if(mc==this) {
							continue;
						}else {
							if(sys) {
								mc.sendMsgToClient("system message: "+msg);
							}else {
								mc.sendMsgToClient(this.name+" to all say: "+msg);
							}
						}
					}
				}
				
			}
		}
		
		@Override
		public void run() {
			while (isRunning) {
				String msg=getMsgFromClient();
				if(msg.isEmpty()) {
					System.out.println("server get msg from client is null");
					return;
				}
				sendMsgToOtherClient(msg,false);
			}
		}
	}

}
