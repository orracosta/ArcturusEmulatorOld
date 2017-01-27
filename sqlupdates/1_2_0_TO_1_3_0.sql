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

#END DATABASE UPDATE: 1.2.0 -> 1.3.0