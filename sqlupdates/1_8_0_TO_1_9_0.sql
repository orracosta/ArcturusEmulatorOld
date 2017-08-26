#DATABASE UPDATE: 1.8.0 -> 1.9.0

ALTER TABLE  `permissions` ADD  `cmd_empty_bots` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty`;
ALTER TABLE  `permissions` ADD  `cmd_empty_pets` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1' AFTER  `cmd_empty_bots`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.succes.cmd_empty_bots.cleared', 'Bots inventory cleared!'),
    ('commands.succes.cmd_empty_bots.verify', 'This will remove all bots from your inventory. Type :emptybots %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_bots', 'emptybots;empty_bots;deletebots'),
    ('commands.description.cmd_empty_bots', ':emptybots'),
    ('commands.succes.cmd_empty_pets.cleared', 'Pets inventory cleared!'),
    ('commands.succes.cmd_empty_pets.verify', 'This will remove all pets from your inventory. Type :emptypets %generic.yes% to continue!'),
    ('commands.keys.cmd_empty_pets', 'emptypets;empty_pets;deletepets'),
    ('commands.description.cmd_empty_pets', ':emptypets');

CREATE TABLE `users_effects` (
    `user_id` INT NOT NULL,
    `effect` INT(5) NOT NULL,
    `duration` INT NOT NULL DEFAULT '86400',
    `activation_timestamp` INT NOT NULL DEFAULT '-1',
    `total` INT(2) NOT NULL DEFAULT '1',
    UNIQUE(user_id, effect)
) ENGINE = MyISAM;

ALTER TABLE  `users_settings` CHANGE  `talent_track_citizenship_level`  `talent_track_citizenship_level` INT( 2 ) NOT NULL DEFAULT  '-1',
CHANGE  `talent_track_helpers_level`  `talent_track_helpers_level` INT( 2 ) NOT NULL DEFAULT  '-1';
UPDATE users_settings SET talent_track_citizenship_level = -1;
UPDATE users_settings SET talent_track_helpers_level = -1;
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.room.tags.staff', 'staff;official;habbo');
ALTER TABLE  `permissions` ADD  `acc_room_staff_tags` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

CREATE TABLE  `catalog_club_offers` (
    `id` INT NOT NULL DEFAULT NULL AUTO_INCREMENT PRIMARY KEY ,
    `enabled` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '1',
    `name` VARCHAR( 35 ) NOT NULL ,
    `days` INT( 7 ) NOT NULL ,
    `credits` INT( 5 ) NOT NULL DEFAULT  '10',
    `points` INT( 5 ) NOT NULL DEFAULT  '0',
    `points_type` INT( 3 ) NOT NULL DEFAULT  '0',
    `type` ENUM(  'HC',  'VIP' ) NOT NULL DEFAULT  'HC',
    `deal` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0'
) ENGINE = MYISAM ;

INSERT INTO `catalog_club_offers` (`id`, `enabled`, `name`, `days`, `credits`, `points`, `points_type`, `type`, `deal`) VALUES
    (NULL, '1', 'HABBO_CLUB_7_DAY', '7', '5', '0', '0', 'VIP', '0'),
    (NULL, '1', 'HABBO_CLUB_1_MONTH', '31', '20', '0', '0', 'VIP', '0');

UPDATE catalog_pages SET page_layout = 'vip_buy' WHERE page_layout = 'club_buy';

ALTER TABLE  `support_cfh_topics` ADD  `ignore_target` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `action`;

ALTER TABLE  `users_settings` ADD  `nux` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';
INSERT INTO `emulator_texts` (`key`, `value`) VALUES
('nux.step.1', 'This is the reception button, it will take you to the hotelview.'),
('nux.step.2', 'This is the navigator button, from here you can visit other rooms.'),
('nux.step.3', 'To talk, just enter your message here.'),
('nux.step.4', 'The chat history window lets you read back your previous conversations.'),
('nux.step.5', 'If you want to change your looks, you can use the wardrobe.'),
('nux.step.6', 'In the catalog you can purchase furniture to decorate your own room.'),
('nux.step.7', 'Furniture can be bought with a combination of credits...'),
('nux.step.8', '...duckets,'), ('nux.step.9', 'or diamonds.'),
('nux.step.10', 'All your friends are located down here.'),
('nux.step.11', 'Lets have some fun and start exploring the hotel!');
ALTER TABLE  `commandlogs` CHANGE  `params`  `params` VARCHAR( 256 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  '';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('alert.superwired.invalid', 'Invalid superwired configuration. Make sure to NOT use <b>;</b> in the product field!');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.navigator.popular.category.maxresults', '10');

ALTER TABLE  `navigator_flatcats` ADD  `caption_save` VARCHAR( 32 ) NOT NULL DEFAULT  'caption_save' AFTER  `min_rank`;
CREATE TABLE  `users_navigator_settings` (
    `user_id` INT NOT NULL ,
    `caption` VARCHAR( 128 ) NOT NULL ,
    `list_type` ENUM(  'list',  'thumbnails' ) NOT NULL DEFAULT  'list',
    `display` ENUM(  'visible',  'collapsed' ) NOT NULL DEFAULT  'visible'
) ENGINE = MYISAM ;

UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_building' WHERE  `navigator_flatcats`.`id` =2;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_chat' WHERE  `navigator_flatcats`.`id` =3;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_fansite' WHERE  `navigator_flatcats`.`id` =4;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_games' WHERE  `navigator_flatcats`.`id` =5;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_help' WHERE  `navigator_flatcats`.`id` =6;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_life' WHERE  `navigator_flatcats`.`id` =7;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_official' WHERE  `navigator_flatcats`.`id` =8;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_party' WHERE  `navigator_flatcats`.`id` =9;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_personal' WHERE  `navigator_flatcats`.`id` =10;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_reviews' WHERE  `navigator_flatcats`.`id` =11;
UPDATE  `navigator_flatcats` SET  `caption_save` =  'caption_save_trading' WHERE  `navigator_flatcats`.`id` =12;

ALTER TABLE  `permissions` ADD  `cmd_allow_trading` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_alert`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
('commands.description.cmd_allow_trading', ":blocktrading - Enables / Disables the tradelock for a user."),
('commands.keys.cmd_allow_trading', 'tradelock;blocktrading;disabletrade'),
('commands.error.cmd_allow_trading.forgot_username', 'Please specify the user to enable / disable trading for.'),
('commands.error.cmd_allow_trading.forgot_trade', 'Please specify if you want to enable trading for %username%.'),
('commands.succes.cmd_allow_trading.enabled', 'Trading has been enabled for %username%!'),
('commands.succes.cmd_allow_trading.disabled', 'Trading has been disabled for %username%!'),
('commands.error.cmd_allow_trading.user_not_found', 'Target Habbo does not exist.'),
('commands.error.cmd_allow_trading.incorrect_setting', 'Please use %enabled% to enable trading. Use %disabled% to disable trading.');

ALTER TABLE  `rooms` CHANGE  `allow_walkthrough`  `allow_walkthrough` ENUM(  '0',  '1' ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '1';

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('debug.show.users', '1');

#This is work in progress.
CREATE TABLE IF NOT EXISTS `nux_gifts` (
  `id` int(3) NOT NULL AUTO_INCREMENT,
  `type` enum('item','room') NOT NULL DEFAULT 'item',
  `value` varchar(32) NOT NULL COMMENT 'If type item then items.item_name. If type room then room id to copy.',
  `image` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=0 ;

INSERT INTO `nux_gifts` (`id`, `type`, `value`, `image`) VALUES
(1, 'item', 'rare_daffodil_rug', 'notifications/rare_daffodil_rug.png'),
(2, 'item', 'rare_moonrug', 'notifications/rare_moonrug.png'),
(3, 'item', 'sandrug', 'notifications/sandrug.png');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('room.chat.delay', '0');
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('room.chat.prefix.format', '[<font color="%color%">%prefix%</font>] ');

ALTER TABLE  `permissions` ADD  `cmd_roommute` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `cmd_roomitem`;
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('camera.use.https', '1');

ALTER TABLE  `permissions` ADD  `prefix` VARCHAR( 5 ) NOT NULL AFTER  `log_commands` ,
                           ADD  `prefix_color` VARCHAR( 7 ) NOT NULL AFTER  `prefix`;

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_give_rank.higher.other', '%username% has an higher rank than you and thus cannot change it!');
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.error.cmd_unmute.not_muted', '%user% is not muted.');

ALTER TABLE  `navigator_filter` ADD PRIMARY KEY (  `key` );
ALTER TABLE  `navigator_filter` CHANGE  `database_query`  `database_query` VARCHAR( 1024 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL;
UPDATE  `navigator_filter` SET  `compare` =  'contains' WHERE  `navigator_filter`.`key` =  'roomname';
UPDATE `navigator_filter` SET `database_query` = 'SELECT * FROM rooms WHERE name COLLATE UTF8_GENERAL_CI LIKE ? ', `compare` = 'contains' WHERE `navigator_filter`.`key` = 'roomname';
UPDATE `navigator_filter` SET `database_query` = 'SELECT rooms.*, CONCAT_WS(rooms.owner_name, rooms.name, rooms.description, rooms.tags, guilds.name, guilds.description) as whole FROM rooms LEFT JOIN guilds ON rooms.guild_id = guilds.id HAVING whole LIKE ? ' WHERE `navigator_filter`.`key` = 'anything';
ALTER TABLE  `users_settings` ADD  `mute_end_timestamp` INT( 11 ) NOT NULL DEFAULT  '0' AFTER  `nux`;
ALTER TABLE  `wordfilter` ADD  `mute` INT( 3 ) NOT NULL DEFAULT  '0' COMMENT  'Time user gets muted for mentioning this word.' AFTER  `report`;

#END DATABASE UPDATE: 1.7.0 -> 1.8.0