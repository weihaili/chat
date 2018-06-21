package org.cn.kkl.nio.demo01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannel01 {
	
	private ByteBuffer buffer=ByteBuffer.allocate(4096);
	
	private FileChannel isfc;
	private FileChannel osfc;
	
	private void fileChannelOperate() {
		try {
			isfc=new FileInputStream("D:"+File.separator+"temp"+File.separator+"Emp.java").getChannel();
			osfc=new FileOutputStream("D:"+File.separator+"temp"+File.separator+"a.txt",true).getChannel();
			buffer.clear();
			//read data
			int len=isfc.read(buffer);
			System.out.println(new String(buffer.array(),0,len));
			
			//write data
			ByteBuffer writeBuffer=ByteBuffer.wrap("jack".getBytes());
			osfc.write(writeBuffer);
			
			osfc.close();
			isfc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}

	public static void main(String[] args) {
		new FileChannel01().fileChannelOperate();
		

	}

}
