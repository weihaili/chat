package org.cn.kkl.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * close io util
 * @author Admin
 */
public class IoClose {

	public static void closeAll(Closeable ... io) {
		for (Closeable closeable : io) {
			try {
				if(null!=closeable) {
					closeable.close();
				}
			} catch (IOException e) {
				System.out.println("IoClose.closeable exception");
				e.printStackTrace();
			}
		}
	}
}
