CREATE TABLE `server_group_copy_damage_record` (
  `id` varchar(128) NOT NULL,
  `groupId` varchar(64) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `groupId` USING BTREE (`extention`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
