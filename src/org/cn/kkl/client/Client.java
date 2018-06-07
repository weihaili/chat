package org.cn.kkl.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import org.cn.kkl.util.IoClose;

public class Client {
	public static void main(String[] args) {
		new Client().start();
	}
	
	private void start() {
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter name:");
			String name=br.readLine();
			if(name.isEmpty() || name.trim().length()>30) {
				System.out.println("your name is illegal");
			}
			Socket client = new Socket("localhost",8889);
			new Thread(new Send(client,name)).start();
			new Thread(new Receive(client)).start();
		} catch (UnknownHostException e) {
			System.out.println("client connection exception");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Receive implements Runnable{

	private DataInputStream dis;
	
	private boolean isRunning=true;
	
	public Receive() {
	}
	
	public Receive(Socket client) {
		try {
			dis=new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			System.out.println("client receive get stream exception");
			e.printStackTrace();
			IoClose.closeAll(dis);
			isRunning=false;
		}
	}
	
	private String receiveFromServer() {
		String msg="";
		try {
			msg= dis.readUTF();
		} catch (IOException e) {
			System.out.println("client receive to server is exception");
			e.printStackTrace();
			IoClose.closeAll(dis);
			isRunning=false;
		}
		return msg;
	}
	
	@Override
	public void run() {
		while (isRunning) {
			if(receiveFromServer().isEmpty()) {
				return ;
			}
			System.out.println(receiveFromServer());
		}
	}
}

class Send implements Runnable{
	private BufferedReader console;
	
	private DataOutputStream dos;
	
	private boolean isRunning=true;
	
	private String name;
	
	public Send() {
		console=new BufferedReader(new InputStreamReader(System.in));
	}
	
	public Send(Socket client,String name){
		this();
		this.name=name;
		try {
			dos=new DataOutputStream(client.getOutputStream());
			sendToServer(this.name);
		} catch (IOException e) {
			System.out.println("client get dataStream exception");
			e.printStackTrace();
			IoClose.closeAll(dos,console);
			isRunning=false;
		}
	}
	
	private String getDataFromConsole() {
		String data="";
		try {
			data= console.readLine();
		} catch (IOException e) {
			System.out.println("get data from console exception");
			e.printStackTrace();
			IoClose.closeAll(console,dos);
			isRunning=false;
		}
		return data;
	}
	
	private void sendToServer(String sendMsg) {
		try {
			if (sendMsg.isEmpty()) {
				return;
			}else {
				dos.writeUTF(sendMsg);
				dos.flush();
			}
		} catch (IOException e) {
			System.out.println("client sendtoServer exception");
			e.printStackTrace();
			IoClose.closeAll(console,dos);
			isRunning=false;
		}
	}
	
	@Override
	public void run() {
		while (isRunning) {
			if(getDataFromConsole().isEmpty()) {
				return;
			}
			sendToServer(getDataFromConsole());
		}
	}
}
