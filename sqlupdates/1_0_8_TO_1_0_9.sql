#DATABASE UPDATE: 1.0.8 -> 1.0.9

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_plugins', ':plugins');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_massbadge', ':massbadge <badge>');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_bots', ':update_bots');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.description.cmd_update_navigator', ':update_navigator');

ALTER TABLE  `bans` ADD  `type` ENUM(  'account',  'ip',  'machine', 'super' ) NOT NULL DEFAULT  'account';
ALTER TABLE  `bans` ADD  `ip` VARCHAR( 50 ) NOT NULL DEFAULT  '' AFTER  `user_id`;
ALTER TABLE  `bans` ADD  `machine_id` VARCHAR( 255 ) NOT NULL AFTER  `ip`;
ALTER TABLE  `bans` CHANGE  `type`  `type` ENUM(  'account',  'ip',  'machine',  'super' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  'account' COMMENT  'Account is the entry in the users table banned.
IP is any client that connects with that IP.
Machine is the computer that logged in.
Super is all of the above.';

ALTER TABLE  `permissions` ADD  `cmd_ip_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ha`;
ALTER TABLE  `permissions` ADD  `cmd_machine_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_ip_ban`;
ALTER TABLE  `permissions` ADD  `cmd_super_ban` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_summonrank`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_ip_ban', 'ipban;banip;ip_ban;ban_ip'), ('commands.description.cmd_ip_ban', ':ipban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_machine_ban', 'machineban;banmachine;banmac;macban'), ('commands.description.cmd_machine_ban', ':machineban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_super_ban', 'superban;megaban'), ('commands.description.cmd_super_ban', ':superban <username> [reason]');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_machine_ban', '%count% accounts have been MAC banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_ip_ban', '%count% accounts have been IP banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_super_ban', '%count% accounts have been Super banned!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_ip_ban.ban_self', 'You cannot IP Ban yourself!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_machine_ban.ban_self', 'You cannot issue yourself a MAC Ban!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_super_ban.ban_self', 'You cannot superban yourself!');

ALTER TABLE `items` CHANGE `extra_data` `extra_data` VARCHAR(1024) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('runtime.threads', '8');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.bossgroup.threads', '1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('io.workergroup.threads', '5');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.teleport.locked.allowed', '1');

ALTER TABLE  `users` CHANGE  `ip_register`  `ip_register` VARCHAR( 45 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
                     CHANGE  `ip_current`   `ip_current`  VARCHAR( 45 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL;

ALTER TABLE  `items` CHANGE  `z`  `z` DOUBLE( 10, 5 ) NOT NULL DEFAULT  '0.0';

ALTER TABLE  `catalog_pages` ADD  `includes` VARCHAR( 32 ) NOT NULL DEFAULT  '' COMMENT  'Example usage: 1;2;3
 This will include page 1, 2 and 3 in the current page.
 Note that permissions are only used for the current entry.';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('pathfinder.step.maximum.height', '1.1');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('pathfinder.step.allow.falling', '1');

ALTER TABLE  `guilds`
ADD  `forum` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0',
ADD  `read_forum` ENUM(  'EVERYONE',  'MEMBERS',  'ADMINS' ) NOT NULL DEFAULT  'EVERYONE',
ADD  `post_messages` ENUM(  'EVERYONE',  'MEMBERS',  'ADMINS',  'OWNER' ) NOT NULL DEFAULT  'EVERYONE',
ADD  `post_threads` ENUM(  'EVERYONE',  'MEMBERS',  'ADMINS',  'OWNER' ) NOT NULL DEFAULT  'EVERYONE',
ADD  `mod_forum` ENUM(  'ADMINS',  'OWNER' ) NOT NULL DEFAULT  'ADMINS';

CREATE TABLE `guilds_forums` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `guild_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `subject` mediumtext NOT NULL,
  `message` longtext NOT NULL,
  `state` enum('OPEN','CLOSED','HIDDEN_BY_ADMIN','HIDDEN_BY_STAFF') NOT NULL DEFAULT 'OPEN',
  `admin_id` int(11) NOT NULL DEFAULT '0',
  `pinned` enum('0','1') NOT NULL DEFAULT '0',
  `locked` enum('0','1') NOT NULL DEFAULT '0',
  `timestamp` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

CREATE TABLE `guilds_forums_comments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `thread_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timestamp` int(11) NOT NULL,
  `message` longtext NOT NULL,
  `state` enum('OPEN','CLOSED','HIDDEN_BY_ADMIN','HIDDEN_BY_STAFF') NOT NULL DEFAULT 'OPEN',
  `admin_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=latin1;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_alert.cmd_connect_camera', 'Camera reconnected!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_connect_camera', 'connectcamera;connect_camera;cameraconnect');

ALTER TABLE  `permissions` ADD  `cmd_connect_camera` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_commands`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_disconnect.higher_rank', 'The Habbo you wanted to disconnect is more important than you are.');

ALTER TABLE  `catalog_pages` CHANGE  `page_layout`  `page_layout` ENUM(
'default_3x3',  'club_buy',  'club_gift',  'frontpage',  'spaces',  'recycler',  'recycler_info',
'recycler_prizes',  'trophies',  'marketplace',  'marketplace_own_items',  'pets',
'spaces_new',  'soundmachine',  'guilds',  'guild_furni',  'info_duckets',  'info_rentables',
'info_pets', 'roomads',  'single_bundle',  'sold_ltd_items',  'badge_display',  'bots',  'pets2',
'pets3',  'room_bundle',  'recent_purchases',  'pets2',  'pets3',  'default_3x3_color_grouping',
'guild_forum',  'vip_buy',  'loyalty_info',  'loyalty_vip_buy',  'collectibles' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  'default_3x3';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.rooms.handitem.time', '100');

ALTER TABLE `permissions` ADD  `cmd_diagonal` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_danceall`;
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.keys.cmd_diagonal', 'diagonal;disablediagonal;diagonally'), ('commands.description.cmd_diagonal', ':diagonal');
ALTER TABLE `rooms` ADD  `move_diagonally` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1';
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_diagonal.disabled', 'You can no longer walk diagonally!'), ('commands.succes.cmd_diagonal.disabled', 'You can now walk diagonally!');

CREATE TABLE  `youtube_items` (
`id` INT NOT NULL ,
`video` VARCHAR( 128 ) NOT NULL ,
`title` VARCHAR( 128 ) NOT NULL ,
`description` VARCHAR( 128 ) NOT NULL ,
`start_time` INT( 5 ) NOT NULL DEFAULT  '0',
`end_time` INT( 5 ) NOT NULL DEFAULT  '0'
) ENGINE = MYISAM ;

CREATE TABLE  `youtube_playlists` (
`item_id` INT NOT NULL ,
`video_id` INT NOT NULL ,
`order` INT NOT NULL ,
UNIQUE (
`item_id` ,
`video_id` ,
`order`
)
) ENGINE = MYISAM ;

ALTER TABLE  `youtube_items` ADD PRIMARY KEY (  `id` );
ALTER TABLE  `youtube_items` CHANGE  `id`  `id` INT( 11 ) NOT NULL AUTO_INCREMENT;

INSERT INTO  `emulator_settings` (
`key` ,
`value`
)
VALUES (
'imager.url.youtube',  'imager.php?url=http://img.youtube.com/vi/%video%/default.jpg'
);

ALTER TABLE  `permissions` ADD  `room_effect` INT NOT NULL DEFAULT  '0' AFTER  `rank_name`;

#END DATABASE UPDATE: 1.0.8 -> 1.0.9