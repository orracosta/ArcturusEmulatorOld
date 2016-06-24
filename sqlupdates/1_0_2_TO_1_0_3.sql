INSERT INTO `emulator_texts` (`key`, `value`) VALUES ('commands.succes.cmd_bundle', 'Roombundle succesfully created with page id %id%');
ALTER TABLE  `users_settings` CHANGE  `can_trade`  `can_trade` ENUM(  '0',  '1' ) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT  '1';
UPDATE `users_settings` SET `can_trade` = '1';
ALTER TABLE  `rooms` ADD  `trade_mode` INT NOT NULL DEFAULT  '2';