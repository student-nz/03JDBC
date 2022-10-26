package com.yj.nz;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
						
						//这里可以用到继承
public class MyProperties extends Properties{
	private static MyProperties myProperties;
	
	//单例
	private MyProperties() {
//		InputStream is = new FileInputStream("db.properties");	//默认是当前项目，因为是文件输入流
		InputStream is = MyProperties.class.getClassLoader().getResourceAsStream("db.properties"); //默认是src文件夹,因为是类加载器加载
		
		try {
			this.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static MyProperties getInstance() {
		if( myProperties == null ) {
			myProperties = new MyProperties();
		}
		return myProperties;
	}
}
