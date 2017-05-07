/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.0.20a-nt : Database - fs_data_platform_branch
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fs_data_platform_branch` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `fs_data_platform_branch`;

/*Table structure for table `fs_base_server` */

DROP TABLE IF EXISTS `fs_base_server`;

CREATE TABLE `fs_base_server` (
  `id` int(11) NOT NULL auto_increment,
  `zoneName` varchar(50) NOT NULL COMMENT '大区名称',
  `zonePath` varchar(50) default NULL COMMENT '大区路径',
  `serverIp` varchar(50) NOT NULL,
  `port` varchar(10) NOT NULL,
  `intranetIp` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `logger_record` */

DROP TABLE IF EXISTS `logger_record`;

CREATE TABLE `logger_record` (
  `id` bigint(20) NOT NULL auto_increment,
  `info` mediumtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_account` */

DROP TABLE IF EXISTS `mt_account`;

CREATE TABLE `mt_account` (
  `accountId` varchar(255) NOT NULL,
  `openAccount` varchar(255) default NULL,
  `password` varchar(255) default NULL,
  `oid` varchar(255) default NULL,
  `binded` tinyint(1) default '0',
  `channelId` varchar(255) default NULL,
  `pub` varchar(255) default NULL,
  `lastLoginTime` bigint(20) default NULL,
  `registerTime` bigint(20) default NULL,
  `imei` varchar(255) default NULL,
  `userZoneInfoList` longtext,
  `lastLoginList` longtext,
  `iphoneLastLoginList` longtext,
  `userZoneInfoMap` longtext,
  PRIMARY KEY  (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_account_login_record` */

DROP TABLE IF EXISTS `mt_account_login_record`;

CREATE TABLE `mt_account_login_record` (
  `accountId` varchar(255) NOT NULL,
  `userId` varchar(128) default NULL,
  `zoneId` int(7) default NULL,
  `loginTime` bigint(20) default NULL,
  PRIMARY KEY  (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `mt_platform_notice` */

DROP TABLE IF EXISTS `mt_platform_notice`;

CREATE TABLE `mt_platform_notice` (
  `id` int(11) NOT NULL auto_increment,
  `title` varchar(255) default NULL,
  `content` mediumtext,
  `startTime` bigint(20) default NULL,
  `endTime` bigint(20) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `mt_platform_whitelist` */

DROP TABLE IF EXISTS `mt_platform_whitelist`;

CREATE TABLE `mt_platform_whitelist` (
  `accountId` varchar(255) NOT NULL,
  `isClose` tinyint(1) default NULL,
  PRIMARY KEY  (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `mt_zone_info` */

DROP TABLE IF EXISTS `mt_zone_info`;

CREATE TABLE `mt_zone_info` (
  `id` int(11) NOT NULL auto_increment,
  `zoneId` int(4) NOT NULL COMMENT '大区id',
  `zoneName` varchar(50) NOT NULL COMMENT '大区名称',
  `zonePath` varchar(50) default NULL COMMENT '大区路径',
  `subZone` varchar(10) NOT NULL,
  `isSubZone` int(1) default NULL,
  `enabled` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `channelId` varchar(50) NOT NULL,
  `recommand` int(1) NOT NULL,
  `serverIp` varchar(50) NOT NULL,
  `port` varchar(10) NOT NULL,
  `intranetIp` varchar(50) NOT NULL,
  `openTime` varchar(10) NOT NULL,
  `gmPort` varchar(10) NOT NULL,
  `chargePort` int(10) NOT NULL default '10000',
  `db_ip` varchar(50) default NULL,
  `db_port` varchar(10) default '0',
  `db_name` varchar(50) default NULL,
  `chargeOpen` int(1) default '0',
  `giftCodeServerIp` varchar(50) default NULL,
  `giftCodeServerPort` int(10) default NULL,
  `closeTips` mediumtext,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_zone_info_copy` */

DROP TABLE IF EXISTS `mt_zone_info_copy`;

CREATE TABLE `mt_zone_info_copy` (
  `id` int(11) NOT NULL auto_increment,
  `zoneId` int(4) NOT NULL COMMENT '大区id',
  `zoneName` varchar(50) NOT NULL COMMENT '大区名称',
  `zonePath` varchar(50) default NULL COMMENT '大区路径',
  `subZone` varchar(10) NOT NULL,
  `isSubZone` int(1) default NULL,
  `enabled` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `channelId` varchar(50) NOT NULL,
  `recommand` int(1) NOT NULL,
  `serverIp` varchar(50) NOT NULL,
  `port` varchar(10) NOT NULL,
  `intranetIp` varchar(50) NOT NULL,
  `openTime` varchar(10) NOT NULL,
  `gmPort` varchar(10) NOT NULL,
  `chargePort` int(10) NOT NULL default '10000',
  `db_ip` varchar(50) default NULL,
  `db_port` varchar(10) default '0',
  `db_name` varchar(50) default NULL,
  `chargeOpen` int(1) default '0',
  `giftCodeServerIp` varchar(50) default NULL,
  `giftCodeServerPort` int(10) default NULL,
  `closeTips` mediumtext,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata` */

DROP TABLE IF EXISTS `table_kvdata`;

CREATE TABLE `table_kvdata` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(64) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `dbkey` USING BTREE (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
