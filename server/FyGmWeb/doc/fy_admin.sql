-- MySQL dump 10.10
--
-- Host: localhost    Database: fy_admin
-- ------------------------------------------------------
-- Server version	5.0.20a-nt

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
-- Table structure for table `action_auths_cfg`
--

DROP TABLE IF EXISTS `action_auths_cfg`;
CREATE TABLE `action_auths_cfg` (
  `name` varchar(50) NOT NULL,
  `roles` varchar(50) default NULL,
  `desc` varchar(50) default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `action_auths_cfg`
--


/*!40000 ALTER TABLE `action_auths_cfg` DISABLE KEYS */;
LOCK TABLES `action_auths_cfg` WRITE;
INSERT INTO `action_auths_cfg` VALUES ('a_update','0','权限分配'),('a_list','0,1','权限分配'),('cpwd','0,1,2,3','修改密码'),('_delete','0','用户管理'),('_edit','0','用户管理'),('_tedit','0','用户管理'),('_add','0','用户管理'),('_list','0,1','用户管理'),('readLogFile','0','后台操作记录'),('getLogList','0','后台操作记录');
UNLOCK TABLES;
/*!40000 ALTER TABLE `action_auths_cfg` ENABLE KEYS */;

--
-- Table structure for table `admin_user`
--

DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `enable` int(11) NOT NULL,
  `roles` varchar(100) NOT NULL,
  `zoneId` varchar(128) default '0',
  `channel` varchar(128) default NULL,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `admin_user`
--


/*!40000 ALTER TABLE `admin_user` DISABLE KEYS */;
LOCK TABLES `admin_user` WRITE;
INSERT INTO `admin_user` VALUES ('allen','55B29D062B223C9CBB41BD0773B06973',1,'0','0','10001,DT001'),('ben','BD03136CA15B6F38202BFB38468A75DA',1,'2',NULL,NULL),('benson','473B6487871FC5FBC3E79C1C40A9E1FD',1,'0',NULL,'10001,360,DT001,YDMM'),('bill_zone0','E94924B7DFC5D9F19AE8045613D02C3C',1,'0','0','10001,DT001'),('dklion','7050C99C849F135006FC9876B1E5821F',1,'1',NULL,NULL),('dominic','03622FCCFF72886710ADF9A711C56757',1,'0','0,1,2,3,5,4','10001'),('DT002','5333A3C8270B60151E2C9B2F7B883539',1,'4','2','DT001'),('jerry','461EE07D57E0B2173B3D7B2C66492622',1,'0','0,1,2,3','10001,DT001'),('scott','586787455AFAA27A1754B6F85EE501ED',1,'0','0','10001,360,DT001,YDMM'),('sun','09BC09B9D1B9A99524C13A89C04D7209',1,'0','0,1,2,3,5,4','10001,DT001'),('sunny','238985812894153487577310B166F60A',1,'0','0','10001,DT001'),('water','9BB66F6C0F72019D378FA93D43EA2915',1,'0',NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `admin_user` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

