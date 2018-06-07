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
		try {
			Socket client = new Socket("localhost",8888);
			new Thread(new Send(client)).start();
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
	
	private String receiveToServer() {
		try {
			return dis.readUTF();
		} catch (IOException e) {
			System.out.println("client receive to server is exception");
			e.printStackTrace();
			IoClose.closeAll(dis);
			isRunning=false;
		}
		return null;
	}
	
	@Override
	public void run() {
		while (isRunning) {
			System.out.println(receiveToServer());
		}
	}
}

class Send implements Runnable{
	private BufferedReader console;
	
	private DataOutputStream dos;
	
	private boolean isRunning=true;
	
	public Send() {
		console=new BufferedReader(new InputStreamReader(System.in));
	}
	
	public Send(Socket client){
		this();
		try {
			dos=new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			System.out.println("client get dataStream exception");
			e.printStackTrace();
			IoClose.closeAll(dos,console);
			isRunning=false;
		}
	}
	
	private String getDataFromConsole() {
		try {
			return console.readLine();
		} catch (IOException e) {
			System.out.println("get data from console exception");
			e.printStackTrace();
			IoClose.closeAll(console,dos);
			isRunning=false;
		}
		return null;
	}
	
	private void sendToServer() {
		String sendMsg=getDataFromConsole();
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
			sendToServer();
		}
	}
}
