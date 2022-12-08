package com.yj.nz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class DBHelper {
    /**
     * 关闭资源的方法
     * @param rs        要关闭的结果集
     * @param pstmt     要关闭的预编译对象
     */
    private void closeAll(ResultSet rs, PreparedStatement pstmt) {
        // 先开启的，后关闭
        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // 要关闭的预编译对象
        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 设置参数的方法  给预编译块语句中占位符 ? 赋值
     * @param pstmt
     * @param params        要执行的SQL语句中对应占位符 ? 的值意义对应
     */
    private void setParams(PreparedStatement pstmt, Object ... params) {
        // 去空判断  说明没有参数给我  也就一位置执行SQL语句中没有占位符
        if(params == null || params.length <= 0) {
            return;
        }

        // 循环参数  注意，先获得参数长度  节省资源
        for (int i = 0, len = params.length; i < len; i++) {
            try {
                pstmt.setObject(i+1, params[i]); // 顺序对应
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                // 项目建议加入日志，此时暂时打印
                System.out.println("第" + (i+1) + " 个参数赋值失败");
            }
        }

    }

    /**
     * 方法重载
     * 多条件动态拼接SQL查询语句的注入参数方法，注意方法的参数位置 构成重载
     * 设置参数的方法 给预编译块语句中占位符 ? 赋值
     * @param pstmt
     * @param prams         要执行的SQL语句中对应占位符 ? 的值一一对应
     */
    private void setParams(PreparedStatement pstmt, List<Object> prams) {
        // 去空判断  说明没有参数给我  也就意味着执行SQL语句中没有占位符
        if(prams == null || prams.isEmpty()) {
            return;
        }
        this.setParams(pstmt, prams.toArray());
    }

    /**
     * 更新操作     Object ... params  不定参数类似于数组，万物皆对象
     * @param con
     * @param sql           要执行的更新语句，可以insert、update、delete
     * @param params        要执行的SQL语句中对应占位符 ? 的值一一对应
     * @return
     */
    public int update(Connection con, String sql, Object ... params) {
        int result = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql); // 预编译执行语句
            // 给预编译语句中占位符赋值
            setParams(pstmt, params);
            result = pstmt.executeUpdate(); // 执行更新
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(null, pstmt);
        }
        return result;
    }

    /**
     * 更新操作                动态SQL语句多条件更新语句
     * @param con
     * @param sql           要执行的更新语句，可以insert、update、delete
     * @param params        要执行的SQL语句中对应占位符 ? 的值一一对应
     * @return
     */
    public int update(Connection con, String sql, List<Object> params) {
        if(params == null || params.isEmpty()) {
            return this.update(con, sql);
        }
        return this.update(con,sql, params.toArray());
    }

    /**
     * 多条SQL语句更新操作  事务处理
     * @param con
     * @param sqls          多条SQL语句要执行的更新语句，可以insert、update、delete
     * @param params        每一条SQL语句对应一个list，多个list存入params，要执行的SQL语句中对应占位符 ? 的值一一对应
     * @return
     */
    public int update(Connection con, List<String> sqls, List<List<Object>> params) throws SQLException {
        int result = -1;
        PreparedStatement pstmt = null;
        try {
            // 将提交方式设置手动提交   JDBC默认自动提交
            con.setAutoCommit(false);

            // 循环迭代
            for (int i = 0, len = sqls.size(); i < len; i++) {
                pstmt = con.prepareStatement(sqls.get(i)); // 预编译执行语句  依次获取SQL语句
                // 给预编译语句中占位符赋值
                setParams(pstmt, params.get(i));
                result = pstmt.executeUpdate(); // 执行更新
            }
            con.commit(); // 事务提交
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            con.rollback(); // 事务回滚
            // 提醒业务层
            throw new SQLException();
        } finally {
            // 将提交方式设置为自动提交
            con.setAutoCommit(true);
            closeAll(null, pstmt);
        }
        return result;
    }

    /**
     * 单行查询返回一条记录   select * from userinfo where user_id = ?
     * @param con
     * @param sql           查看SQL语句
     * @param params        传入参数
     * @return map          一条记录
     */
    public Map<String, Object> findSingle(Connection con, String sql, Object ... params) {
        Map<String, Object> map = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();

            List<String> columnNames = getAllColumnNames(rs); // 获取结果集中所有的列名

            if(rs.next()) { // 处理结果集
                map = new HashMap<String, Object>();
                for (String columnName : columnNames) { // 升级版  增强for  获取列名对应的值
                    map.put(columnName, rs.getObject(columnName)); // 列名做key   字段对应类型不确定
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(rs, pstmt);
        }

        return map;
    }

    public Map<String, Object> findSingle(Connection con, String sql, List<Object> params) {
        if(params == null || params.isEmpty()) {
            return this.findSingle(con, sql);
        }
        return this.findSingle(con, sql, params.toArray());
    }

    /**
     * 多条查询  返回多条记录  select * from userinfo
     * @param con
     * @param sql           查看SQL语句
     * @param params        传入参数
     * @return  list        多条记录
     */
    public List<Map<String, Object>> findMutile(Connection con, String sql, Object ... params) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = con.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            List<String> columnNames = getAllColumnNames(rs);
            // 处理结果集
            while(rs.next()) {
                map = new HashMap<String, Object>();
                for (String columnName : columnNames) {
                    map.put(columnName, rs.getObject(columnName));
                }
                list.add(map);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(rs, pstmt);
        }
        return list;
    }

    public List<Map<String, Object>> findMutile(Connection con, String sql, List<Object> params) {
        if(params == null || params.isEmpty()) {
            return this.findMutile(con, sql);
        }
        return this.findMutile(con, sql, params.toArray());
    }

    /**
     * JDBC 2.0获取所有的列名
     * @param rs    结果集
     * @return
     */
    private List<String> getAllColumnNames(ResultSet rs) {
        // 列名的集合
        List<String> columnNames = new ArrayList<>();
        try {
            // ResultSetMetaData检索此ResultSet对象的列的数量，类型和属性
            ResultSetMetaData rsmd = rs.getMetaData();
            // 获取表的总列数
            int count = rsmd.getColumnCount();
            // 范围确定循环
            for (int i = 1; i <= count; i++) {
                // 获取对应的列名
                columnNames.add(rsmd.getColumnName(i).toLowerCase()); // 表默认的字段大写，需要转换成小写
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return columnNames;
    }

    private Map<String, Method> getSetter(Class<?> c) {
        Map<String, Method> settes = new HashMap<>();
        Method[] methods = c.getDeclaredMethods();
        String methodName = null;
        for (Method method : methods) {
            methodName = method.getName();
            if(methodName.startsWith("set")) {
                settes.put(methodName, method);
            }
        }
        return settes;
    }

    /**
     * 以对象的方式返回数据，查询所有信息
     * @param con
     * @param c
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> findMutile(Connection con, Class<?> c, String sql, Object ... params) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<T> list = new ArrayList<>();

        try {
            pstmt = con.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            List<String> columnNames = getAllColumnNames(rs);
            T t = null; // 声明一个对象
            String obj = null; // 对应列的值
            Class<?> columnType = null; // 对应列的值的类型
            Class<?>[] types = null;

            Map<String, Method> setters = this.getSetter(c);
            String methodName = null;
            Method method = null;
            while(rs.next()) {
                t = (T) c.newInstance(); // 调用午餐构造方法 AdminInfo admin = new AdminInfo
                for (String columnName : columnNames) {
                    obj = rs.getString(columnName);
                    if(obj == null) {
                        continue;
                    }

                    methodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    method = setters.getOrDefault(methodName, null);
                    if(method == null) {
                        continue;
                    }

                    // 获取这个方法的形参类型  setTname(String tname)
                    types = method.getParameterTypes();
                    if(types == null) { // 获取这个setter方法的形参列表的类型
                        continue;
                    }
                    columnType = types[0]; // 如果是标准的setter方法，只会有一个参数，name我们获取它的第一个形参类型

                    if(columnType == Integer.TYPE) { // 说明是int类型
                        method.invoke(t, Integer.parseInt(obj));
                    } else if(columnType == Integer.class) { // 说明是Integer
                        method.invoke(t, Integer.valueOf(obj));
                    }else if(columnType == Float.TYPE) { // 说明是float类型
                        method.invoke(t, Float.parseFloat(obj));
                    } else if(columnType == Float.class) { // 说明是Float
                        method.invoke(t, Float.valueOf(obj));
                    }else if(columnType == Double.TYPE) { // 说明是float类型
                        method.invoke(t, Double.parseDouble(obj));
                    } else if(columnType == Double.class) { // 说明是Float
                        method.invoke(t, Double.valueOf(obj));
                    }
                }
                list.add(t); // 将T t对象存入list
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            closeAll(rs, pstmt);
        }
        return list;
    }

    /**
     * 以对象的方式返回数据，查询所有信息
     * @param con
     * @param c
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> findMutile(Connection con, Class<?> c, String sql, List<Object> params) {
        // 判断 如果list有值
        if(params == null || params.isEmpty()) {
            return this.findMutile(con, c, sql);
        }
        return this.findMutile(con, c, sql, params.toArray());
    }

    /**
     * 以对象的方式返回数据，查询单个信息
     * @param con
     * @param c
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> T findSingle(Connection con, Class<?> c, String sql, Object ... params) {
        List<T> list = this.findMutile(con, c, sql, params);
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 以对象的方式返回数据，查询单个信息  新增方法  多条件查询的优化
     * @param con
     * @param c
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> T findSingle(Connection con, Class<?> c, String sql, List<Object> params) {
        // 和上面的方法中的参数位置调换一下顺序，不然传入null，重载方法调用歧义
        if(params == null || params.isEmpty()) {
            return this.findSingle(con, c, sql);
        }
        return this.findSingle(con, c, sql, params.toArray());
    }

    /**
     * 获取总记录数的方式    新增的方法
     * @param con
     * @param sql
     * @param params
     * @return
     */
    public Integer total(Connection con, String sql, Object ... params) {
        int result = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            // 处理结果集
            if(rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            closeAll(rs, pstmt);
        }
        return result;
    }

    /**
     * 获取总记录数的方法  新增方法  多条件查询的优化
     * @param con
     * @param sql
     * @param params
     * @return
     */
    public Integer total(Connection con, String sql, List<Object> params) {
        // 和上面的方法中的参数位置调换一下顺序，不然传入null，重载方法调用歧义
        if (params == null || params.isEmpty()) {
            return this.total(con, sql);
        }
        return this.total(con, sql, params.toArray());
    }
}