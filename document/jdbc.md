# JDBC（MySQL8讲解）

# 1. 数据持久化

```
数据持久化就是指将那些内存中的瞬时数据保存到存储设备中，保证即使在电脑或手机在关闭状态，这些数据仍然不会消失

瞬时数据是指那些存储在内存当中，有可能会因为程序关闭或其他原因导致内存被回收而丢失的数据
例如：将内存中的数据持久化保存到硬盘

数据持久化的应用场景：
	开发中经常将内存中的数据持久化到磁盘文件、XML数据文件、数据库中等等，其中主要应用于关系型数据库中
```

# 2. Java中的数据存储技术

```
1.  JDBC直接访问数据库
2.  JDO (Java Data Object )技术
3. 第三方O/R工具，如Hibernate, Mybatis 等
. . .	
JDBC是java访问数据库的基石，JDO、Hibernate、MyBatis等只是更好的封装了JDBC
```

# 3. JDBC是什么？

```
JDBC(Java Database Connectivity)是sun公司提供的一套用于数据库操作的接口，java程序员只需要面向这套接口编程即可
这套接口定义了用来访问数据库的标准Java实现类类库
不同的数据库厂商，针对这套接口，提供了不同的实现，即不同数据库的驱动，
所以说JDBC就是一套规范，使用JDBC就是面向接口编程
同时JDBC也是基于TCP/IP的应用层协议,数据的传输都是通过socket进行
如下图详解：
```




![1566741692804](assets/1566741692804.png)

# 4. JDBC包含两个层次？

## 	1. 面向应用的API

```
Java API， 抽象接口，供应用程序开发人员使用（连接数据库，执行SQL语句，获得结果）
```

## 	2. 面向数据库的API

```
Java Driver API，供开发商开发数据库驱动程序用
```

# 5. JDBC程序编写步骤

```
1.加载驱动
2.获得连接
3. 获得执行SQL语句的对象
4. 编写SQL语句
5. 执行SQL
6. 遍历结果集
```

# 6. Driver详解

```
java.sql.Driver 接口是所有 JDBC 驱动程序需要实现的接口，
这个接口提供给数据库厂商使用的，不同数据库厂商提供不同的实现
在程序中不需要直接去访问 Driver 接口的实现类，而是由驱动程序管理器类(java.sql.DriverManager)去调用这些Driver实现
常见驱动：
	Oracle的驱动：oracle.jdbc.driver.OracleDriver	
	mySql5xx的驱动：com.mysql.jdbc.Driver
	mysql8xx的驱动：com.mysql.cj.jdbc.Driver
	. . .
```

# 7. 加载与注册驱动详解

## 	1. 加载驱动

```
Class.forName(“com.mysql.jdbc.Driver”);
Class.forName(“com.mysql.cj.jdbc.Driver”);

加载 JDBC 驱动需调用 Class 类的静态方法 forName()，向其传递要加载的 JDBC 驱动的类名
```

源码分析：

​	![image-20221025235928071](assets\image-20221025235928071.png)

## 			2. 注册驱动

```
DriverManager.registerDriver(“com.mysql.cj.jdbc.Driver“)
DriverManager 类是驱动程序管理器类，负责管理驱动程序，使用它来注册驱动

为什么不显示调用DriverManager 类的 registerDriver() 方法来注册驱动程序类的实例？
	因为 Driver 接口的驱动程序类都包含了这样一个静态代码块，
	在这个静态代码块中，会调用 DriverManager.registerDriver() 方法来注册自身的一个实例
```

​	源码分析：

![image-20221025205458616](assets\image-20221025205458616.png)

DriverManager.registerDriver(“com.mysql.cj.jdbc.Driver“)源码分析：

![image-20221026000114525](assets\image-20221026000114525.png)

# 8. 获取连接

```
三种语法：
	1. getConnection(String url)：
		url：访问数据库的 URL 路径
	2. getConnection(String url,Properties info)：
		url：访问数据库的 URL 路径
		info：是一个持久的属性集对象（通常指的是Properties），包括 user 和 password 属性
	3. Connection(String url,String user,String password)
		url：访问数据库的 URL 路径
		user：是访问数据库的用户名
		password：连接数据库的密码
```

源码分析：

![image-20221026000331847](assets\image-20221026000331847.png)

![image-20221026000400785](assets\image-20221026000400785.png)

![image-20221026000302990](assets\image-20221026000302990.png)

# 9. JDBC URL详解

```
JDBC URL就是用于标识一个被注册的驱动程序，驱动程序管理器通过这个 URL 选择正确的驱动程序，从而建立到数据库的连接
```

## 	1. JDBC URL的标准

```
JDBC URL的标准由三部分组成：jdbc:子协议:子名称
	例如：jdbc:mysql://localhost:3306/test?jdbc:mysql://localhost:3306/test01?
	useSSL=false&requireSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
```

```
	协议：JDBC URL中的协议总是jdbc
    子协议：子协议用于标识一个数据库驱动程序
    子名称：一种标识具体数据库的方法
    
例如：MySQL的URL的常用编写方式为例

    jdbc:mysql://主机名称:mysql服务端口号/数据库名称?参数1=值1&参数2=值2
    jdbc:mysql://localhost:3306/yjxz?localhost:3306/test？
    useUnicode=true&characterEncoding=utf8&serverTimezoneGMT%2B8&useSSL=true&rewriteBatchedStatements=true
协议：jdbc
子协议：mysql
子名称：localhost:3306/yjxz?	useUnicode=true&characterEncoding=utf8&serverTimezoneGMT%2B8&useSSL=true&rewriteBatchedStatements=true

	MySQL连接URL编写方式：
		jdbc:mysql://主机名称:mysql服务端口号/数据库名称?参数=值&参数=值
		jdbc:mysql://localhost:3306/yjxz?useUnicode=true&characterEncoding=utf8
		jdbc:mysql://localhost:3306/yjxz?user=root&password=root
		如果JDBC程序与服务器端的字符集不一致，会导致乱码，那么可以通过参数指定服务器端的字符集
		其中xml文件中的&要用字符实体，即&要用&amp;

	Oracle 9i的连接URL编写方式：
		jdbc:oracle:thin:@主机名称:oracle服务端口号:数据库名称
		jdbc:oracle:thin:@localhost:1521:yjxz
		
	SQLServer的连接URL编写方式:
		jdbc:sqlserver://主机名称:sqlserver服务端口号:DatabaseName=数据库名称
		jdbc:sqlserver://localhost:1433:DatabaseName=yjxz
		
补充Oracle JDBC URL：

	使用jdbc连接oracle时url有三种格式
		格式一: Oracle JDBC Thin using an SID
			jdbc:oracle:thin:@host:port:SID 
			例如: jdbc:oracle:thin:@localhost:1521:orcl 
		
		格式二: Oracle JDBC Thin using a ServiceName
			jdbc:oracle:thin:@//host:port/service_name 
			例如: jdbc:oracle:thin:@//localhost:1521/orcl.city.com 
		
		格式三：Oracle JDBC Thin using a TNSName
		    jdbc:oracle:thin:@TNSName 
    		例如:  jdbc:oracle:thin:@TNS_ALIAS_NAME 
    		
thin和oci的url写法上的区别：
	jdbc:oracle:thin:@server ip: service
	jdbc:oracle:oci:@service
	
	1. 使用上来看，oci必须在客户机上安装oracle客户端或才能连接，而thin就不需要，因此使用thin的多

	2. 原理上来看，thin是纯java实现tcp/ip的c/s通讯；而oci方式,客户端通过native java method调用c library访问服务端，
		而这个c library就是oci(oracle called interface)，因此这个oci总是需要随着oracle客户端安装	

	3. 驱动上来看，它们分别是不同的驱动类别，oci是二类驱动， thin是四类驱动，但它们在功能上并无差异
```

## 2. 常用参数

![image-20221025233958583](assets\image-20221025233958583.png)

```
1.useSSL
	-- MySQL在高版本需要指明是否进行SSL连接
	-- 目的是保障数据传输的安全
	-- 需要在url后面添加useSSL参数
	-- 否则在运行时控制台会出现红色警告
   	  useSSL=true  连接
  	  useSSL=false 不连接

2.serverTimezone
		使用MySQL8时可能会出现时差问题
	例：
		向数据库中添加的时间是："2021-10-02 10:00:00"
		然而数据库中显示的时间却少了8个小时，显示为："2021-10-02 02:00:00"

	根本原因是时区设置的问题
		-- UTC代表的是全球标准时间
		-- 但是我们使用的时间是北京时区也就是东八区，领先UTC八个小时
		-- UTC + 8 = 北京时间

	解决方案：修改url的时区
   	  serverTimezone=GMT%2B8			北京时间东八区
  	   serverTimezone=Asia/Shanghai	上海时间
   	  serverTimezone=UTC				UTC时间，注意，区分大小写，一定是大写，不可以是小写的

3. rewriteBatchedStatements
	实现高性能的批量插入
	需要在MySQL的JDBC连接的URL中添加rewriteBatchedStatements参数
	并且保证5.1.13以上版本的驱动

	MySQL JDBC驱动在默认情况下会无视executeBatch()语句
	把我们期望批量执行的一组sql语句拆散
	一条一条地发给MySQL数据库
	此时的批量插入实际上是单条插入，直接造成较低的性能

	只有把rewriteBatchedStatements参数置为true
	驱动才会帮你批量执行SQL
	另外这个选项对INSERT/UPDATE/DELETE都有效

4. useUnicode 、characterEncoding
	JDBC程序与服务器端的字符集不一致，会导致乱码，可以通过参数指定服务器端的字符集

	设置字符集时，需要设置如下参数
		-- useUnicode=true			开启字符集的设置
		-- characterEncoding=utf8	设置字符集为utf8

注意事项：
	在xml配置文件中配置数据库url时，要使用&的转义字符也就是 &amp
```

# 10. 配置文件的好处

​			实现了代码和数据的分离

# 	11. 数据库三种调用方式

​			在java.sql 包中有 3 个接口分别定义了对数据库的调用的不同方式：

## 			1. Statement

```
通过调用Connection对象的createStatement()创建Statement对象

作用：
	用于执行静态 SQL 语句并返回它所生成结果的对象

Statement的增删改查操作：
	int excuteUpdate(String sql)：执行更新操作 如：INSERT、UPDATE、DELETE
	ResultSet executeQuery(String sql)：执行查询操作	如：SELECT
	
弊端：
	1. 存在拼串操作，繁琐
	
	2. 存在SQL注入问题

	SQL 注入就是利用某些系统 没有对用户输入的数据进行充分的检查，而在用户输入数据中注入非法的 SQL 语句段或命令，
	从而利用系统的 SQL 引擎完成恶意行为的做法
	如：SELECT user,password FROM user_table WHERE USER = '1' or ' AND PASSWORD = '='1' or '1' = '1';
	 or '1' = '1'，这意味着无论如何操作都会操作成功

如何防范sql注入？
	 利用PreparedStatement防范
```

## 			2. PrepatedStatement		

```
通过利用PreparedStatement防范SQL注入问题

PreparedStatement 接口是 Statement 的子接口，它表示一条预编译过的 SQL 语句
SQL语句被预编译并存储在此对象中，可以使用此对象多次高效地执行该语句

通过调用Connection对象的preparedStatement(String sql)方法获取PreparedStatement对象

PreparedStatement对象所代表的SQL语句中的参数用问号(?)来表示

调用PreparedStatement对象的setXxx()方法设置这些参数，setXxx() 方法有两个参数
参数一：SQL 语句中要设置的参数索引(从 1 开始)
参数二：SQL 语句中要设置的参数值

PreparedStatement相对于Statement的优势：
	1. 代码的可读性和可维护性
	2. 提高了性能
	3. 安全
原因：
	提高了性能：
		预编译语句有可能被重复调用，DBServer的编译器会把预编译语句的执行后代码缓存下来
		那么下次调用时只要是相同的预编译语句就不需要编译，只要将参数直接传入编译过的语句的执行代码中就会得到执行
		在statement语句中,每执行一次都要对传入的语句编译一次
	安全：
		语法检查、语义检查、防止SQL 注入

相关API：
	 ResultSet executeQuery()：	查询操作
	 int executeUpdate() throws SQLException：增删改操作

批量处理相关API：
	当需要成批插入或者更新记录时，可以采用Java的批量更新机制，
	这一机制允许多条语句一次性提交给数据库批量处理，通常情况下比单独提交处理更有效率
		1. addBatch(String)：添加需要批量处理的SQL语句或是参数；
		2. executeBatch()：执行批量处理语句，执行批处理:
		3. clearBatch():清空缓存的数据

	两种批量执行SQL语句：
		1. 多条SQL语句的批量处理；
		2. 一个SQL语句的批量传参；
```

## 			3. CallableStatement

​			用于执行 SQL 存储过程：

# 12. Java与SQL对应数据类型转换表

| Java类型           | SQL类型                  |
| ------------------ | ------------------------ |
| boolean            | BIT                      |
| byte               | TINYINT                  |
| short              | SMALLINT                 |
| int                | INTEGER                  |
| long               | BIGINT                   |
| String             | CHAR,VARCHAR,LONGVARCHAR |
| byte   array       | BINARY  ,    VAR BINARY  |
| java.sql.Date      | DATE                     |
| java.sql.Time      | TIME                     |
| java.sql.Timestamp | TIMESTAMP                |

# 13. ResultSet与ResultSetMetaData

## 		1.  ResultSet

```
	PreparedStatement的executeQuery()：查询结果是一个ResultSet 对象
	ResultSet对象封装了执行数据库操作的结果集，实际上就是一张数据表，有一个指针指向数据表的第一条记录的前面
	ResultSet对象的next()方法移动到下一行，调用 next()方法检测下一行是否有效
		若有效，该方法返回 true，且指针下移，相当于Iterator对象的 hasNext() 和 next() 方法的结合体
		当指针指向一行时, 可以通过调用 getXxx(int index) 或 getXxx(String columnLabel) 获取每一列的值
		例如: getInt(1), getString("name")
		Java与数据库交互涉及到的相关Java API中的索引都是从1开始
```

## 	2. ResultSetMetaData		

```
ResultSetMetaData可用于获取关于 ResultSet 对象中列的类型和属性信息的对象
ResultSetMetaData meta = rs.getMetaData();

相关API：
	getColumnName(int column)：获取指定列的名称

	getColumnLabel(int column)：获取指定列的别名

	getColumnCount()：返回当前 ResultSet 对象中的列数

	getColumnTypeName(int column)：检索指定列的数据库特定的类型名称

	getColumnDisplaySize(int column)：指示指定列的最大标准宽度，以字符为单位 

	isNullable(int column)：指示指定列中的值是否可以为 null

	isAutoIncrement(int column)：指示是否自动为指定列进行编号，这样这些列仍然是只读的

资源的释放：

	1. 释放ResultSet, Statement,Connection。

	2. 数据库连接（Connection）是非常稀有的资源，用完后必须马上释放，
		如果Connection不能及时正确的关闭将导致系统宕机，Connection的使用原则是尽量晚创建，尽量早的释放

	3. 一般在finally中关闭，因为finally中，无论是否出现异常都会执行finally代码块中的代码
		保证即使其他代码出现异常，资源也一定能被关闭。、
```

# 14. 操作BLOB类型字段

```
在MySQL中，BLOB是一个二进制大型对象，是一个可以存储大量数据的容器，它能容纳不同大小的数据

插入BLOB类型的数据必须使用PreparedStatement，因为BLOB类型的数据无法使用字符串拼接写的

MySQL的四种BLOB类型(除了在存储的最大信息量上不同外，他们是等同的)
	TinyBlob：最大255
	Blob：最大65K
	MediumBlob：16M
	LongBlob: 4G
根据需要存入的数据大小定义不同的BLOB类型，	如果使用BLOB类型存储的文件过大，数据库的性能会下降

常见错误：
	如果在指定了相关的Blob类型以后，还报错：xxx too large
	找my.ini文件加上如下的配置参数： max_allowed_packet=16M，允许最大数据包传输为16M
```

# 15. 数据库连接池

## 	1. 为什么要使用连接池技术？

```
在开发基于数据库的web程序时，传统的模式基本是按以下步骤：
	1. 在主程序（如servlet、beans）中建立数据库连接
	2. 进行sql操作
	3. 断开数据库连接

这种模式开发，存在的问题:
	1. 普通的JDBC数据库连接使用 DriverManager 来获取，
		每次向数据库建立连接的时候都要将 Connection 加载到内存中，再验证用户名和密码(得花费0.05s～1s的时间)
		需要数据库连接的时候，就向数据库要求一个，执行完成后再断开连接
		这样的方式将会消耗大量的资源和时间
		数据库的连接资源并没有得到很好的重复利用
		若同时有几百人甚至几千人在线，频繁的进行数据库连接操作将占用很多的系统资源，严重的甚至会造成服务器的崩溃
		对于每一次数据库连接，使用完后都得断开
		否则，如果程序出现异常而未能关闭，将会导致数据库系统中的内存泄漏，最终将导致重启数据库
	2. 何为Java的内存泄漏？（看JVM篇）
	3. 这种开发不能控制被创建的连接对象数，系统资源会被毫无顾及的分配出去
		如连接过多，也可能导致内存泄漏，服务器崩溃

连接池技术就是为解决传统开发中的数据库连接问题而出现的一种技术
```

## 	2. 基本思想

```
数据库连接池的基本思想：就是为数据库连接建立一个“缓冲池”
	1. 预先在缓冲池中放入一定数量的连接，当需要建立数据库连接时，只需从“缓冲池”中取出一个，使用完毕之后再放回去。
	2. 数据库连接池负责分配、管理和释放数据库连接，它允许应用程序重复使用一个现有的数据库连接，而不是重新建立一个
	3. 数据库连接池在初始化时将创建一定数量的数据库连接放到连接池中，
		这些数据库连接的数量是由最小数据库连接数来设定*的。无论这些数据库连接是否被使用，
		连接池都将一直保证至少拥有这么多的连接数量
		连接池的最大数据库连接数量限定了这个连接池能占有的最大连接数，
		当应用程序向连接池请求的连接数超过最大连接数量时，这些请求将被加入到等待队列中
```

## 3. 相比较传统方式的优点：

### 	1. 资源重用

​		避免了频繁创建，释放连接引起的大量性能开销，减少系统消耗的基础，增加了系统运行环境的平稳性

### 	2. 更快的系统反应速度

​		数据库连接池在初始化过程中，往往已经创建了若干数据库连接置于连接池中备用

​		对于业务请求处理而言，直接利用现有可用连接，

​		避免了数据库连接初始化和释放过程的时间开销，从而减少了系统的响应时间

### 	3. 新的资源分配手段

​		对于多应用共享同一数据库的系统而言，可在应用层通过数据库连接池的配置，

​		实现某一应用最大可用数据库连接数的限制，避免某一应用独占所有的数据库资源

### 	4. 统一的连接管理，避免数据库连接泄漏

​		连接池可根据预先的占用超时设定，强制回收被占用连接，

​		从而避免了常规数据库连接操作中可能出现的资源泄露


## 	4. JDBC的数据库连接池

​			JDBC的数据库连接池使用 javax.sql.DataSource 来表示，

​			DataSource 只是一个接口，该接口通常由服务器(Weblogic, WebSphere, Tomcat)或一些开源组织提供实现

## 5. 其它常用的数据库连接池

### 				1. C3P0

​				C3P0是一个开源组织提供的一个数据库连接池，速度相对较慢，稳定性还可以，hibernate官方推荐使用

### 				2. DBCP

​				DBCP是Apache提供的数据库连接池，

​				tomcat 服务器自带dbcp数据库连接池，

​				速度相对c3p0较快，但因自身存在BUG，Hibernate3已不再提供支持

### 				3. Proxool

​					Proxool是sourceforge下的一个开源项目数据库连接池，有监控连接池状态的功能，稳定性较c3p0差一点

### 				4. BoneCP

​					BoneCP是一个开源组织提供的数据库连接池，速度快

### 				5. Druid

​					Druid是阿里提供的数据库连接池，据说是集DBCP 、C3P0 、Proxool 优点于一身的数据库连接池，

​					但是速度不确定是否有BoneCP快

```
DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
```



### 				6. DataSource

​						DataSource 通常被称为数据源，它包含连接池和连接池管理两个部分，习惯上也经常把 DataSource 称为连接池

​						DataSource用来取代DriverManager来获取Connection，获取速度快，同时可以大幅度提高数据库访问速度

```
注意：
	数据源和数据库连接不同，数据源无需创建多个，它是产生数据库连接的工厂，因此整个应用只需要一个数据源即可
	当数据库访问结束后，程序还是像以前一样关闭数据库连接：conn.close(); 
	但conn.close()并没有关闭数据库的物理连接，它仅仅把数据库连接释放，归还给了数据库连接池
```

## 6. 常用连接池相关配置

### 	1. c3p0-config.xml参数清单

```xml
    <c3p0-config>   
        <default-config>   
        <!--当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3 -->   
        <property name="acquireIncrement">3</property>   
     
        <!--定义在从数据库获取新连接失败后重复尝试的次数。Default: 30 -->   
        <property name="acquireRetryAttempts">30</property>   
           
        <!--两次连接中间隔时间，单位毫秒。Default: 1000 -->   
        <property name="acquireRetryDelay">1000</property>   
           
        <!--连接关闭时默认将所有未提交的操作回滚。Default: false -->   
        <property name="autoCommitOnClose">false</property>   
           
        <!--c3p0将建一张名为Test的空表，并使用其自带的查询语句进行测试。如果定义了这个参数那么   
        属性preferredTestQuery将被忽略。你不能在这张Test表上进行任何操作，它将只供c3p0测试   
        使用。Default: null-->   
        <property name="automaticTestTable">Test</property>   
           
        <!--获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常。但是数据源仍有效   
        保留，并在下次调用getConnection()的时候继续尝试获取连接。如果设为true，那么在尝试   
        获取连接失败后该数据源将申明已断开并永久关闭。Default: false-->   
        <property name="breakAfterAcquireFailure">false</property>   
           
        <!--当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出   
        SQLException,如设为0则无限期等待。单位毫秒。Default: 0 -->   
        <property name="checkoutTimeout">100</property>   
           
        <!--通过实现ConnectionTester或QueryConnectionTester的类来测试连接。类名需制定全路径。   
        Default: com.mchange.v2.c3p0.impl.DefaultConnectionTester-->   
        <property name="connectionTesterClassName"></property>   
           
        <!--指定c3p0 libraries的路径，如果（通常都是这样）在本地即可获得那么无需设置，默认null即可   
        Default: null-->   
        <property name="factoryClassLocation">null</property>   
           
        <!--强烈不建议使用该方法，将这个设置为true可能会导致一些微妙而奇怪的bug-->   
        <property name="forceIgnoreUnresolvedTransactions">false</property>   
           
        <!--每60秒检查所有连接池中的空闲连接。Default: 0 -->   
        <property name="idleConnectionTestPeriod">60</property>   
           
        <!--初始化时获取三个连接，取值应在minPoolSize与maxPoolSize之间。Default: 3 -->   
        <property name="initialPoolSize">3</property>   
           
        <!--最大空闲时间,60秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0 -->   
        <property name="maxIdleTime">60</property>   
           
        <!--连接池中保留的最大连接数。Default: 15 -->   
        <property name="maxPoolSize">15</property>   
           
        <!--JDBC的标准参数，用以控制数据源内加载的PreparedStatements数量。但由于预缓存的statements   
        属于单个connection而不是整个连接池。所以设置这个参数需要考虑到多方面的因素。   
        如果maxStatements与maxStatementsPerConnection均为0，则缓存被关闭。Default: 0-->   
        <property name="maxStatements">100</property>   
           
        <!--maxStatementsPerConnection定义了连接池内单个连接所拥有的最大缓存statements数。Default: 0 -->   
        <property name="maxStatementsPerConnection"></property>   
           
        <!--c3p0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能   
        通过多线程实现多个操作同时被执行。Default: 3-->   
        <property name="numHelperThreads">3</property>   
           
        <!--当用户调用getConnection()时使root用户成为去获取连接的用户。主要用于连接池连接非c3p0   
        的数据源时。Default: null-->   
        <property name="overrideDefaultUser">root</property>   
           
        <!--与overrideDefaultUser参数对应使用的一个参数。Default: null-->   
        <property name="overrideDefaultPassword">password</property>   
           
        <!--密码。Default: null-->   
        <property name="password"></property>   
           
        <!--定义所有连接测试都执行的测试语句。在使用连接测试的情况下这个一显著提高测试速度。注意：   
        测试的表必须在初始数据源的时候就存在。Default: null-->   
        <property name="preferredTestQuery">select id from test where id=1</property>   
           
        <!--用户修改系统配置参数执行前最多等待300秒。Default: 300 -->   
        <property name="propertyCycle">300</property>   
           
        <!--因性能消耗大请只在需要的时候使用它。如果设为true那么在每个connection提交的   
        时候都将校验其有效性。建议使用idleConnectionTestPeriod或automaticTestTable   
        等方法来提升连接测试的性能。Default: false -->   
        <property name="testConnectionOnCheckout">false</property>   
           
        <!--如果设为true那么在取得连接的同时将校验连接的有效性。Default: false -->   
        <property name="testConnectionOnCheckin">true</property>   
           
        <!--用户名。Default: null-->   
        <property name="user">root</property>   
           
        <!--早期的c3p0版本对JDBC接口采用动态反射代理。在早期版本用途广泛的情况下这个参数   
        允许用户恢复到动态反射代理以解决不稳定的故障。最新的非反射代理更快并且已经开始   
        广泛的被使用，所以这个参数未必有用。现在原先的动态反射与新的非反射代理同时受到   
        支持，但今后可能的版本可能不支持动态反射代理。Default: false-->   
        <property name="usesTraditionalReflectiveProxies">false</property> 
        </default-config>      
    </c3p0-config>
其中xml文件中的&要用字符实体，即&要用&amp;
```

### 2. dbcp.properties参数清单

```
1)数据库连接相关
    username="v10"
    password="v10"
    driverClassName="oracle.jdbc.driver.OracleDriver"
    url="jdbc:oracle:thin:@127.0.0.1:1521:cahs"
2)jndi相关
    name="jdbc/btdb1"
    type="javax.sql.DataSource"
    factory="org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory"
    factory默认是org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory，tomcat也允许采用其他连接实现，不过默认使用dbcp
3)连接数控制与连接归还策略
    maxActive="5"  
    maxIdle="3"  
    minIdle=”2”
    maxWait="5000" 
4)应对网络不稳定的策略
    testOnBorrow="true"
    validationQuery="select count(*) from bt_user" 
5)应对连接泄漏的策略
    removeAbandoned="true"
    removeAbandonedTimeout="60"
```

| 属性                       | 默认值 | 说明                                                         |
| -------------------------- | ------ | ------------------------------------------------------------ |
| initialSize                | 0      | 连接池启动时创建的初始化连接数量                             |
| maxActive                  | 8      | 连接池中可同时连接的最大的连接数                             |
| maxIdle                    | 8      | 连接池中最大的空闲的连接数，超过的空闲连接将被释放，如果设置为负数表示不限制 |
| minIdle                    | 0      | 连接池中最小的空闲的连接数，低于这个数量会被创建新的连接。该参数越接近maxIdle，性能越好，因为连接的创建和销毁，都是需要消耗资源的；但是不能太大。 |
| maxWait                    | 无限制 | 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，超过该时间限制会抛出异常，如果设置-1表示无限等待 |
| poolPreparedStatements     | false  | 开启池的Statement是否prepared                                |
| maxOpenPreparedStatements  | 无限制 | 开启池的prepared 后的同时最大连接数                          |
| minEvictableIdleTimeMillis |        | 连接池中连接，在时间段内一直空闲， 被逐出连接池的时间        |
| removeAbandonedTimeout     | 300    | 超过时间限制，回收没有用(废弃)的连接                         |
| removeAbandoned            | false  | 超过removeAbandonedTimeout时间后，是否进 行没用连接（废弃）的回收 |

### 3. druid.properties参数清单

```java
url=jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true
username=root
password=123456
driverClassName=com.mysql.cj.jdbc.Driver
    
initialSize=10
maxActive=20
maxWait=1000
filters=wall
```

| **配置**                      | **缺省** | **说明**                                                     |
| ----------------------------- | -------- | ------------------------------------------------------------ |
| name                          |          | 配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。   如果没有配置，将会生成一个名字，格式是：”DataSource-” +   System.identityHashCode(this) |
| url                           |          | 连接数据库的url，不同数据库不一样。例如：mysql :   jdbc:mysql://10.20.153.104:3306/druid2      oracle :   jdbc:oracle:thin:@10.20.149.85:1521:ocnauto |
| username                      |          | 连接数据库的用户名                                           |
| password                      |          | 连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用ConfigFilter。详细看这里：<https://github.com/alibaba/druid/wiki/%E4%BD%BF%E7%94%A8ConfigFilter> |
| driverClassName               |          | 根据url自动识别   这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName(建议配置下) |
| initialSize                   | 0        | 初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时 |
| maxActive                     | 8        | 最大连接池数量                                               |
| maxIdle                       | 8        | 已经不再使用，配置了也没效果                                 |
| minIdle                       |          | 最小连接池数量                                               |
| maxWait                       |          | 获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。 |
| poolPreparedStatements        | false    | 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。 |
| maxOpenPreparedStatements     | -1       | 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100 |
| validationQuery               |          | 用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。 |
| testOnBorrow                  | true     | 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 |
| testOnReturn                  | false    | 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 |
| testWhileIdle                 | false    | 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 |
| timeBetweenEvictionRunsMillis |          | 有两个含义： 1)Destroy线程会检测连接的间隔时间2)testWhileIdle的判断依据，详细看testWhileIdle属性的说明 |
| numTestsPerEvictionRun        |          | 不再使用，一个DruidDataSource只支持一个EvictionRun           |
| minEvictableIdleTimeMillis    |          |                                                              |
| connectionInitSqls            |          | 物理连接初始化的时候执行的sql                                |
| exceptionSorter               |          | 根据dbType自动识别   当数据库抛出一些不可恢复的异常时，抛弃连接 |
| filters                       |          | 属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：   监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall |
| proxyFilters                  |          | 类型是List，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系 |

# 16. Apache-DBUtils实现CRUD操作

​			commons-dbutils 是 Apache 组织提供的一个开源 JDBC工具类库，它是对JDBC的简单封装，学习成本极低

​			并且使用dbutils能极大简化jdbc编码的工作量，同时也不会影响程序的性能。

```
工具类：
	org.apache.commons.dbutils.DbUtils

API包说明：
	1. org.apache.commons.dbutils.QueryRunner
		QueryRunner提供数据操作的一系列重载的update()和query()操作
	2. org.apache.commons.dbutils.ResultSetHandler
		ResultSetHandler用于处理数据查询操作得到的结果集，不同结果情形由不同子类实现

```

## 	1. DbUtils工具类

```
DbUtils ：提供如关闭连接、装载JDBC驱动程序等常规工作的工具类，里面的所有方法都是静态的。主要方法如下：

public static void close(…) throws java.sql.SQLException：　

DbUtils类提供了三个重载的关闭方法

这些方法检查所提供的参数是不是NULL，如果不是的话，它们就关闭Connection、Statement和ResultSet

1. public static void closeQuietly(…): 
	这一类方法不仅能在Connection、Statement和ResultSet为NULL情况下避免关闭，还能隐藏一些在程序中抛出的SQLEeception。

2. public static void commitAndClose(Connection conn)throws SQLException 
	用来提交连接的事务，然后关闭连接

3. public static void commitAndCloseQuietly(Connection conn)： 
	用来提交连接的事务，然后关闭连接，并且在关闭连接时不抛出SQL异常。

4. public static void rollback(Connection conn)throws SQLException
		允许conn为null，因为方法内部做了判断

	public static void rollbackAndClose(Connection conn)throws SQLException

	rollbackAndCloseQuietly(Connection)

5. public static boolean loadDriver(java.lang.String driverClassName)：
	这一方装载并注册JDBC驱动程序，如果成功就返回true，
	使用该方法，你不需要捕捉这个异常ClassNotFoundException
```

## 2. QueryRunner类

```
该类封装了SQL的执行，是线程安全的

	1. 可以实现增、删、改、查、批处理

	2. 考虑了事务处理需要共用Connection。

	3. 该类最主要的就是简单化了SQL查询，它与ResultSetHandler组合在一起使用可以完成大部分的数据库操作，能够大大减少编码量

QueryRunner类提供了两个构造器：
  	1. QueryRunner()：默认的构造方法
  	2. QueryRunner(DataSource ds)：需要一个 javax.sql.DataSource 来作参数的构造方法

QueryRunner类的主要方法：
 	 1. 更新
  		 public int update(String sql, Object... params) throws SQLException
  			 更新操作：支持增删改。即insert、update、delete
		
	2. 批处理（具体现学现用，无代码演示）
    	public int[] batch(Connection conn,String sql,Object[][] params)throws SQLException： 
   	 		支持增、删、改语句，即insert、update、delete
	
 	 4.查询
		 public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException：
			查询一条记录的操作
		 public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException
		 	查询多条记录操作
```

## 3. ResultSetHandler接口及实现类

```
ResultSetHandler接口用于处理java.sql.ResultSet，将数据按要求转换为另一种形式

ResultSetHandler接口提供了一个单独的方法：Object handle (java.sql.ResultSet .rs)

接口的主要实现类：

  1. ArrayHandler：把结果集中的第一行数据转成对象数组，即将一条记录封装到一个Object数组中

  2. ArrayListHandler：把结果集中的每一行数据都转成一个数组，再存放到List中，将多条记录封装到一个装有Object数组的List集合中

  3. BeanHandler：将结果集中的第一行数据封装到一个对应的JavaBean实例中，即将一条记录封装到一个JavaBean中

  4. BeanListHandler：将结果集中的每一行数据都封装到一个对应的JavaBean实例中，存放到List里
  						即将多条记录封装到一个装有JavaBean的List集合中

  5. ColumnListHandler：将结果集中某一列的数据存放到List中，即将某列的值封装到List集合中
  						即将某列的值封装到List集合中

  6. KeyedHandler(name)：将结果集中的每一行数据都封装到一个Map里，再把这些map再存到一个map里，其key为指定的key
  				将一条记录封装到一个Map集合中。将多条记录封装到一个装有Map集合的Map集合中。而且外面的Map的key是可以指定的

  7. MapHandler：将结果集中的第一行数据封装到一个Map里，key是列名，value就是对应的值
  				即将一条记录封装到一个Map集合中，Map的key是列名，Map的value就是表中列的记录值

  8. MapListHandler：将结果集中的每一行数据都封装到一个Map里，然后再存放到List
  				即将多条记录封装到一个装有Map的List集合中

  9. ScalarHandler：查询单个值对象，即单值封装

```

# 17. 自定义数据库连接池

# 18. 数据库事务操作

```
相关API：
	setAutoCommit(false)：取消自动提交事务
	commit()：提交事务
	rollback()：回滚事务

事务可以理解为：事情要直接完成，不能半途而废！

原理如下：

数据一旦提交，就不可回滚。

数据什么时候意味着提交？
	当一个连接对象被创建时，默认情况下是自动提交事务
	每次执行一个 SQL 语句时，如果执行成功，就会向数据库自动提交，而不能回滚
	
	关闭数据库连接，数据就会自动的提交]
		如果多个操作，每个操作使用的是自己单独的连接，则无法保证事务，即同一个事务的多个操作必须在同一个连接下。

由于JDBC默认的事务行为是每执行一条DML语句,则自动提交一次

JDBC程序中为了让多个 SQL 语句作为一个事务执行，如下操作！
  	1. 调用 Connection 对象的 **setAutoCommit(false);：以取消自动提交事务
 	2. 在所有的 SQL 语句都成功执行后，调用commit();：方法提交事务
  	3. 在出现异常时，调用 **rollback();：方法回滚事务
  	

那在数据库操作事务是怎么样的呢？
	默认的MySQL行为是自动提交所有更改
	默认的Oracle行为是手动提交所有更改
```

# 19. 其余未涉及均现学现用，查API！

​				因为实际开发基本都是框架开发

# 20. 提供一个JDBC单例封装的高效工具包

​				这个工具包，无案例演示，自我解读源代码
