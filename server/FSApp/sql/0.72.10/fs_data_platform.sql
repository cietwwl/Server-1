-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: 10.66.103.224    Database: yh_dzfs_login
-- ------------------------------------------------------
-- Server version	5.5.24-CDB-2.0.0-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `fs_base_server`
--

DROP TABLE IF EXISTS `fs_base_server`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fs_base_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `zoneName` varchar(50) NOT NULL COMMENT '大区名称',
  `zonePath` varchar(50) DEFAULT NULL COMMENT '大区路径',
  `serverIp` varchar(50) NOT NULL,
  `port` varchar(10) NOT NULL,
  `intranetIp` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `logger_record`
--

DROP TABLE IF EXISTS `logger_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logger_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `info` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30366 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_account`
--

DROP TABLE IF EXISTS `mt_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_account` (
  `accountId` varchar(255) NOT NULL,
  `openAccount` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `oid` varchar(255) DEFAULT NULL,
  `binded` tinyint(1) DEFAULT '0',
  `channelId` varchar(255) DEFAULT NULL,
  `pub` varchar(255) DEFAULT NULL,
  `lastLoginTime` bigint(20) DEFAULT NULL,
  `registerTime` bigint(20) DEFAULT NULL,
  `imei` varchar(255) DEFAULT NULL,
  `userZoneInfoList` longtext,
  `lastLoginList` longtext,
  `iphoneLastLoginList` longtext,
  `userZoneInfoMap` longtext,
  PRIMARY KEY (`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_zone_info`
--

DROP TABLE IF EXISTS `mt_zone_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_zone_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `zoneId` int(4) NOT NULL COMMENT '大区id',
  `zoneName` varchar(50) NOT NULL COMMENT '大区名称',
  `zonePath` varchar(50) DEFAULT NULL COMMENT '大区路径',
  `subZone` varchar(10) NOT NULL,
  `isSubZone` int(1) DEFAULT NULL,
  `enabled` int(1) NOT NULL,
  `status` int(1) NOT NULL,
  `channelId` varchar(50) NOT NULL,
  `recommand` int(1) NOT NULL,
  `serverIp` varchar(50) NOT NULL,
  `port` varchar(10) NOT NULL,
  `intranetIp` varchar(50) NOT NULL,
  `openTime` varchar(10) NOT NULL,
  `gmPort` varchar(10) NOT NULL,
  `db_ip` varchar(50) DEFAULT NULL,
  `db_port` varchar(10) DEFAULT '0',
  `db_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'yh_dzfs_login'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-23 11:53:32
