-- 如果字段id被定义为AUTO_INCREMENT，在插入一行数据的时候，自增值的行为如下：
-- 1.如果插入数据时id字段指定为0、null或未指定值，那么就把这个表当前的AUTO_INCREMENT值填到自增字段
-- 2.如果插入数据时id字段指定了具体的值，就直接使用语句里指定的值

create database test01;
use test01;
create table user(
                     id int primary key auto_increment,
                     username varchar(20),
                     password varchar(20),
                     nickname varchar(20),
                     age int
);
insert into user values (null,'aaa','123','小丽',34);
insert into user values (null,'bbb','123','大王',32);
insert into user values (null,'ccc','123','小明',28);
insert into user values (null,'ddd','123','大黄',21);