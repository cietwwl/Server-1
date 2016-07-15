CREATE TABLE `user_group_copy_map_record` (
  `id` varchar(128) NOT NULL,
  `userId` varchar(64) NOT NULL,
  `extention` varchar(4096) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`extention`(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
