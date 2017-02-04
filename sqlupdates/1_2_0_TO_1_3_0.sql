#DATABASE UPDATE: 1.2.0 -> 1.3.0

INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('camera.wait', 'Please wait %seconds% seconds before making another picture.');

ALTER TABLE  `rooms` CHANGE  `name`  `name` VARCHAR( 50 ) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT  '';
ALTER TABLE  `rooms` CHANGE  `description`  `description` VARCHAR( 512 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '';
ALTER TABLE  `rooms` CHANGE  `password`  `password` VARCHAR( 20 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT  '';

UPDATE `items_base` SET `interaction_type` = 'switch' WHERE `item_name` LIKE 'wf_floor_switch%';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.bot.chat.minimum.interval', '5');
DELETE FROM `emulator_settings` WHERE `key` LIKE 'hotel.home.room';
INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.home.room', '0');

ALTER TABLE  `users` CHANGE  `ip_current`  `ip_current` VARCHAR( 45 ) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT  'Have your CMS update this IP. If you do not do this IP banning won''t work!';
INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('loggedin.elsewhere', 'You have been disconnected as you logged in from somewhere else.');

CREATE TABLE  `catalog_featured_pages` (
    `slot_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `image` VARCHAR( 64 ) NOT NULL DEFAULT  '',
    `caption` VARCHAR( 128 ) NOT NULL DEFAULT  '',
    `type` ENUM(  'page_name',  'page_id',  'product_name' ) NOT NULL DEFAULT  'page_name',
    `expire_timestamp` INT NOT NULL DEFAULT  '-1',
    `page_name` VARCHAR( 16 ) NOT NULL DEFAULT  '',
    `page_id` INT NOT NULL DEFAULT  '0',
    `product_name` VARCHAR( 32 ) NOT NULL DEFAULT  ''
) ENGINE = MYISAM ;

INSERT INTO `catalog_featured_pages` (`slot_id`, `image`, `caption`, `type`, `expire_timestamp`, `page_name`, `page_id`, `product_name`) VALUES
('1', 'catalogue/feature_cata_vert_oly16bundle4.png', 'New Olympic 2016 bundle!', 'page_name', '-1', 'olympic_2016', '0', ''),
('2', 'catalogue/feature_cata_hort_habbergerbundle.png', 'Get your own Habbo FASTFOOD restaurant!', 'page_name', '-1', 'fastfood', '0', ''),
('3', 'catalogue/feature_cata_hort_olympic16.png', 'HabboLympix are here!', 'page_name', '-1', 'habbo_lympix', '0', ''),
('4', 'catalogue/feature_cata_hort_HC_b.png', 'Obtain Habbo Club Today!', 'page_name', '-1', 'habbo_club', '0', '');

ALTER TABLE  `users_settings` ADD  `ignore_bots` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0',
ADD  `ignore_pets` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0';

INSERT INTO `emulator_texts` (`key`, `value`) VALUES
    ('commands.keys.cmd_mute_pets', 'mutepets;ignorepets;mute_pets;ignore_pets'),
    ('commands.succes.cmd_mute_pets.ignored', 'You''re now ignoring pets.'),
    ('commands.succes.cmd_mute_pets.unignored', 'You''re no longer ignoring pets.'),
    ('commands.keys.cmd_mute_bots', 'mutebots;ignorebots;mute_bots;ignore_bots'),
    ('commands.succes.cmd_mute_bots.ignored', 'You are now ignoring bots.'),
    ('commands.succes.cmd_mute_bots.unignored', 'You are no longer ignoring bots.');

INSERT INTO `emulator_settings` (`key`, `value`) VALUES ('hotel.daily.respect', '3'), ('hotel.daily.respect.pets', '3'),  ('hotel.refill.daily', '86400');

CREATE TABLE  `commandlogs` (
    `user_id` INT NOT NULL ,
    `timestamp` INT NOT NULL ,
    `command` VARCHAR( 32 ) NOT NULL DEFAULT  '',
    `params` VARCHAR( 100 ) NOT NULL DEFAULT  '',
    `succes` ENUM(  'no',  'yes' ) NOT NULL DEFAULT  'yes',
INDEX (  `user_id` )
) ENGINE = MYISAM ;

ALTER TABLE  `permissions` ADD  `log_commands` ENUM(  '0',  '1' ) NOT NULL DEFAULT  '0' AFTER  `room_effect`;

#END DATABASE UPDATE: 1.2.0 -> 1.3.0