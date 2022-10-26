package com.yj.nz.datasource.demo3;

import com.yj.nz.utils.JDBCUtils;
import com.yj.nz.utils.JDBCUtils2;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 工具类测试
 * @author jt
 *
 */
public class C3P0Demo2 {
	@Test
	/**
	 * 使用新的工具类的测试：
	 */
	public void demo(){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			// 获得连接：从连接池中获取：
			// 从连接池中获得连接:
			conn = JDBCUtils2.getConnection();
			// 编写SQL：
			String sql = "select * from account";
			// 预编译SQL:
			pstmt = conn.prepareStatement(sql);
			// 执行SQL：
			rs = pstmt.executeQuery();
			while(rs.next()){
				System.out.println(rs.getInt("id")+" "+rs.getString("name")+" "+rs.getDouble("money"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			JDBCUtils.release(rs, pstmt, conn);
		}
	}
}
