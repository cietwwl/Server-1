/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.0.20a-nt : Database - fs_data_mt
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`fs_data_mt` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `fs_data_mt`;

/*Table structure for table `activity_counttype_item` */

DROP TABLE IF EXISTS `activity_counttype_item`;

CREATE TABLE `activity_counttype_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_daily_charge_item` */

DROP TABLE IF EXISTS `activity_daily_charge_item`;

CREATE TABLE `activity_daily_charge_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `activity_dailycounttype_item` */

DROP TABLE IF EXISTS `activity_dailycounttype_item`;

CREATE TABLE `activity_dailycounttype_item` (
  `id` varchar(128) NOT NULL default '',
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_dailydiscounttype_item` */

DROP TABLE IF EXISTS `activity_dailydiscounttype_item`;

CREATE TABLE `activity_dailydiscounttype_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `activity_exchange_item` */

DROP TABLE IF EXISTS `activity_exchange_item`;

CREATE TABLE `activity_exchange_item` (
  `id` varchar(128) NOT NULL default '',
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `activity_fortunecattype_item` */

DROP TABLE IF EXISTS `activity_fortunecattype_item`;

CREATE TABLE `activity_fortunecattype_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_limitherotype_item` */

DROP TABLE IF EXISTS `activity_limitherotype_item`;

CREATE TABLE `activity_limitherotype_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_ranktype_item` */

DROP TABLE IF EXISTS `activity_ranktype_item`;

CREATE TABLE `activity_ranktype_item` (
  `id` varchar(128) NOT NULL default '',
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `activity_ratetype_item` */

DROP TABLE IF EXISTS `activity_ratetype_item`;

CREATE TABLE `activity_ratetype_item` (
  `id` varchar(128) NOT NULL default '',
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_redenvelope_item` */

DROP TABLE IF EXISTS `activity_redenvelope_item`;

CREATE TABLE `activity_redenvelope_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `activity_retrievetype_item` */

DROP TABLE IF EXISTS `activity_retrievetype_item`;

CREATE TABLE `activity_retrievetype_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_time_count_type_item` */

DROP TABLE IF EXISTS `activity_time_count_type_item`;

CREATE TABLE `activity_time_count_type_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_timecard_item` */

DROP TABLE IF EXISTS `activity_timecard_item`;

CREATE TABLE `activity_timecard_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `activity_vitalitytype_item` */

DROP TABLE IF EXISTS `activity_vitalitytype_item`;

CREATE TABLE `activity_vitalitytype_item` (
  `id` varchar(128) NOT NULL default '',
  `userId` varchar(128) default NULL,
  `extention` varchar(4096) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `angel_array_data` */

DROP TABLE IF EXISTS `angel_array_data`;

CREATE TABLE `angel_array_data` (
  `userId` varchar(40) NOT NULL default '' COMMENT '角色Id',
  `maxFloor` tinyint(3) default NULL COMMENT '历史最高的层数',
  `curFloor` tinyint(3) default NULL COMMENT '当前挑战的层数',
  `resetTime` bigint(21) default '0' COMMENT '重置匹配数据时间',
  `resetTimes` tinyint(3) default NULL COMMENT '已经使用的重置次数',
  `resetFighting` int(11) default NULL COMMENT '重置时的战斗力',
  `resetLevel` smallint(5) default NULL COMMENT '重置时的等级',
  `resetRankIndex` smallint(5) default NULL COMMENT '重置时的竞技场排名',
  `heroChange` text COMMENT '当前上阵阵容的血量变化',
  `curFloorState` tinyint(2) default NULL COMMENT '当前的关卡状态',
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `angel_array_enemy_data` */

DROP TABLE IF EXISTS `angel_array_enemy_data`;

CREATE TABLE `angel_array_enemy_data` (
  `id` varchar(64) NOT NULL COMMENT '记录的Id:userId_floor',
  `userId` varchar(64) default NULL COMMENT '角色Id',
  `floor` int(3) default NULL COMMENT '层数',
  `teamInfo` text COMMENT '阵容信息',
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `angel_array_enemy_info` */

DROP TABLE IF EXISTS `angel_array_enemy_info`;

CREATE TABLE `angel_array_enemy_info` (
  `id` varchar(64) NOT NULL default '' COMMENT '记录的唯一Id',
  `userId` varchar(64) default '' COMMENT '数据的角色名字',
  `floor` int(3) default '0' COMMENT '层数',
  `enemyChange` text COMMENT '敌人血量变化信息',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `angel_array_team_info` */

DROP TABLE IF EXISTS `angel_array_team_info`;

CREATE TABLE `angel_array_team_info` (
  `userId` varchar(64) NOT NULL default '' COMMENT '记录所属角色Id',
  `minFloor` int(3) default '0' COMMENT '可以被随机到的下限',
  `maxFloor` int(3) default '0' COMMENT '可以被随机到的上限',
  `minFighting` int(21) default '0' COMMENT '战力区间下限',
  `maxFighting` int(21) default '0' COMMENT '战力区间上限',
  `teamGroupId` varchar(3) default '0' COMMENT '约束归属那个MapItemStore',
  `teamInfo` text COMMENT '角色的阵容信息',
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arena_data` */

DROP TABLE IF EXISTS `arena_data`;

CREATE TABLE `arena_data` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arena_info` */

DROP TABLE IF EXISTS `arena_info`;

CREATE TABLE `arena_info` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arena_record` */

DROP TABLE IF EXISTS `arena_record`;

CREATE TABLE `arena_record` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `arena_robot` */

DROP TABLE IF EXISTS `arena_robot`;

CREATE TABLE `arena_robot` (
  `dbkey` varchar(255) NOT NULL default '',
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `battle_tower_data` */

DROP TABLE IF EXISTS `battle_tower_data`;

CREATE TABLE `battle_tower_data` (
  `dbkey` varchar(255) NOT NULL COMMENT '用户Id',
  `dbvalue` longtext NOT NULL COMMENT '试练塔记录数据',
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔数据';

/*Table structure for table `battle_tower_rank` */

DROP TABLE IF EXISTS `battle_tower_rank`;

CREATE TABLE `battle_tower_rank` (
  `dbkey` varchar(255) NOT NULL COMMENT '用户的Id',
  `dbvalue` longtext NOT NULL COMMENT '个人试练塔历史中最高层的详细信息',
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔个人最高层数据记录';

/*Table structure for table `battle_tower_strategy` */

DROP TABLE IF EXISTS `battle_tower_strategy`;

CREATE TABLE `battle_tower_strategy` (
  `dbkey` varchar(255) NOT NULL COMMENT '试练塔的里程碑Id',
  `dbvalue` longtext NOT NULL COMMENT '试练塔某个里程碑中缓存的攻略信息',
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试练塔攻略信息';

/*Table structure for table `bilog_stat` */

DROP TABLE IF EXISTS `bilog_stat`;

CREATE TABLE `bilog_stat` (
  `id` bigint(20) NOT NULL auto_increment,
  `date` date NOT NULL,
  `type` varchar(20) NOT NULL,
  `count` int(11) NOT NULL,
  `update_time` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `idx_date_type` USING BTREE (`date`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `charge_info` */

DROP TABLE IF EXISTS `charge_info`;

CREATE TABLE `charge_info` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `charge_order_identifier` */

DROP TABLE IF EXISTS `charge_order_identifier`;

CREATE TABLE `charge_order_identifier` (
  `id` bigint(11) NOT NULL auto_increment COMMENT '自增Id',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `charge_record` */

DROP TABLE IF EXISTS `charge_record`;

CREATE TABLE `charge_record` (
  `id` bigint(20) NOT NULL auto_increment COMMENT '自增长的id',
  `user_id` varchar(64) default NULL COMMENT '游戏的userId',
  `sdk_user_id` varchar(64) NOT NULL COMMENT 'sdk的用户编号',
  `trade_no` varchar(128) NOT NULL COMMENT '交易的订单号',
  `money` int(10) default NULL COMMENT '购买金额',
  `currency_type` varchar(10) default NULL COMMENT '货币单位',
  `channel_id` varchar(64) default NULL COMMENT '购买的客户端的渠道编号',
  `item_id` varchar(128) default NULL COMMENT '购买的物品编号',
  `charge_time` timestamp NULL default NULL COMMENT '充值的时间',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `TRADE_NO` (`trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy` */

DROP TABLE IF EXISTS `copy`;

CREATE TABLE `copy` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record00` */

DROP TABLE IF EXISTS `copy_level_record00`;

CREATE TABLE `copy_level_record00` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record01` */

DROP TABLE IF EXISTS `copy_level_record01`;

CREATE TABLE `copy_level_record01` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record02` */

DROP TABLE IF EXISTS `copy_level_record02`;

CREATE TABLE `copy_level_record02` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record03` */

DROP TABLE IF EXISTS `copy_level_record03`;

CREATE TABLE `copy_level_record03` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record04` */

DROP TABLE IF EXISTS `copy_level_record04`;

CREATE TABLE `copy_level_record04` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record05` */

DROP TABLE IF EXISTS `copy_level_record05`;

CREATE TABLE `copy_level_record05` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record06` */

DROP TABLE IF EXISTS `copy_level_record06`;

CREATE TABLE `copy_level_record06` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record07` */

DROP TABLE IF EXISTS `copy_level_record07`;

CREATE TABLE `copy_level_record07` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record08` */

DROP TABLE IF EXISTS `copy_level_record08`;

CREATE TABLE `copy_level_record08` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_level_record09` */

DROP TABLE IF EXISTS `copy_level_record09`;

CREATE TABLE `copy_level_record09` (
  `id` varchar(128) NOT NULL default '',
  `levelId` int(11) default NULL,
  `userId` varchar(128) NOT NULL default '',
  `passStar` int(1) NOT NULL default '0',
  `currentCount` int(3) NOT NULL default '0',
  `buyCount` int(3) NOT NULL default '0',
  `isFirst` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `copy_map_record` */

DROP TABLE IF EXISTS `copy_map_record`;

CREATE TABLE `copy_map_record` (
  `id` varchar(128) NOT NULL,
  `mapId` int(11) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `giftStates` varchar(5) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `daily_gif_info` */

DROP TABLE IF EXISTS `daily_gif_info`;

CREATE TABLE `daily_gif_info` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `drop_record` */

DROP TABLE IF EXISTS `drop_record`;

CREATE TABLE `drop_record` (
  `dbKey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `embattle_pos` */

DROP TABLE IF EXISTS `embattle_pos`;

CREATE TABLE `embattle_pos` (
  `id` varchar(64) NOT NULL COMMENT 'Id',
  `userId` varchar(64) NOT NULL COMMENT '角色Id',
  `type` tinyint(3) default NULL COMMENT '类型',
  `posInfo` text COMMENT '阵容信息',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `fashion_being_using` */

DROP TABLE IF EXISTS `fashion_being_using`;

CREATE TABLE `fashion_being_using` (
  `userId` varchar(64) character set utf8 NOT NULL,
  `wingId` int(12) default NULL,
  `suitId` int(12) default NULL,
  `petId` int(12) default NULL,
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

/*Table structure for table `fashion_brought_items` */

DROP TABLE IF EXISTS `fashion_brought_items`;

CREATE TABLE `fashion_brought_items` (
  `id` varchar(64) character set utf8 NOT NULL,
  `fashionId` int(12) default NULL,
  `userId` varchar(64) character set utf8 default NULL,
  `buyTime` bigint(20) default NULL,
  `type` int(2) default NULL,
  `expiredTime` bigint(20) default NULL,
  `specialIncrPlanId` varchar(12) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

/*Table structure for table `fashion_item` */

DROP TABLE IF EXISTS `fashion_item`;

CREATE TABLE `fashion_item` (
  `id` varchar(128) NOT NULL,
  `type` int(2) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `state` int(2) NOT NULL default '0',
  `buyTime` bigint(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gamble_hotheroplan` */

DROP TABLE IF EXISTS `gamble_hotheroplan`;

CREATE TABLE `gamble_hotheroplan` (
  `dbkey` varchar(255) character set utf8 NOT NULL,
  `dbvalue` longtext character set utf8 NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `gamble_record` */

DROP TABLE IF EXISTS `gamble_record`;

CREATE TABLE `gamble_record` (
  `dbkey` varchar(255) character set utf8 NOT NULL,
  `dbvalue` longtext character set utf8 NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `game_notice` */

DROP TABLE IF EXISTS `game_notice`;

CREATE TABLE `game_notice` (
  `noticeId` int(11) NOT NULL auto_increment,
  `title` varchar(255) default NULL,
  `content` longtext,
  `startTime` bigint(20) default NULL,
  `endTime` bigint(20) default NULL,
  PRIMARY KEY  (`noticeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `game_world` */

DROP TABLE IF EXISTS `game_world`;

CREATE TABLE `game_world` (
  `dbkey` varchar(128) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gc_fight_record` */

DROP TABLE IF EXISTS `gc_fight_record`;

CREATE TABLE `gc_fight_record` (
  `dbKey` varchar(255) NOT NULL,
  `dbValue` longtext NOT NULL,
  PRIMARY KEY  (`dbKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gc_quiz_event` */

DROP TABLE IF EXISTS `gc_quiz_event`;

CREATE TABLE `gc_quiz_event` (
  `dbKey` varchar(255) NOT NULL default '',
  `dbValue` text NOT NULL,
  PRIMARY KEY  (`dbKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gc_quiz_item` */

DROP TABLE IF EXISTS `gc_quiz_item`;

CREATE TABLE `gc_quiz_item` (
  `id` varchar(128) NOT NULL default '',
  `userID` varchar(128) NOT NULL default '',
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userID` USING BTREE (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gc_rank_data` */

DROP TABLE IF EXISTS `gc_rank_data`;

CREATE TABLE `gc_rank_data` (
  `dbkey` varchar(64) NOT NULL default '',
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gf_bidding_item` */

DROP TABLE IF EXISTS `gf_bidding_item`;

CREATE TABLE `gf_bidding_item` (
  `biddingID` varchar(128) NOT NULL default '',
  `resourceID` varchar(128) NOT NULL default '',
  `extention` text NOT NULL,
  PRIMARY KEY  (`biddingID`),
  KEY `resourceID` USING BTREE (`resourceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gf_defend_army_item` */

DROP TABLE IF EXISTS `gf_defend_army_item`;

CREATE TABLE `gf_defend_army_item` (
  `armyID` varchar(128) NOT NULL default '',
  `groupID` varchar(128) NOT NULL default '',
  `extention` text NOT NULL,
  PRIMARY KEY  (`armyID`),
  KEY `groupID` USING BTREE (`groupID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gf_final_reward_item` */

DROP TABLE IF EXISTS `gf_final_reward_item`;

CREATE TABLE `gf_final_reward_item` (
  `rewardID` varchar(128) NOT NULL COMMENT 'resourceID_userID_rewardType',
  `userID` varchar(128) NOT NULL COMMENT 'resourceID_userID',
  `extention` text,
  PRIMARY KEY  (`rewardID`),
  KEY `userId` USING BTREE (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gf_group_data` */

DROP TABLE IF EXISTS `gf_group_data`;

CREATE TABLE `gf_group_data` (
  `dbkey` int(11) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gf_resource_data` */

DROP TABLE IF EXISTS `gf_resource_data`;

CREATE TABLE `gf_resource_data` (
  `dbkey` int(11) NOT NULL,
  `dbvalue` text,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `gift_code` */

DROP TABLE IF EXISTS `gift_code`;

CREATE TABLE `gift_code` (
  `code` varchar(32) NOT NULL default '' COMMENT '兑换码',
  `userId` varchar(32) default '' COMMENT '兑换的角色Id',
  `useTime` bigint(32) default '0' COMMENT '使用时间',
  `giftId` int(16) default '0' COMMENT '兑换的礼包Id',
  PRIMARY KEY  (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `globaldata` */

DROP TABLE IF EXISTS `globaldata`;

CREATE TABLE `globaldata` (
  `serverId` int(11) NOT NULL auto_increment,
  `gulidIndex` int(10) default NULL,
  PRIMARY KEY  (`serverId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_copy_item_drop_apply_record` */

DROP TABLE IF EXISTS `group_copy_item_drop_apply_record`;

CREATE TABLE `group_copy_item_drop_apply_record` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` longtext NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_copy_level_item` */

DROP TABLE IF EXISTS `group_copy_level_item`;

CREATE TABLE `group_copy_level_item` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_copy_map_item` */

DROP TABLE IF EXISTS `group_copy_map_item`;

CREATE TABLE `group_copy_map_item` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_copy_reward_record` */

DROP TABLE IF EXISTS `group_copy_reward_record`;

CREATE TABLE `group_copy_reward_record` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_data` */

DROP TABLE IF EXISTS `group_data`;

CREATE TABLE `group_data` (
  `id` varchar(64) NOT NULL default '' COMMENT '帮派Id',
  `createUserId` varchar(128) NOT NULL default '' COMMENT '创建帮派的角色Id',
  `createTime` bigint(21) default '0' COMMENT '帮派创建的时间',
  `groupLevel` tinyint(2) default '0' COMMENT '创建帮派的等级',
  `groupExp` int(16) default '0' COMMENT '帮派的经验',
  `groupState` tinyint(2) default '0' COMMENT '帮派状态',
  `groupName` varchar(64) NOT NULL default '' COMMENT '帮派名字',
  `iconId` varchar(32) NOT NULL default '' COMMENT '帮派图标',
  `supplies` int(21) default '0' COMMENT '帮派物资',
  `announcement` varchar(1024) NOT NULL default '' COMMENT '帮派公告',
  `declaration` varchar(1024) NOT NULL default '' COMMENT '帮派宣言',
  `validateType` tinyint(2) default '0' COMMENT '帮派验证类型',
  `applyLevel` smallint(15) default '0' COMMENT '帮派等级',
  `toLevelTime` bigint(21) default '0' COMMENT '到达等级的时间',
  `dismissTime` bigint(21) default '0' COMMENT '帮派申请解散的时间',
  `researchSkillMap` text COMMENT '帮派学习研究的技能列表',
  `token` int(16) default '0' COMMENT '令牌数量',
  `daySupplies` int(16) default '0' COMMENT '每日获取物资上限',
  `dayExp` int(16) default '0' COMMENT '每日获取经验上限',
  `updateLimitTime` bigint(21) default '0' COMMENT '更新上限时间',
  PRIMARY KEY  (`id`),
  UNIQUE KEY `groupName` (`groupName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_identifier` */

DROP TABLE IF EXISTS `group_identifier`;

CREATE TABLE `group_identifier` (
  `id` int(11) NOT NULL auto_increment COMMENT '帮派的自增',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_log` */

DROP TABLE IF EXISTS `group_log`;

CREATE TABLE `group_log` (
  `dbkey` varchar(128) NOT NULL default '' COMMENT '存储的Key',
  `dbvalue` text COMMENT '存储的内容',
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `group_member` */

DROP TABLE IF EXISTS `group_member`;

CREATE TABLE `group_member` (
  `id` varchar(128) NOT NULL default '' COMMENT '成员Id',
  `userId` varchar(128) NOT NULL default '' COMMENT '成员对应的UserId',
  `groupId` varchar(64) NOT NULL default '' COMMENT '帮派成员Id',
  `post` tinyint(2) default '0' COMMENT '成员职位',
  `logoutTime` bigint(21) default '0' COMMENT '登出游戏的时间',
  `name` varchar(64) NOT NULL default '' COMMENT '成员的名字',
  `level` smallint(3) default '0' COMMENT '成员等级',
  `headbox` varchar(32) default NULL,
  `headId` varchar(32) NOT NULL default '' COMMENT '成员头像Id',
  `vipLevel` tinyint(2) default '0' COMMENT '成员的Vip等级',
  `job` tinyint(2) default '0' COMMENT '成员的职业',
  `contribution` int(10) default '0' COMMENT '个人的贡献',
  `totalContribution` int(11) default '0' COMMENT '个人的总捐献',
  `fighting` int(10) default '0' COMMENT '战斗力',
  `applyTime` bigint(21) default '0' COMMENT '申请加入帮派时间',
  `receiveTime` bigint(21) default '0' COMMENT '接受加入帮派的时间',
  `templateId` varchar(28) NOT NULL default '' COMMENT '成员的模版Id',
  `dayContribution` int(16) default '0' COMMENT '每日从令牌捐献获取的捐献',
  `allotRewardCount` tinyint(2) default '0' COMMENT '帮派管理员每日分配物品次数',
  `armyFashion` varchar(256) default NULL COMMENT '成员时装',
  `extention` text COMMENT '帮派成员的扩展数据',
  PRIMARY KEY  (`id`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero` */

DROP TABLE IF EXISTS `hero`;

CREATE TABLE `hero` (
  `id` varchar(64) NOT NULL COMMENT '英雄唯一的id',
  `user_id` varchar(64) default '' COMMENT '英雄所属的玩家的userId，如果英雄数据为玩家的主英雄，此字段的值为EMPTY',
  `name` varchar(64) default NULL COMMENT '英雄的名字',
  `template_id` varchar(64) default NULL COMMENT '英雄的模板id',
  `hero_type` mediumint(9) default NULL COMMENT '英雄的类型',
  `level` int(11) default NULL COMMENT '英雄的等级',
  `exp` int(11) default NULL COMMENT '英雄的经验',
  `attribute` varchar(4096) default NULL COMMENT '英雄的自定义属性',
  `create_time` bigint(64) default NULL COMMENT '英雄创建的时间',
  PRIMARY KEY  (`id`),
  KEY `user_id` USING BTREE (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_comment_item` */

DROP TABLE IF EXISTS `hero_comment_item`;

CREATE TABLE `hero_comment_item` (
  `id` varchar(128) NOT NULL,
  `heroId` varchar(128) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property00` */

DROP TABLE IF EXISTS `hero_extended_property00`;

CREATE TABLE `hero_extended_property00` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property01` */

DROP TABLE IF EXISTS `hero_extended_property01`;

CREATE TABLE `hero_extended_property01` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property02` */

DROP TABLE IF EXISTS `hero_extended_property02`;

CREATE TABLE `hero_extended_property02` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property03` */

DROP TABLE IF EXISTS `hero_extended_property03`;

CREATE TABLE `hero_extended_property03` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property04` */

DROP TABLE IF EXISTS `hero_extended_property04`;

CREATE TABLE `hero_extended_property04` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property05` */

DROP TABLE IF EXISTS `hero_extended_property05`;

CREATE TABLE `hero_extended_property05` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property06` */

DROP TABLE IF EXISTS `hero_extended_property06`;

CREATE TABLE `hero_extended_property06` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property07` */

DROP TABLE IF EXISTS `hero_extended_property07`;

CREATE TABLE `hero_extended_property07` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property08` */

DROP TABLE IF EXISTS `hero_extended_property08`;

CREATE TABLE `hero_extended_property08` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property09` */

DROP TABLE IF EXISTS `hero_extended_property09`;

CREATE TABLE `hero_extended_property09` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property10` */

DROP TABLE IF EXISTS `hero_extended_property10`;

CREATE TABLE `hero_extended_property10` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property11` */

DROP TABLE IF EXISTS `hero_extended_property11`;

CREATE TABLE `hero_extended_property11` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property12` */

DROP TABLE IF EXISTS `hero_extended_property12`;

CREATE TABLE `hero_extended_property12` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property13` */

DROP TABLE IF EXISTS `hero_extended_property13`;

CREATE TABLE `hero_extended_property13` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property14` */

DROP TABLE IF EXISTS `hero_extended_property14`;

CREATE TABLE `hero_extended_property14` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hero_extended_property15` */

DROP TABLE IF EXISTS `hero_extended_property15`;

CREATE TABLE `hero_extended_property15` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item00` */

DROP TABLE IF EXISTS `item00`;

CREATE TABLE `item00` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item01` */

DROP TABLE IF EXISTS `item01`;

CREATE TABLE `item01` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item02` */

DROP TABLE IF EXISTS `item02`;

CREATE TABLE `item02` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item03` */

DROP TABLE IF EXISTS `item03`;

CREATE TABLE `item03` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item04` */

DROP TABLE IF EXISTS `item04`;

CREATE TABLE `item04` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item05` */

DROP TABLE IF EXISTS `item05`;

CREATE TABLE `item05` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item06` */

DROP TABLE IF EXISTS `item06`;

CREATE TABLE `item06` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item07` */

DROP TABLE IF EXISTS `item07`;

CREATE TABLE `item07` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item08` */

DROP TABLE IF EXISTS `item08`;

CREATE TABLE `item08` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `item09` */

DROP TABLE IF EXISTS `item09`;

CREATE TABLE `item09` (
  `id` varchar(64) NOT NULL default '' COMMENT '道具数据库Id(userId|id)',
  `modelId` int(11) default NULL COMMENT '道具模版Id',
  `count` int(4) default NULL COMMENT '道具数量',
  `userId` varchar(64) default '' COMMENT '角色Id',
  `allExtendAttr` varchar(256) default '' COMMENT '扩展属性(json数据)',
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `log_store` */

DROP TABLE IF EXISTS `log_store`;

CREATE TABLE `log_store` (
  `dbkey` varchar(64) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `logger_record` */

DROP TABLE IF EXISTS `logger_record`;

CREATE TABLE `logger_record` (
  `id` bigint(20) NOT NULL auto_increment,
  `info` mediumtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `login_log` */

DROP TABLE IF EXISTS `login_log`;

CREATE TABLE `login_log` (
  `id` bigint(20) NOT NULL auto_increment,
  `userId` varchar(128) default NULL,
  `account` varchar(128) default NULL,
  `loginTime` bigint(20) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `magic` */

DROP TABLE IF EXISTS `magic`;

CREATE TABLE `magic` (
  `id` varchar(64) NOT NULL default '' COMMENT '佣兵的Id',
  `magicId` varchar(64) default '' COMMENT '佣兵法宝的Id',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `magic_chapter_info` */

DROP TABLE IF EXISTS `magic_chapter_info`;

CREATE TABLE `magic_chapter_info` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `magic_equip_fetter_record` */

DROP TABLE IF EXISTS `magic_equip_fetter_record`;

CREATE TABLE `magic_equip_fetter_record` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(64) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `majordata` */

DROP TABLE IF EXISTS `majordata`;

CREATE TABLE `majordata` (
  `id` varchar(128) NOT NULL,
  `coin` bigint(20) default NULL,
  `gold` int(11) default NULL,
  `giftGold` int(11) default NULL,
  `chargeGold` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `map` */

DROP TABLE IF EXISTS `map`;

CREATE TABLE `map` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_table_worship` */

DROP TABLE IF EXISTS `mt_table_worship`;

CREATE TABLE `mt_table_worship` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_unique_name` */

DROP TABLE IF EXISTS `mt_unique_name`;

CREATE TABLE `mt_unique_name` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `mt_user_purse` */

DROP TABLE IF EXISTS `mt_user_purse`;

CREATE TABLE `mt_user_purse` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `newguide_give_item_history` */

DROP TABLE IF EXISTS `newguide_give_item_history`;

CREATE TABLE `newguide_give_item_history` (
  `storeId` varchar(64) character set utf8 NOT NULL,
  `userId` varchar(64) character set utf8 default NULL,
  `giveActionId` int(12) default NULL,
  `given` int(2) default NULL,
  PRIMARY KEY  (`storeId`),
  UNIQUE KEY `storeId_UNIQUE` USING BTREE (`storeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

/*Table structure for table `peak_arena_data` */

DROP TABLE IF EXISTS `peak_arena_data`;

CREATE TABLE `peak_arena_data` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `peak_arena_info` */

DROP TABLE IF EXISTS `peak_arena_info`;

CREATE TABLE `peak_arena_info` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `peak_arena_record` */

DROP TABLE IF EXISTS `peak_arena_record`;

CREATE TABLE `peak_arena_record` (
  `dbkey` varchar(255) character set utf8 collate utf8_bin NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `random_boss_record` */

DROP TABLE IF EXISTS `random_boss_record`;

CREATE TABLE `random_boss_record` (
  `dbkey` varchar(128) NOT NULL default '',
  `dbvalue` mediumtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ranking` */

DROP TABLE IF EXISTS `ranking`;

CREATE TABLE `ranking` (
  `ranking_sequence` bigint(20) NOT NULL COMMENT '排行榜primary_key的唯一性约束在内存控制',
  `primary_key` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL COMMENT '排行榜数据库是批量操作，因为唯一性约束在内存控制',
  `conditions` varchar(255) NOT NULL,
  `extension` text NOT NULL COMMENT '排行榜数据库是批量操作，因为唯一性约束在内存控制',
  PRIMARY KEY  (`ranking_sequence`),
  KEY `type` USING BTREE (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `ranking_swap` */

DROP TABLE IF EXISTS `ranking_swap`;

CREATE TABLE `ranking_swap` (
  `id` bigint(20) NOT NULL auto_increment,
  `primary_key` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL,
  `ranking` int(11) NOT NULL,
  `extension` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property00` */

DROP TABLE IF EXISTS `role_extended_property00`;

CREATE TABLE `role_extended_property00` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property01` */

DROP TABLE IF EXISTS `role_extended_property01`;

CREATE TABLE `role_extended_property01` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property02` */

DROP TABLE IF EXISTS `role_extended_property02`;

CREATE TABLE `role_extended_property02` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property03` */

DROP TABLE IF EXISTS `role_extended_property03`;

CREATE TABLE `role_extended_property03` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property04` */

DROP TABLE IF EXISTS `role_extended_property04`;

CREATE TABLE `role_extended_property04` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property05` */

DROP TABLE IF EXISTS `role_extended_property05`;

CREATE TABLE `role_extended_property05` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property06` */

DROP TABLE IF EXISTS `role_extended_property06`;

CREATE TABLE `role_extended_property06` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property07` */

DROP TABLE IF EXISTS `role_extended_property07`;

CREATE TABLE `role_extended_property07` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property08` */

DROP TABLE IF EXISTS `role_extended_property08`;

CREATE TABLE `role_extended_property08` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `role_extended_property09` */

DROP TABLE IF EXISTS `role_extended_property09`;

CREATE TABLE `role_extended_property09` (
  `id` bigint(20) NOT NULL auto_increment,
  `owner_id` varchar(36) NOT NULL,
  `type` smallint(6) NOT NULL,
  `sub_type` int(10) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `ownerId` (`owner_id`,`type`,`sub_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea` */

DROP TABLE IF EXISTS `secretarea`;

CREATE TABLE `secretarea` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea_battleinfo` */

DROP TABLE IF EXISTS `secretarea_battleinfo`;

CREATE TABLE `secretarea_battleinfo` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea_def_record` */

DROP TABLE IF EXISTS `secretarea_def_record`;

CREATE TABLE `secretarea_def_record` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) default NULL,
  `secretId` varchar(64) default NULL,
  `secretType` varchar(32) default NULL,
  `attrackUserId` varchar(64) default NULL,
  `guildName` varchar(32) default NULL,
  `regiondName` varchar(32) default NULL,
  `attrackUserName` varchar(32) default NULL,
  `attrackTime` varchar(64) default NULL,
  `attrackCount` int(32) default NULL,
  `isWin` int(32) default NULL,
  `keyNum` int(32) default NULL,
  `sourceNumList` varchar(128) default NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea_info` */

DROP TABLE IF EXISTS `secretarea_info`;

CREATE TABLE `secretarea_info` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea_user_record` */

DROP TABLE IF EXISTS `secretarea_user_record`;

CREATE TABLE `secretarea_user_record` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) default NULL,
  `secretType` varchar(32) default NULL,
  `status` int(32) default NULL,
  `getGiftTime` varchar(64) default NULL,
  `userSourceList` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretarea_userinfo` */

DROP TABLE IF EXISTS `secretarea_userinfo`;

CREATE TABLE `secretarea_userinfo` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `secretmember` */

DROP TABLE IF EXISTS `secretmember`;

CREATE TABLE `secretmember` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `server_common_data` */

DROP TABLE IF EXISTS `server_common_data`;

CREATE TABLE `server_common_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `server_data` */

DROP TABLE IF EXISTS `server_data`;

CREATE TABLE `server_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `server_gm_email` */

DROP TABLE IF EXISTS `server_gm_email`;

CREATE TABLE `server_gm_email` (
  `id` int(255) NOT NULL auto_increment,
  `sendToAllEmailData` text,
  `conditionList` text,
  `status` int(8) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `server_gm_notice` */

DROP TABLE IF EXISTS `server_gm_notice`;

CREATE TABLE `server_gm_notice` (
  `id` int(255) NOT NULL auto_increment,
  `noticeInfo` text,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `server_gmemail` */

DROP TABLE IF EXISTS `server_gmemail`;

CREATE TABLE `server_gmemail` (
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `server_group_copy_damage_record` */

DROP TABLE IF EXISTS `server_group_copy_damage_record`;

CREATE TABLE `server_group_copy_damage_record` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` longtext NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`extention`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `seven_day_gifinfo` */

DROP TABLE IF EXISTS `seven_day_gifinfo`;

CREATE TABLE `seven_day_gifinfo` (
  `dbkey` varchar(50) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `special_account` */

DROP TABLE IF EXISTS `special_account`;

CREATE TABLE `special_account` (
  `account` varchar(40) NOT NULL default '' COMMENT '账户Id(渠道Id_账户)',
  `loadKey` int(3) NOT NULL default '1' COMMENT '用于MapItemStore统一加载的Key',
  PRIMARY KEY  (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata00` */

DROP TABLE IF EXISTS `table_kvdata00`;

CREATE TABLE `table_kvdata00` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata01` */

DROP TABLE IF EXISTS `table_kvdata01`;

CREATE TABLE `table_kvdata01` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata02` */

DROP TABLE IF EXISTS `table_kvdata02`;

CREATE TABLE `table_kvdata02` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata03` */

DROP TABLE IF EXISTS `table_kvdata03`;

CREATE TABLE `table_kvdata03` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata04` */

DROP TABLE IF EXISTS `table_kvdata04`;

CREATE TABLE `table_kvdata04` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata05` */

DROP TABLE IF EXISTS `table_kvdata05`;

CREATE TABLE `table_kvdata05` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata06` */

DROP TABLE IF EXISTS `table_kvdata06`;

CREATE TABLE `table_kvdata06` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata07` */

DROP TABLE IF EXISTS `table_kvdata07`;

CREATE TABLE `table_kvdata07` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata08` */

DROP TABLE IF EXISTS `table_kvdata08`;

CREATE TABLE `table_kvdata08` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `table_kvdata09` */

DROP TABLE IF EXISTS `table_kvdata09`;

CREATE TABLE `table_kvdata09` (
  `id` bigint(20) NOT NULL auto_increment,
  `dbkey` varchar(36) NOT NULL,
  `dbvalue` mediumtext NOT NULL,
  `type` mediumint(9) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `dbkey` (`dbkey`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `task_item` */

DROP TABLE IF EXISTS `task_item`;

CREATE TABLE `task_item` (
  `id` varchar(64) NOT NULL,
  `userId` varchar(64) NOT NULL,
  `extention` longtext,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tb_team_item` */

DROP TABLE IF EXISTS `tb_team_item`;

CREATE TABLE `tb_team_item` (
  `teamID` varchar(128) NOT NULL,
  `hardID` varchar(128) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`teamID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `tower_data` */

DROP TABLE IF EXISTS `tower_data`;

CREATE TABLE `tower_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `unending_war` */

DROP TABLE IF EXISTS `unending_war`;

CREATE TABLE `unending_war` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `upgrade_data` */

DROP TABLE IF EXISTS `upgrade_data`;

CREATE TABLE `upgrade_data` (
  `dbkey` varchar(255) NOT NULL default '',
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `userId` varchar(40) NOT NULL,
  `openAccount` varchar(128) NOT NULL,
  `zoneId` int(11) NOT NULL,
  `mapId` int(7) default NULL,
  `userName` varchar(32) default '',
  `vip` int(4) default NULL,
  `sex` int(2) default NULL,
  `exp` bigint(11) default NULL,
  `level` int(4) default NULL,
  `account` varchar(64) NOT NULL,
  `headImage` varchar(128) default '',
  `createTime` bigint(20) default '0',
  `lastLoginTime` bigint(20) default '0',
  `kickOffCoolTime` bigint(20) NOT NULL,
  `zoneRegInfo` varchar(4096) default NULL,
  `extendInfo` varchar(4096) default NULL,
  `isRobot` tinyint(1) default '0' COMMENT '是否是机器人',
  `zoneLoginInfo` varchar(4096) default NULL,
  `channelId` varchar(255) default NULL,
  PRIMARY KEY  (`userId`),
  UNIQUE KEY `account` USING BTREE (`account`,`zoneId`),
  UNIQUE KEY `userName` USING BTREE (`userName`),
  KEY `openAccount` USING BTREE (`openAccount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_army_data` */

DROP TABLE IF EXISTS `user_army_data`;

CREATE TABLE `user_army_data` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_comment_item` */

DROP TABLE IF EXISTS `user_comment_item`;

CREATE TABLE `user_comment_item` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(128) NOT NULL,
  `extention` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_group_attribute` */

DROP TABLE IF EXISTS `user_group_attribute`;

CREATE TABLE `user_group_attribute` (
  `userId` varchar(128) NOT NULL default '' COMMENT '角色Id',
  `groupId` varchar(64) NOT NULL default '' COMMENT '帮派Id',
  `quitGroupTime` bigint(21) default '0' COMMENT '退出帮派的时间',
  `sendEmailTime` bigint(21) default '0' COMMENT '发送邮件的时间',
  `applyGroupIdList` text COMMENT '申请列表',
  `groupApplySize` int(5) default NULL COMMENT '已经申请的帮派的数量',
  `lastResetApplyTime` bigint(21) default NULL COMMENT '上次重置申请队列长度的时间',
  `studySkill` text COMMENT '个人学习到的帮派技能',
  `contribution` int(10) default '0' COMMENT '个人的贡献',
  `donateTimes` int(5) default '0' COMMENT '捐献的次数',
  `lastDonateTime` bigint(21) default '0' COMMENT '上次捐献的时间',
  `extention` text COMMENT '帮派的扩展数据',
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_group_copy_map_record` */

DROP TABLE IF EXISTS `user_group_copy_map_record`;

CREATE TABLE `user_group_copy_map_record` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(64) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_guide_progress` */

DROP TABLE IF EXISTS `user_guide_progress`;

CREATE TABLE `user_guide_progress` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_identifier` */

DROP TABLE IF EXISTS `user_identifier`;

CREATE TABLE `user_identifier` (
  `id` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_other` */

DROP TABLE IF EXISTS `user_other`;

CREATE TABLE `user_other` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` longtext NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_plot_progress` */

DROP TABLE IF EXISTS `user_plot_progress`;

CREATE TABLE `user_plot_progress` (
  `dbkey` varchar(255) NOT NULL,
  `dbvalue` text NOT NULL,
  PRIMARY KEY  (`dbkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
