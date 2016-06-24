CREATE TABLE `group_copy_map_item` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`groupId`),
  KEY `groupId` USING BTREE (`extention`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
