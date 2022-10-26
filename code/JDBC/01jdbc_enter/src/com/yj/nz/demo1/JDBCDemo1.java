package com.yj.nz.demo1;

import org.junit.Test;

import java.sql.*;

/**
 * JDBC的入门程序
 * @author yjxz
 *
 */
public class JDBCDemo1 {

	@Test
	/**
	 * JDBC的入门
	 */
	public void demo1(){
		Connection conn = null;
		Statement statement = null;
		ResultSet rs  = null;
		
		try{
			// 1.加载驱动
			Class.forName("com.mysql.cj.jdbc.Driver");
			// DriverManager.registerDriver(driver);
			// 2.获得连接
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test01?useSSL=false&requireSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai", "root", "root");
			// 3.基本操作：执行SQL
			// 3.1获得执行SQL语句的对象
			statement = conn.createStatement();
			// 3.2编写SQL语句:
			String sql = "select * from user";
			// 3.3执行SQL:
			rs = statement.executeQuery(sql);
			// 3.4遍历结果集:
			while(rs.next()){
				System.out.print(rs.getInt("id")+" ");
				System.out.print(rs.getString("username")+" ");
				System.out.print(rs.getString("password")+" ");
				System.out.print(rs.getString("nickname")+" ");
				System.out.print(rs.getInt("age"));
				System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			// 4.释放资源
			// 标准资源释放的代码：
			/*rs.close();
			statement.close();
			conn.close();*/
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				rs = null;
			}
			
			if(statement !=null){
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				statement = null;
			}
			
			if(conn !=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				conn = null;
			}
		}

	}
}
