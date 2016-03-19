-- MySQL dump 10.13  Distrib 5.1.73, for redhat-linux-gnu (x86_64)
--
-- Host: 10.66.103.224    Database: yh_dzfs_01
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
-- Table structure for table `arena_data`
--

DROP TABLE IF EXISTS `arena_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_data` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arena_info`
--

DROP TABLE IF EXISTS `arena_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_info` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arena_record`
--

DROP TABLE IF EXISTS `arena_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_record` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `battle_tower_data`
--

DROP TABLE IF EXISTS `battle_tower_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `battle_tower_data` (
  `dbkey` varchar(255) NOT NULL COMMENT '用户Id',
  `dbvalue` longtext NOT NULL COMMENT '试练塔记录数据',
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `battle_tower_rank`
--

DROP TABLE IF EXISTS `battle_tower_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `battle_tower_rank` (
  `dbkey` varchar(255) NOT NULL COMMENT '用户的Id',
  `dbvalue` longtext NOT NULL COMMENT '个人试练塔历史中最高层的详细信息',
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔个人最高层数据记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `battle_tower_strategy`
--

DROP TABLE IF EXISTS `battle_tower_strategy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `battle_tower_strategy` (
  `dbkey` varchar(255) NOT NULL COMMENT '试练塔的里程碑Id',
  `dbvalue` longtext NOT NULL COMMENT '试练塔某个里程碑中缓存的攻略信息',
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔攻略信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy`
--

DROP TABLE IF EXISTS `copy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy_level_record`
--

DROP TABLE IF EXISTS `copy_level_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy_level_record` (
  `id` varchar(128) NOT NULL,
  `levelId` int(11) DEFAULT NULL,
  `userId` varchar(128) NOT NULL,
  `passStar` int(1) NOT NULL DEFAULT '0',
  `currentCount` int(3) NOT NULL DEFAULT '0',
  `buyCount` int(3) NOT NULL DEFAULT '0',
  `isFirst` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `copy_map_record`
--

DROP TABLE IF EXISTS `copy_map_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `copy_map_record` (
  `id` varchar(128) NOT NULL,
  `mapId` int(11) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `giftStates` varchar(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `daily_gif_info`
--

DROP TABLE IF EXISTS `daily_gif_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `daily_gif_info` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `drop_record`
--

DROP TABLE IF EXISTS `drop_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `drop_record` (
  `dbKey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY (`dbKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equip_item`
--

DROP TABLE IF EXISTS `equip_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `equip_item` (
  `id` varchar(64) NOT NULL,
  `ownerId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fashion_item`
--

DROP TABLE IF EXISTS `fashion_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fashion_item` (
  `id` varchar(128) NOT NULL,
  `type` int(2) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `state` int(2) NOT NULL DEFAULT '0',
  `buyTime` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fresheractivity`
--

DROP TABLE IF EXISTS `fresheractivity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fresheractivity` (
  `id` varchar(255) NOT NULL,
  `ownerId` varchar(255) NOT NULL,
  `extention` text,
  PRIMARY KEY (`id`),
  KEY `ownerId` (`ownerId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `game_world`
--

DROP TABLE IF EXISTS `game_world`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `game_world` (
  `dbkey` varchar(128) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `globaldata`
--

DROP TABLE IF EXISTS `globaldata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `globaldata` (
  `serverId` int(11) NOT NULL AUTO_INCREMENT,
  `gulidIndex` int(10) DEFAULT NULL,
  PRIMARY KEY (`serverId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild`
--

DROP TABLE IF EXISTS `guild`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guild_userinfo`
--

DROP TABLE IF EXISTS `guild_userinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guild_userinfo` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `inlay_item`
--

DROP TABLE IF EXISTS `inlay_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `inlay_item` (
  `id` varchar(64) NOT NULL,
  `ownerId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `id` varchar(64) NOT NULL DEFAULT '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) DEFAULT NULL COMMENT '道具模版Id',
  `count` int(4) DEFAULT NULL COMMENT '道具数量',
  `userId` varchar(64) DEFAULT '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) DEFAULT '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=6051886 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `login_log`
--

DROP TABLE IF EXISTS `login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` varchar(128) DEFAULT NULL,
  `account` varchar(128) DEFAULT NULL,
  `loginTime` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `magic`
--

DROP TABLE IF EXISTS `magic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `magic` (
  `id` varchar(64) NOT NULL DEFAULT '' COMMENT '佣兵的Id',
  `magicId` varchar(64) DEFAULT '' COMMENT '佣兵法宝的Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `map`
--

DROP TABLE IF EXISTS `map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `map` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_copy_data`
--

DROP TABLE IF EXISTS `mt_copy_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_copy_data` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_copylevel_data`
--

DROP TABLE IF EXISTS `mt_copylevel_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_copylevel_data` (
  `dbKey` varchar(255) NOT NULL,
  `dbValue` longtext NOT NULL,
  PRIMARY KEY (`dbKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_hero`
--

DROP TABLE IF EXISTS `mt_hero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_hero` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_privilege_user_info`
--

DROP TABLE IF EXISTS `mt_privilege_user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_privilege_user_info` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_sign_data`
--

DROP TABLE IF EXISTS `mt_sign_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_sign_data` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_daily_activity`
--

DROP TABLE IF EXISTS `mt_table_daily_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_daily_activity` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_friend`
--

DROP TABLE IF EXISTS `mt_table_friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_friend` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_gamble`
--

DROP TABLE IF EXISTS `mt_table_gamble`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_gamble` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_guide`
--

DROP TABLE IF EXISTS `mt_table_guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_guide` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_hotpoint`
--

DROP TABLE IF EXISTS `mt_table_hotpoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_hotpoint` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_item_bag`
--

DROP TABLE IF EXISTS `mt_table_item_bag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_item_bag` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_ranking`
--

DROP TABLE IF EXISTS `mt_table_ranking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_ranking` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_setting`
--

DROP TABLE IF EXISTS `mt_table_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_setting` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_store`
--

DROP TABLE IF EXISTS `mt_table_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_store` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_task`
--

DROP TABLE IF EXISTS `mt_table_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_task` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_vip`
--

DROP TABLE IF EXISTS `mt_table_vip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_vip` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_table_worship`
--

DROP TABLE IF EXISTS `mt_table_worship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_table_worship` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_unique_name`
--

DROP TABLE IF EXISTS `mt_unique_name`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_unique_name` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_user_chat`
--

DROP TABLE IF EXISTS `mt_user_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_user_chat` (
  `dbkey` varchar(255) NOT NULL DEFAULT '' COMMENT '角色Id',
  `dbvalue` longtext NOT NULL COMMENT '私聊和秘境分享信息',
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='私聊和秘境分享';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_user_email`
--

DROP TABLE IF EXISTS `mt_user_email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_user_email` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_user_hero`
--

DROP TABLE IF EXISTS `mt_user_hero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_user_hero` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mt_user_purse`
--

DROP TABLE IF EXISTS `mt_user_purse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mt_user_purse` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peak_arena_data`
--

DROP TABLE IF EXISTS `peak_arena_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peak_arena_data` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peak_arena_info`
--

DROP TABLE IF EXISTS `peak_arena_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peak_arena_info` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `peak_arena_record`
--

DROP TABLE IF EXISTS `peak_arena_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peak_arena_record` (
  `dbkey` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ranking`
--

DROP TABLE IF EXISTS `ranking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ranking` (
  `ranking_sequence` bigint(20) NOT NULL COMMENT '排行榜primary_key的唯一性约束在内存控制',
  `primary_key` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL COMMENT '排行榜数据库是批量操作，因为唯一性约束在内存控制',
  `conditions` varchar(255) NOT NULL,
  `extension` text NOT NULL COMMENT '排行榜数据库是批量操作，因为唯一性约束在内存控制',
  PRIMARY KEY (`ranking_sequence`),
  KEY `type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ranking_swap`
--

DROP TABLE IF EXISTS `ranking_swap`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ranking_swap` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `primary_key` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL,
  `ranking` int(11) NOT NULL,
  `extension` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `type` (`type`,`primary_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=544040 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rolebaseinfo`
--

DROP TABLE IF EXISTS `rolebaseinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rolebaseinfo` (
  `dbkey` varchar(64) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea`
--

DROP TABLE IF EXISTS `secretarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea_battleinfo`
--

DROP TABLE IF EXISTS `secretarea_battleinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea_battleinfo` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea_def_record`
--

DROP TABLE IF EXISTS `secretarea_def_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea_def_record` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) DEFAULT NULL,
  `secretId` varchar(64) DEFAULT NULL,
  `secretType` varchar(32) DEFAULT NULL,
  `attrackUserId` varchar(64) DEFAULT NULL,
  `guildName` varchar(32) DEFAULT NULL,
  `regiondName` varchar(32) DEFAULT NULL,
  `attrackUserName` varchar(32) DEFAULT NULL,
  `attrackTime` varchar(64) DEFAULT NULL,
  `attrackCount` int(32) DEFAULT NULL,
  `isWin` int(32) DEFAULT NULL,
  `keyNum` int(32) DEFAULT NULL,
  `sourceNumList` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea_info`
--

DROP TABLE IF EXISTS `secretarea_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea_info` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea_user_record`
--

DROP TABLE IF EXISTS `secretarea_user_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea_user_record` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) DEFAULT NULL,
  `secretType` varchar(32) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `getGiftTime` varchar(64) DEFAULT NULL,
  `sourceNumList` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretarea_userinfo`
--

DROP TABLE IF EXISTS `secretarea_userinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretarea_userinfo` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `secretmember`
--

DROP TABLE IF EXISTS `secretmember`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secretmember` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `server_data`
--

DROP TABLE IF EXISTS `server_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `server_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seven_day_gifinfo`
--

DROP TABLE IF EXISTS `seven_day_gifinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seven_day_gifinfo` (
  `dbkey` varchar(50) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skill_item`
--

DROP TABLE IF EXISTS `skill_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skill_item` (
  `id` varchar(64) NOT NULL,
  `ownerId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skill_item2`
--

DROP TABLE IF EXISTS `skill_item2`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skill_item2` (
  `id` varchar(64) NOT NULL,
  `ownerId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `task_item`
--

DROP TABLE IF EXISTS `task_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `task_item` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tower_data`
--

DROP TABLE IF EXISTS `tower_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tower_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unending_war`
--

DROP TABLE IF EXISTS `unending_war`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unending_war` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `userId` varchar(128) NOT NULL,
  `zoneId` int(7) DEFAULT NULL,
  `mapId` int(7) DEFAULT NULL,
  `userName` varchar(32) DEFAULT '',
  `vip` int(4) DEFAULT NULL,
  `sex` int(2) DEFAULT NULL,
  `exp` bigint(11) DEFAULT NULL,
  `level` int(4) DEFAULT NULL,
  `account` varchar(128) DEFAULT NULL,
  `career` int(7) DEFAULT NULL,
  `headImage` varchar(128) DEFAULT '',
  `createTime` bigint(20) DEFAULT '0',
  `lastLoginTime` bigint(20) DEFAULT '0',
  `kickOffCoolTime` bigint(20) DEFAULT NULL,
  `zoneRegInfo` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `userName` (`userName`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_army_data`
--

DROP TABLE IF EXISTS `user_army_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_army_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_guide_progress`
--

DROP TABLE IF EXISTS `user_guide_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_guide_progress` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_identifier`
--

DROP TABLE IF EXISTS `user_identifier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_identifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11410 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_other`
--

DROP TABLE IF EXISTS `user_other`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_other` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_plot_progress`
--

DROP TABLE IF EXISTS `user_plot_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_plot_progress` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'yh_dzfs_01'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-22 18:16:34
