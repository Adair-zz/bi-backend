# database init
# @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>

-- create database
create database if not exists bi_db;

use bi_db;

-- user table
create table if not exists user
(
    id           bigint                                 auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment 'account',
    userPassword varchar(512)                           not null comment 'password',
    userName     varchar(256)                           null comment 'user name',
    userAvatar   varchar(1024)                          null comment 'user avatar',
    userRole     varchar(256) default 'user'            not null comment 'user role: user/admin',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete     tinyint      default 0                 not null comment 'is delete',
    index idx_userAccount (userAccount)
) comment 'user' collate = utf8mb4_unicode_ci;

-- chart table
create table if not exists chart
(
    id           bigint                           auto_increment comment 'id' primary key,
    goal		 text                                       null comment 'goal',
    `name`       varchar(128)                               null comment 'chart name',
    chartData    text                                       null comment 'chart raw data',
    chartType	 varchar(128)                               null comment 'chart type',
    genChart	 text	                                    null comment 'generated chart',
    genResult	 text	                                    null comment 'generated result',
    status       varchar(128)                           not null default 'wait' comment 'wait,running,succeed,failed',
    execMessage  text                                       null comment 'executive message',
    userId       bigint                                     null comment 'user id',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'create time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update time',
    isDelete     tinyint      default 0                 not null comment 'is delete'
) comment 'chart' collate = utf8mb4_unicode_ci;
