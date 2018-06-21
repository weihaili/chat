package org.cn.kkl.nio.demo01;

import java.nio.IntBuffer;
@SuppressWarnings(value="all")
public class Test01 {

	public static void main(String[] args) {
		//create specified length buffer
		IntBuffer buffer=IntBuffer.allocate(10);
		
		//create int type of array
		int[] arr = new int[] {1,9,0,4};
		
		//use array create buffer
		//buffer=buffer.wrap(arr);
		
		//create an buffer using a range of array
		buffer=buffer.wrap(arr, 1, 3);
		System.out.println(buffer);
		System.out.println(buffer.limit());
		for (int i = 0; i < buffer.limit()-7; i++) {
			System.out.println(buffer.get());
			
		}
		
		buffer.flip();
		//update specified position data
		buffer.put(0, 100);
		for (int i = 0; i < buffer.limit(); i++) {
			System.out.println(buffer.get());
		}
		
		buffer.duplicate();
		System.out.println(buffer);
	}

}
