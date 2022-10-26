package com.yj.nz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {
	//2、建立连接
	public Connection getCon() {
		Connection conn = null;
		try {
			//conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:orcl", "scott", "a");
			//新写法适应性更强，只要改改配置文件即可；不用动源代码
			conn = DriverManager.getConnection(MyProperties.getInstance().getProperty("url"), MyProperties.getInstance());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
	/**
	 * 执行增删改的功能		因为对于增删改而言，我们只需要知道，是成功还是失败；而且执行的方法都是一样的
	 * @param sql		要执行的sql语句
	 * @param params	量词参数，首先，量词参数可以使任意个值
	 * @return		返回受影响的行
	 */
	public int doUpdate(String sql, Object... params) {
		int result = -1;
		//先获取连接
		try {
			Connection conn = getCon();
			PreparedStatement ps = conn.prepareStatement(sql);
			//注入参数
			doParams(ps, params);
			//执行
			result = ps.executeUpdate();
			//关闭连接
			closeAll(conn, ps, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	public List<Map<String,String>> find(String sql, Object... params){
		List<Map<String,String>> list = new ArrayList();
		
		try {
			Connection conn = getCon();
			PreparedStatement ps = conn.prepareStatement(sql);
			//注入参数
			doParams(ps, params);
			//执行
			ResultSet rs = ps.executeQuery();
			
			//想办法，来得到我们的列名，以列名为键
			ResultSetMetaData rsmd = rs.getMetaData();
			String[] columnNames = new String[rsmd.getColumnCount()];
			for (int i = 0; i < columnNames.length; i++) {
				//注意，千万不要写成 rsmd.getCatalogName(i + 1)
				columnNames[i] = rsmd.getColumnName(i + 1);
			}

			//字段已经有了，那么，就可以开始存储了
			while( rs.next() ) {
				Map<String, String> map = new HashMap();
				//循环数组，取值
				for(int i = 0;i < columnNames.length; i++) {
					//键默认转换为小写，因为数据库是不区分大小写的，java严格区分大小写的
					map.put(columnNames[i].toLowerCase(), rs.getString( columnNames[i] ));
				}
				//数据添加完毕，继续将map添加到list里面
				list.add(map);
			}
		
			//关闭连接
			closeAll(conn,ps,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return list;	
	}
	
	
	//关闭连接
	public void closeAll(Connection conn,PreparedStatement ps,ResultSet rs) {
		try {
			if( conn !=null ) {
				conn.close();
			}
			if( ps !=null ) {
				ps.close();
			}
			if( rs !=null ) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//注入参数
	private void doParams(PreparedStatement ps, Object... params) {
		
		try {
			
			if( ps != null && params.length > 0 ) {
				for(int i = 0;i < params.length; i++ ) {
					ps.setObject(i + 1, params[i]);
				}
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		}
	
	}
	
	
	//静态块
	static {
		//1、加载驱动
		try {
			//Class.forName("oracle.jdbc.OracleDriver");
			Class.forName(MyProperties.getInstance().getProperty("driverClass"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
