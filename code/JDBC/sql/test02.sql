-- 如果字段id被定义为AUTO_INCREMENT，在插入一行数据的时候，自增值的行为如下：
-- 1.如果插入数据时id字段指定为0、null或未指定值，那么就把这个表当前的AUTO_INCREMENT值填到自增字段
-- 2.如果插入数据时id字段指定了具体的值，就直接使用语句里指定的值

create database test02;
use test02;
create table account(
                        id int primary key auto_increment,
                        name varchar(20),
                        money double
);
insert into account values (null,'aaa',10000);
insert into account values (null,'bbb',10000);
insert into account values (null,'ccc',10000);