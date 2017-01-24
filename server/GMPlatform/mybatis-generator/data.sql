/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.5.24 : Database - fs_gm_mt
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fs_gm_mt` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `fs_gm_mt`;

/*Table structure for table `role_permissions` */

DROP TABLE IF EXISTS `role_permissions`;

CREATE TABLE `role_permissions` (
  `permission` char(10) NOT NULL COMMENT '权限',
  `role_name` char(10) DEFAULT NULL COMMENT '角色名',
  PRIMARY KEY (`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `role_permissions` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `account` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `lastlogintime` datetime DEFAULT NULL COMMENT '上次登录时间',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`account`,`password`,`lastlogintime`,`createTime`) values ('1111','d17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2','2017-01-24 09:57:05','2017-01-19 15:28:05'),('1112','d17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2','2017-01-19 15:41:30','2017-01-19 15:41:30'),('1113','d17f25ecfbcc7857f7bebea469308be0b2580943e96d13a3ad98a13675c4bfc2','2017-01-19 15:42:16','2017-01-19 15:42:16');

/*Table structure for table `user_roles` */

DROP TABLE IF EXISTS `user_roles`;

CREATE TABLE `user_roles` (
  `account` char(30) NOT NULL COMMENT '用户名',
  `roles` mediumtext COMMENT '角色列表',
  PRIMARY KEY (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_roles` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
